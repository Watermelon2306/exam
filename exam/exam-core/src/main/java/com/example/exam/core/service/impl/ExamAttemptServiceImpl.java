package com.example.exam.core.service.impl;

import com.example.exam.api.dto.*;
import com.example.exam.api.enums.AttemptStatus;
import com.example.exam.core.domain.Answer;
import com.example.exam.core.domain.Attempt;
import com.example.exam.core.model.PaperSnapshot;
import com.example.exam.core.repository.AnswerRepository;
import com.example.exam.core.repository.AttemptRepository;
import com.example.exam.core.repository.ExamRepository;
import com.example.exam.core.service.ExamAttemptService;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final AttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;
    private final ExamRepository examRepository;

    public ExamAttemptServiceImpl(AttemptRepository attemptRepository, AnswerRepository answerRepository,
                                  ExamRepository examRepository) {
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.examRepository = examRepository;
    }

    @Override
    public AttemptStartResponse start(Long userId, AttemptStartRequest request) {
        Optional<Attempt> existing = attemptRepository.findLatestInProgress(request.getExamId(), userId);
        PaperSnapshot snapshot = examRepository.loadPaperSnapshot(request.getExamId())
                .orElseThrow(() -> new IllegalStateException("Exam or paper snapshot not found"));

        Attempt attempt = existing.orElseGet(() -> {
            Attempt fresh = new Attempt();
            fresh.setExamId(request.getExamId());
            fresh.setUserId(userId);
            fresh.setStatus(AttemptStatus.IN_PROGRESS);
            fresh.setCreatedAt(Instant.now());
            fresh.setSnapshotVersion(snapshot.asDto().getVersion());
            return attemptRepository.saveNew(fresh);
        });

        AttemptStartResponse response = new AttemptStartResponse();
        response.setAttemptId(attempt.getId());
        response.setServerTime(Instant.now().getEpochSecond());
        response.setSnapshot(snapshot.asDto());
        return response;
    }

    @Override
    public AttemptStatus saveAnswers(Long attemptId, AttemptSaveRequest request) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Attempt not editable in state " + attempt.getStatus());
        }

        List<AnswerDTO> incoming = Optional.ofNullable(request.getAnswers()).orElseGet(List::of);
        List<Answer> answers = incoming.stream()
                .map(dto -> toDomain(attemptId, dto))
                .collect(Collectors.toList());
        answerRepository.upsertAnswers(attemptId, answers);
        return attempt.getStatus();
    }

    @Override
    public AttemptStatus submit(Long attemptId, AttemptSubmitRequest request) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Attempt not in progress, actual: " + attempt.getStatus());
        }

        PaperSnapshot snapshot = examRepository.loadPaperSnapshot(attempt.getExamId())
                .orElseThrow(() -> new IllegalStateException("Exam or paper snapshot not found"));
        List<Answer> answers = answerRepository.findByAttemptId(attemptId);
        double objectiveScore = gradeObjective(snapshot, answers);
        attempt.setObjectiveScore(objectiveScore);
        attempt.setSubmittedAt(Instant.now());

        boolean updated = attemptRepository.updateStatus(attemptId, AttemptStatus.IN_PROGRESS, AttemptStatus.SUBMITTED);
        if (!updated) {
            throw new IllegalStateException("Concurrent submit detected for attempt " + attemptId);
        }
        attempt.setStatus(AttemptStatus.SUBMITTED);
        return attempt.getStatus();
    }

    private Answer toDomain(Long attemptId, AnswerDTO dto) {
        Answer answer = new Answer();
        answer.setAttemptId(attemptId);
        answer.setQuestionId(dto.getQuestionId());
        answer.setContent(dto.getContent());
        return answer;
    }

    private double gradeObjective(PaperSnapshot snapshot, List<Answer> answers) {
        Map<Long, AnswerContent> submitted = answers.stream()
                .collect(Collectors.toMap(Answer::getQuestionId, Answer::getContent, (a, b) -> b));

        double total = 0.0;
        for (QuestionSnapshotDTO q : snapshot.getQuestions()) {
            AnswerContent expected = q.getCorrectAnswer();
            AnswerContent actual = submitted.get(q.getQuestionId());
            if (actual == null || expected == null) {
                continue;
            }
            switch (q.getType()) {
                case SINGLE_CHOICE, TRUE_FALSE -> total += scoreSingle(q, expected, actual);
                case MULTI_CHOICE -> total += scoreMulti(q, expected, actual);
                case FILL_BLANK -> total += scoreFillBlank(q, expected, actual);
                default -> {
                }
            }
        }
        return total;
    }

    private double scoreSingle(QuestionSnapshotDTO question, AnswerContent expected, AnswerContent actual) {
        if (expected.getChoice() == null || actual.getChoice() == null) {
            return 0;
        }
        return Objects.equals(first(expected.getChoice()), first(actual.getChoice())) ? question.getScore() : 0;
    }

    private double scoreMulti(QuestionSnapshotDTO question, AnswerContent expected, AnswerContent actual) {
        if (expected.getChoice() == null || actual.getChoice() == null) {
            return 0;
        }
        Set<String> expectedSet = new HashSet<>(expected.getChoice());
        Set<String> actualSet = new HashSet<>(actual.getChoice());
        return expectedSet.equals(actualSet) ? question.getScore() : 0;
    }

    private double scoreFillBlank(QuestionSnapshotDTO question, AnswerContent expected, AnswerContent actual) {
        if (expected.getBlanks() == null || actual.getBlanks() == null) {
            return 0;
        }
        Map<Integer, String> expectedMap = expected.getBlanks().stream()
                .collect(Collectors.toMap(BlankAnswer::getIndex, BlankAnswer::getValue));
        Map<Integer, String> actualMap = actual.getBlanks().stream()
                .collect(Collectors.toMap(BlankAnswer::getIndex, BlankAnswer::getValue));

        int blanks = expectedMap.size();
        if (blanks == 0) {
            return 0;
        }
        long correct = expectedMap.entrySet().stream()
                .filter(entry -> normalize(entry.getValue()).equals(normalize(actualMap.get(entry.getKey()))))
                .count();
        return question.getScore() * ((double) correct / blanks);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String first(List<String> values) {
        return values.isEmpty() ? null : values.get(0);
    }
}

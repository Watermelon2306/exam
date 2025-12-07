package com.example.exam.core.repository;

import com.example.exam.core.domain.Answer;
import java.util.List;

public interface AnswerRepository {
    void upsertAnswers(Long attemptId, List<Answer> answers);

    List<Answer> findByAttemptId(Long attemptId);
}

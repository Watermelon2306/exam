package com.example.exam.core.domain;

import com.example.exam.api.dto.AnswerContent;

public class Answer {
    private Long attemptId;
    private Long questionId;
    private AnswerContent content;
    private Double score;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public AnswerContent getContent() {
        return content;
    }

    public void setContent(AnswerContent content) {
        this.content = content;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

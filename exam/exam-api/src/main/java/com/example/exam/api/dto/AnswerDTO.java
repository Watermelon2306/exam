package com.example.exam.api.dto;

public class AnswerDTO {
    private Long questionId;
    private AnswerContent content;

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
}

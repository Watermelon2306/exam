package com.example.exam.api.dto;

import com.example.exam.api.enums.QuestionType;
import java.util.List;

public class QuestionSnapshotDTO {
    private Long questionId;
    private QuestionType type;
    private String stemHtml;
    private List<String> options;
    private AnswerContent correctAnswer;
    private double score;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getStemHtml() {
        return stemHtml;
    }

    public void setStemHtml(String stemHtml) {
        this.stemHtml = stemHtml;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public AnswerContent getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(AnswerContent correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}

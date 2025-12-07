package com.example.exam.api.dto;

import java.util.List;

public class PaperSnapshotDTO {
    private Long paperId;
    private String version;
    private List<QuestionSnapshotDTO> questions;

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<QuestionSnapshotDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionSnapshotDTO> questions) {
        this.questions = questions;
    }
}

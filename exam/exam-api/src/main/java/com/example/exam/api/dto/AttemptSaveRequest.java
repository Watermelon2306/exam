package com.example.exam.api.dto;

import java.util.List;

public class AttemptSaveRequest {
    private long clientTime;
    private List<AnswerDTO> answers;

    public long getClientTime() {
        return clientTime;
    }

    public void setClientTime(long clientTime) {
        this.clientTime = clientTime;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
}

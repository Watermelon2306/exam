package com.example.exam.api.dto;

public class AttemptStartResponse {
    private Long attemptId;
    private long serverTime;
    private PaperSnapshotDTO snapshot;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public PaperSnapshotDTO getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(PaperSnapshotDTO snapshot) {
        this.snapshot = snapshot;
    }
}

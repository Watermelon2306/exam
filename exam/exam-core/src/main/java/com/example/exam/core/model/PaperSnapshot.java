package com.example.exam.core.model;

import com.example.exam.api.dto.PaperSnapshotDTO;
import com.example.exam.api.dto.QuestionSnapshotDTO;
import java.util.List;

public class PaperSnapshot {
    private final PaperSnapshotDTO snapshot;

    public PaperSnapshot(PaperSnapshotDTO snapshot) {
        this.snapshot = snapshot;
    }

    public PaperSnapshotDTO asDto() {
        return snapshot;
    }

    public List<QuestionSnapshotDTO> getQuestions() {
        return snapshot.getQuestions();
    }
}

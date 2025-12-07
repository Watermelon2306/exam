package com.example.exam.core.repository;

import com.example.exam.core.model.PaperSnapshot;
import java.util.Optional;

public interface ExamRepository {
    Optional<PaperSnapshot> loadPaperSnapshot(Long examId);
}

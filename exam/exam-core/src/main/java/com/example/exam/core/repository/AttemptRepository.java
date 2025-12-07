package com.example.exam.core.repository;

import com.example.exam.api.enums.AttemptStatus;
import com.example.exam.core.domain.Attempt;
import java.util.Optional;

public interface AttemptRepository {
    Optional<Attempt> findLatestInProgress(Long examId, Long userId);

    Attempt saveNew(Attempt attempt);

    Optional<Attempt> findById(Long attemptId);

    boolean updateStatus(Long attemptId, AttemptStatus expected, AttemptStatus target);
}

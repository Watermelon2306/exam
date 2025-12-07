package com.example.exam.core.service;

import com.example.exam.api.dto.AttemptSaveRequest;
import com.example.exam.api.dto.AttemptStartRequest;
import com.example.exam.api.dto.AttemptStartResponse;
import com.example.exam.api.dto.AttemptSubmitRequest;
import com.example.exam.api.enums.AttemptStatus;

public interface ExamAttemptService {
    AttemptStartResponse start(Long userId, AttemptStartRequest request);

    AttemptStatus saveAnswers(Long attemptId, AttemptSaveRequest request);

    AttemptStatus submit(Long attemptId, AttemptSubmitRequest request);
}

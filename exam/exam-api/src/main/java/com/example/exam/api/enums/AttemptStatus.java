package com.example.exam.api.enums;

/**
 * State machine for a single attempt lifecycle.
 */
public enum AttemptStatus {
    IN_PROGRESS,
    SUBMITTED,
    MARKING,
    SCORED,
    EXPIRED
}

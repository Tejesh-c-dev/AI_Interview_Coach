package com.aicoach.execution.entity;

/** Normalized execution verdict for a code submission. */
public enum Verdict {
    ACCEPTED,
    WRONG_ANSWER,
    COMPILE_ERROR,
    RUNTIME_ERROR,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    PROCESSING,
    UNKNOWN
}

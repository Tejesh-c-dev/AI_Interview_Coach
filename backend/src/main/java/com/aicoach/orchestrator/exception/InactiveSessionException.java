package com.aicoach.orchestrator.exception;

public class InactiveSessionException extends RuntimeException {
    public InactiveSessionException() {
        super("Interview session is no longer active");
    }
}

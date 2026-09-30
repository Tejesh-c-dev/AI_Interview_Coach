package com.aicoach.orchestrator.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("Interview session was not found");
    }
}

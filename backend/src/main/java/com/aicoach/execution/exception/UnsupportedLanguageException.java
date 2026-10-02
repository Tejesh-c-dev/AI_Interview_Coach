package com.aicoach.execution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when the requested programming language is not supported. */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedLanguageException extends RuntimeException {
    public UnsupportedLanguageException(String language) {
        super("Unsupported language: '" + language + "'. Supported languages: java, python, cpp");
    }
}

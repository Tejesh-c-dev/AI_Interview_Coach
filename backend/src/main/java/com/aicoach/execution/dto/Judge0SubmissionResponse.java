package com.aicoach.execution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the raw response from Judge0 after polling a submission.
 * Only fields we actually use are mapped; all others are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Judge0SubmissionResponse(
        String token,
        @JsonProperty("stdout") String stdout,
        @JsonProperty("stderr") String stderr,
        @JsonProperty("compile_output") String compileOutput,
        @JsonProperty("message") String message,
        @JsonProperty("time") String time,
        @JsonProperty("memory") Integer memory,
        @JsonProperty("status") Judge0Status status
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Judge0Status(
            int id,
            @JsonProperty("description") String description
    ) {}
}

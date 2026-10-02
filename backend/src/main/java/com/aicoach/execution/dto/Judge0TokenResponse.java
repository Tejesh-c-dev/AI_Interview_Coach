package com.aicoach.execution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response from Judge0 on initial submission creation - contains the token
 * used to poll for results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Judge0TokenResponse(String token) {}

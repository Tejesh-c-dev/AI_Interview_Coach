package com.aicoach.execution.client;

import com.aicoach.execution.dto.Judge0SubmissionRequest;
import com.aicoach.execution.dto.Judge0SubmissionResponse;
import com.aicoach.execution.dto.Judge0TokenResponse;
import com.aicoach.execution.exception.Judge0UnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

/**
 * Backend-only HTTP client for the Judge0 code execution API.
 *
 * <p>All communication with Judge0 goes through this class; no Judge0
 * credentials or endpoints are ever exposed to the frontend.
 *
 * <p>Polling strategy: for Phase 1 we use synchronous polling with a short
 * wait between retries.  If the submission takes longer than
 * {@code judge0.poll-timeout-ms} (default 10 s) we stop polling and return
 * a {@link com.aicoach.execution.entity.Verdict#PROCESSING} verdict so the
 * caller can decide how to handle the result.
 */
@Component
public class Judge0Client {

    private static final Logger log = LoggerFactory.getLogger(Judge0Client.class);

    /** Judge0 status IDs that mean execution is still in progress. */
    private static final int STATUS_IN_QUEUE   = 1;
    private static final int STATUS_PROCESSING = 2;

    private static final int POLL_INTERVAL_MS = 1_000;
    private static final int MAX_POLL_ATTEMPTS = 10;

    private final RestClient restClient;
    private final long pollTimeoutMs;

    public Judge0Client(
            RestClient judge0RestClient,
            @Value("${app.judge0.poll-timeout-ms:10000}") long pollTimeoutMs) {
        this.restClient    = judge0RestClient;
        this.pollTimeoutMs = pollTimeoutMs;
    }

    /**
     * Submits source code to Judge0 and polls until a terminal result is
     * received or the timeout elapses.
     *
     * @param request the submission payload
     * @return the terminal Judge0 response
     * @throws Judge0UnavailableException if Judge0 cannot be reached
     */
    public Judge0SubmissionResponse submit(Judge0SubmissionRequest request) {
        String token = createSubmission(request);
        return pollForResult(token);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private String createSubmission(Judge0SubmissionRequest request) {
        try {
            Judge0TokenResponse tokenResponse = restClient.post()
                    .uri("/submissions?base64_encoded=false&wait=false")
                    .body(request)
                    .retrieve()
                    .body(Judge0TokenResponse.class);

            if (tokenResponse == null || tokenResponse.token() == null || tokenResponse.token().isBlank()) {
                throw new Judge0UnavailableException("Judge0 returned an empty token");
            }
            return tokenResponse.token();

        } catch (ResourceAccessException ex) {
            log.error("Could not connect to Judge0", ex);
            throw new Judge0UnavailableException("Code execution service is unavailable", ex);
        } catch (RestClientResponseException ex) {
            log.error("Judge0 rejected submission: status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new Judge0UnavailableException("Code execution service returned an error: " + ex.getStatusCode(), ex);
        }
    }

    private Judge0SubmissionResponse pollForResult(String token) {
        long deadline = System.currentTimeMillis() + pollTimeoutMs;
        int attempt = 0;

        while (attempt < MAX_POLL_ATTEMPTS && System.currentTimeMillis() < deadline) {
            attempt++;
            Judge0SubmissionResponse result = fetchResult(token);

            int statusId = result.status() != null ? result.status().id() : -1;
            if (statusId != STATUS_IN_QUEUE && statusId != STATUS_PROCESSING) {
                log.debug("Judge0 token={} finished after {} poll(s) with status={}", token, attempt, statusId);
                return result;
            }

            log.debug("Judge0 token={} still processing (attempt {})", token, attempt);
            sleepQuietly(POLL_INTERVAL_MS);
        }

        log.warn("Judge0 token={} did not complete within {}ms", token, pollTimeoutMs);
        // Return a synthetic PROCESSING response so the service layer can map it to Verdict.PROCESSING
        return new Judge0SubmissionResponse(token, null, null, null, "Execution timed out waiting for result",
                null, null,
                new Judge0SubmissionResponse.Judge0Status(STATUS_PROCESSING, "Processing"));
    }

    private Judge0SubmissionResponse fetchResult(String token) {
        try {
            Judge0SubmissionResponse response = restClient.get()
                    .uri("/submissions/{token}?base64_encoded=false&fields=token,stdout,stderr,compile_output,message,time,memory,status", token)
                    .retrieve()
                    .body(Judge0SubmissionResponse.class);

            if (response == null) {
                throw new Judge0UnavailableException("Judge0 returned an empty response for token: " + token);
            }
            return response;

        } catch (ResourceAccessException ex) {
            log.error("Lost connection to Judge0 while polling token={}", token, ex);
            throw new Judge0UnavailableException("Code execution service became unavailable", ex);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new Judge0UnavailableException("Submission token not found: " + token, ex);
            }
            log.error("Judge0 polling error: status={}", ex.getStatusCode(), ex);
            throw new Judge0UnavailableException("Code execution service returned an error while polling", ex);
        }
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

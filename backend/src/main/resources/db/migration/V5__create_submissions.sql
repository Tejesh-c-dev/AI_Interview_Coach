CREATE TABLE submissions (
    id           UUID PRIMARY KEY,
    session_id   UUID        NOT NULL REFERENCES interview_sessions(id) ON DELETE CASCADE,
    question_id  UUID        NOT NULL REFERENCES questions(id),
    language     VARCHAR(32) NOT NULL,
    source_code  TEXT        NOT NULL,
    verdict      VARCHAR(32) NOT NULL,
    runtime_ms   DOUBLE PRECISION,
    memory_kb    INTEGER,
    stdout       TEXT,
    stderr       TEXT,
    compile_output TEXT,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT submissions_verdict_check CHECK (verdict IN (
        'ACCEPTED', 'WRONG_ANSWER', 'COMPILE_ERROR', 'RUNTIME_ERROR',
        'TIME_LIMIT_EXCEEDED', 'MEMORY_LIMIT_EXCEEDED', 'PROCESSING', 'UNKNOWN'
    )),
    CONSTRAINT submissions_language_check CHECK (language IN ('java', 'python', 'cpp'))
);

CREATE INDEX submissions_session_idx  ON submissions (session_id, created_at DESC);
CREATE INDEX submissions_question_idx ON submissions (question_id);

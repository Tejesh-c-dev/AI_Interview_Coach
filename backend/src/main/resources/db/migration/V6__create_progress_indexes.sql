CREATE TABLE topic_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic VARCHAR(100) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    successful_attempts INTEGER NOT NULL DEFAULT 0 CHECK (successful_attempts >= 0),
    accuracy DOUBLE PRECISION NOT NULL DEFAULT 0 CHECK (accuracy >= 0 AND accuracy <= 100),
    last_practiced TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT topic_progress_user_topic_unique UNIQUE (user_id, topic),
    CONSTRAINT topic_progress_successes_check CHECK (successful_attempts <= attempts)
);

CREATE INDEX topic_progress_user_idx ON topic_progress (user_id, accuracy, topic);

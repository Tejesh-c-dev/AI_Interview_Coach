CREATE TABLE interview_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    track VARCHAR(32) NOT NULL,
    difficulty VARCHAR(16) NOT NULL,
    company_mode VARCHAR(100),
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(16) NOT NULL,
    CONSTRAINT interview_sessions_status_check CHECK (status IN ('ACTIVE', 'COMPLETED')),
    CONSTRAINT interview_sessions_track_check CHECK (track IN ('DSA', 'BEHAVIORAL', 'SYSTEM_DESIGN', 'OOP', 'JAVA')),
    CONSTRAINT interview_sessions_difficulty_check CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);

CREATE INDEX interview_sessions_user_idx ON interview_sessions (user_id, started_at);

CREATE TABLE interview_session_questions (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES interview_sessions(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES questions(id),
    question_order INTEGER NOT NULL,
    CONSTRAINT session_question_unique UNIQUE (session_id, question_id)
);

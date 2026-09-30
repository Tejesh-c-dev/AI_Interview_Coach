CREATE TABLE questions (
    id UUID PRIMARY KEY,
    track VARCHAR(32) NOT NULL,
    difficulty VARCHAR(16) NOT NULL,
    topic VARCHAR(100) NOT NULL,
    prompt TEXT NOT NULL,
    question_type VARCHAR(16) NOT NULL,
    coding_language VARCHAR(50),
    starter_code TEXT,
    function_signature VARCHAR(255),
    hidden_test_cases JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT questions_track_check CHECK (track IN ('DSA', 'BEHAVIORAL', 'SYSTEM_DESIGN', 'OOP', 'JAVA')),
    CONSTRAINT questions_difficulty_check CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    CONSTRAINT questions_type_check CHECK (question_type IN ('CODING', 'NON_CODING'))
);

CREATE INDEX questions_configuration_idx ON questions (track, difficulty, topic, question_type, id);

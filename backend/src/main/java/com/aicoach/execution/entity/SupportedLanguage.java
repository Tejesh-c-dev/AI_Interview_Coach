package com.aicoach.execution.entity;

import java.util.Map;
import java.util.Optional;

/**
 * Supported programming languages for Phase 1 code execution.
 *
 * Judge0 language IDs:
 *  - Java   = 62  (OpenJDK 13.0.1)
 *  - Python = 71  (Python 3.8.1)
 *  - C++    = 54  (GCC 9.2.0)
 *
 * These IDs correspond to the Judge0 Community Edition language list.
 */
public enum SupportedLanguage {
    JAVA("java", 62),
    PYTHON("python", 71),
    CPP("cpp", 54);

    private final String key;
    private final int judge0Id;

    private static final Map<String, SupportedLanguage> BY_KEY = Map.of(
            "java", JAVA,
            "python", PYTHON,
            "cpp", CPP
    );

    SupportedLanguage(String key, int judge0Id) {
        this.key = key;
        this.judge0Id = judge0Id;
    }

    public String getKey() { return key; }
    public int getJudge0Id() { return judge0Id; }

    public static Optional<SupportedLanguage> fromKey(String key) {
        if (key == null) return Optional.empty();
        return Optional.ofNullable(BY_KEY.get(key.toLowerCase()));
    }
}

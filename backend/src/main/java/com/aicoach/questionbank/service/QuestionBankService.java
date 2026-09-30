package com.aicoach.questionbank.service;

import com.aicoach.questionbank.dto.QuestionResponse;
import com.aicoach.questionbank.dto.QuestionSelectionRequest;
import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionDifficulty;
import com.aicoach.questionbank.entity.QuestionTrack;
import com.aicoach.questionbank.entity.QuestionType;
import com.aicoach.questionbank.exception.QuestionNotFoundException;
import com.aicoach.questionbank.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class QuestionBankService {
    private final QuestionRepository repository;

    public QuestionBankService(QuestionRepository repository) {
        this.repository = repository;
    }

    public List<QuestionResponse> findQuestions(String track, String difficulty, String topic, String type) {
        QuestionTrack parsedTrack = parseTrack(track);
        QuestionDifficulty parsedDifficulty = parseDifficulty(difficulty);
        QuestionType parsedType = type == null || type.isBlank() ? null : parseType(type);
        String normalizedTopic = topic == null ? null : topic.trim();

        List<Question> questions;
        if (parsedType != null && normalizedTopic != null && !normalizedTopic.isBlank()) {
            questions = repository.findAllByTrackAndDifficultyAndTopicContainingIgnoreCaseAndQuestionTypeOrderById(
                    parsedTrack, parsedDifficulty, normalizedTopic, parsedType);
        } else if (parsedType != null) {
            questions = repository.findAllByTrackAndDifficultyAndQuestionTypeOrderById(
                    parsedTrack, parsedDifficulty, parsedType);
        } else if (normalizedTopic != null && !normalizedTopic.isBlank()) {
            questions = repository.findAllByTrackAndDifficultyAndTopicContainingIgnoreCaseOrderById(
                    parsedTrack, parsedDifficulty, normalizedTopic);
        } else {
            questions = repository.findAllByTrackAndDifficultyOrderById(parsedTrack, parsedDifficulty);
        }
        return questions.stream().map(QuestionResponse::from).toList();
    }

    public QuestionResponse selectQuestion(QuestionSelectionRequest request) {
        return selectQuestion(request, Set.of());
    }

    public QuestionResponse selectQuestion(QuestionSelectionRequest request, Set<UUID> excludedQuestionIds) {
        QuestionTrack track = parseTrack(request.track());
        QuestionDifficulty difficulty = parseDifficulty(request.difficulty());
        String topic = request.topic() == null ? null : request.topic().trim();
        List<Question> candidates = topic == null || topic.isBlank()
                ? repository.findAllByTrackAndDifficultyOrderById(track, difficulty)
                : repository.findAllByTrackAndDifficultyAndTopicContainingIgnoreCaseOrderById(
                track, difficulty, topic);
        if (candidates.isEmpty()) {
            throw new QuestionNotFoundException("No questions match the requested track, difficulty, and topic");
        }
        String key = request.selectionKey() == null ? "" : request.selectionKey();
        // Stable hashing gives each interview key a repeatable slot while rotating across candidates.
        int startIndex = Math.floorMod((track.name() + "|" + difficulty.name() + "|" + topic + "|" + key).hashCode(),
                candidates.size());
        for (int offset = 0; offset < candidates.size(); offset++) {
            Question candidate = candidates.get((startIndex + offset) % candidates.size());
            if (!excludedQuestionIds.contains(candidate.getId())) {
                return QuestionResponse.from(candidate);
            }
        }
        throw new QuestionNotFoundException("No unused questions remain for this session");
    }

    public Question findEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question was not found"));
    }

    public static QuestionTrack parseTrack(String value) {
        return parse(value, QuestionTrack.class, "track");
    }

    public static QuestionDifficulty parseDifficulty(String value) {
        return parse(value, QuestionDifficulty.class, "difficulty");
    }

    public static QuestionType parseType(String value) {
        return parse(value, QuestionType.class, "type");
    }

    private static <T extends Enum<T>> T parse(String value, Class<T> enumType, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(enumType, normalized);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid " + field + " value: " + value);
        }
    }
}

package com.aicoach.questionbank.service;

import com.aicoach.questionbank.dto.QuestionResponse;
import com.aicoach.questionbank.dto.QuestionSelectionRequest;
import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionDifficulty;
import com.aicoach.questionbank.entity.QuestionTrack;
import com.aicoach.questionbank.entity.QuestionType;
import com.aicoach.questionbank.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionBankServiceTest {
    @Mock
    private QuestionRepository repository;

    @Test
    void filtersByTrackDifficultyTopicAndType() {
        Question question = question(QuestionTrack.DSA, QuestionDifficulty.MEDIUM, "Arrays", QuestionType.CODING);
        when(repository.findAllByTrackAndDifficultyAndTopicContainingIgnoreCaseAndQuestionTypeOrderById(
                QuestionTrack.DSA, QuestionDifficulty.MEDIUM, "array", QuestionType.CODING))
                .thenReturn(List.of(question));

        List<QuestionResponse> result = new QuestionBankService(repository)
                .findQuestions("dsa", "medium", "array", "coding");

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.track()).isEqualTo(QuestionTrack.DSA);
            assertThat(response.difficulty()).isEqualTo(QuestionDifficulty.MEDIUM);
            assertThat(response.questionType()).isEqualTo(QuestionType.CODING);
        });
    }

    @Test
    void selectionIsStableForTheSameKeyAndCanChooseAnotherCandidate() {
        Question first = question(QuestionTrack.DSA, QuestionDifficulty.EASY, "Arrays", QuestionType.CODING);
        Question second = question(QuestionTrack.DSA, QuestionDifficulty.EASY, "Strings", QuestionType.CODING);
        when(repository.findAllByTrackAndDifficultyOrderById(QuestionTrack.DSA, QuestionDifficulty.EASY))
                .thenReturn(List.of(first, second));
        QuestionBankService service = new QuestionBankService(repository);

        QuestionResponse selected = service.selectQuestion(
                new QuestionSelectionRequest("DSA", "Easy", null, "interview-1"));
        QuestionResponse repeated = service.selectQuestion(
                new QuestionSelectionRequest("DSA", "Easy", null, "interview-1"));
        QuestionResponse different = service.selectQuestion(
                new QuestionSelectionRequest("DSA", "Easy", null, "interview-2"));

        assertThat(repeated.id()).isEqualTo(selected.id());
        assertThat(different.id()).isNotEqualTo(selected.id());
    }

    @Test
    void doesNotExposeHiddenTestsInPublicResponse() {
        Question question = question(QuestionTrack.DSA, QuestionDifficulty.EASY, "Arrays", QuestionType.CODING);
        question.setHiddenTestCases(Map.of("secret", "assertion"));
        when(repository.findAllByTrackAndDifficultyOrderById(QuestionTrack.DSA, QuestionDifficulty.EASY))
                .thenReturn(List.of(question));

        QuestionResponse response = new QuestionBankService(repository).selectQuestion(
                new QuestionSelectionRequest("DSA", "EASY", null, "key"));

        assertThat(response.toString()).doesNotContain("secret", "hiddenTestCases");
    }

    @Test
    void rejectsInvalidTrack() {
        assertThatThrownBy(() -> QuestionBankService.parseTrack("not-a-track"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid track");
    }

    private static Question question(
            QuestionTrack track, QuestionDifficulty difficulty, String topic, QuestionType type) {
        Question question = new Question();
        question.setId(UUID.randomUUID());
        question.setTrack(track);
        question.setDifficulty(difficulty);
        question.setTopic(topic);
        question.setPrompt("Prompt");
        question.setQuestionType(type);
        return question;
    }
}

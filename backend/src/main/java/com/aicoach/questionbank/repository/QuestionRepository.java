package com.aicoach.questionbank.repository;

import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionDifficulty;
import com.aicoach.questionbank.entity.QuestionTrack;
import com.aicoach.questionbank.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findAllByTrackAndDifficultyOrderById(QuestionTrack track, QuestionDifficulty difficulty);
    List<Question> findAllByTrackAndDifficultyAndTopicContainingIgnoreCaseOrderById(
            QuestionTrack track, QuestionDifficulty difficulty, String topic);
    List<Question> findAllByTrackAndDifficultyAndQuestionTypeOrderById(
            QuestionTrack track, QuestionDifficulty difficulty, QuestionType questionType);
    List<Question> findAllByTrackAndDifficultyAndTopicContainingIgnoreCaseAndQuestionTypeOrderById(
            QuestionTrack track, QuestionDifficulty difficulty, String topic, QuestionType questionType);
    List<Question> findAllByTrackOrderById(QuestionTrack track);
    List<Question> findAllByDifficultyOrderById(QuestionDifficulty difficulty);
    List<Question> findAllByTopicContainingIgnoreCaseOrderById(String topic);
    List<Question> findAllByQuestionTypeOrderById(QuestionType questionType);
}

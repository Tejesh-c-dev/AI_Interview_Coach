package com.aicoach.progress.repository;

import com.aicoach.progress.entity.TopicProgress;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicProgressRepository extends JpaRepository<TopicProgress, UUID> {
    List<TopicProgress> findByUserIdOrderByAccuracyAscTopicAsc(UUID userId);

    Optional<TopicProgress> findByUserIdAndTopic(UUID userId, String topic);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from TopicProgress p where p.user.id = :userId and p.topic = :topic")
    Optional<TopicProgress> findByUserIdAndTopicForUpdate(UUID userId, String topic);
}

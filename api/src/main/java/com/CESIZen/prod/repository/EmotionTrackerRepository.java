package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.EmotionTracker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmotionTrackerRepository extends JpaRepository<EmotionTracker, Long> {
    List<EmotionTracker> findByUserId(Long userId);
    List<EmotionTracker> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}
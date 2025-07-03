package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.EmotionLevel1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionLevel1Repository extends JpaRepository<EmotionLevel1, Long> {
}

package com.CESIZen.prod.repository;

import com.CESIZen.prod.entity.EmotionLevel2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionLevel2Repository extends JpaRepository<EmotionLevel2, Long> {
}

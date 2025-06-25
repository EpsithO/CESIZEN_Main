package com.CESIZen.prod.dto.tracker;

import java.time.LocalDate;


public class EmotionTrackerResponseDto {
    public Long id;
    public String emotionName;
    public String description;
    public LocalDate date;

    public EmotionTrackerResponseDto(Long id, String emotionName, String description, LocalDate date) {
        this.id = id;
        this.emotionName = emotionName;
        this.description = description;
        this.date = date;
    }
}

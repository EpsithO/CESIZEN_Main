package com.CESIZen.prod.dto.tracker;

import java.time.LocalDate;

public class EmotionTrackerResponseDto {
    private Long id;
    private String emotionName;
    private String description;
    private LocalDate date;

    public EmotionTrackerResponseDto(Long id, String emotionName, String description, LocalDate date) {
        this.id = id;
        this.emotionName = emotionName;
        this.description = description;
        this.date = date;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmotionName() {
        return emotionName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

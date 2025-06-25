package com.CESIZen.prod.dto.tracker;

import java.time.LocalDate;

public class EmotionTrackerRequestDto {
    public Long emotionId;
    public String description;
    public LocalDate date;

    public Long getEmotionId() {
        return emotionId;
    }
    
    public void setEmotionId(Long emotionId) {
        this.emotionId = emotionId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
}




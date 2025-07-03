package com.CESIZen.prod.dto.tracker;

public class EmotionTrackerResponseDto {
    private Long id;
    private String emotionName;
    private String description;
    private String date; 

    public EmotionTrackerResponseDto(Long id, String emotionName, String description, String date) {
        this.id = id;
        this.emotionName = emotionName;
        this.description = description;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getEmotionName() {
        return emotionName;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

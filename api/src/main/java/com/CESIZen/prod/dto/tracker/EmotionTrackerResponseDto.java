package com.CESIZen.prod.dto.tracker;

public class EmotionTrackerResponseDto {

    private Long id;
    private String emotion;
    private String description;
    private String date;

    public EmotionTrackerResponseDto() {
    }

    public EmotionTrackerResponseDto(Long id, String emotion, String description, String date) {
        this.id = id;
        this.emotion = emotion;
        this.description = description;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

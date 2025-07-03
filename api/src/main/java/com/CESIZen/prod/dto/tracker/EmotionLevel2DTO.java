package com.CESIZen.prod.dto.tracker;

public class EmotionLevel2DTO {
    private Long id;
    private String name;
    private Long parentId; // utile pour créer/mettre à jour

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}

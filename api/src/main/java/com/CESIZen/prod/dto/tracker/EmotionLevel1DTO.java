package com.CESIZen.prod.dto.tracker;

import java.util.List;

public class EmotionLevel1DTO {
    private Long id;
    private String name;
    private List<EmotionLevel2DTO> children;

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

    public List<EmotionLevel2DTO> getChildren() {
        return children;
    }

    public void setChildren(List<EmotionLevel2DTO> children) {
        this.children = children;
    }
}

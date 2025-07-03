package com.CESIZen.prod.entity;

import jakarta.persistence.*;

@Entity
public class EmotionLevel2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private EmotionLevel1 parent;

    public EmotionLevel2() {
    }

    public EmotionLevel2(Long id, String name, EmotionLevel1 parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

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

    public EmotionLevel1 getParent() {
        return parent;
    }

    public void setParent(EmotionLevel1 parent) {
        this.parent = parent;
    }
}

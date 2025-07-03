package com.CESIZen.prod.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EmotionLevel1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmotionLevel2> children = new ArrayList<>();

    public EmotionLevel1() {
    }

    public EmotionLevel1(Long id, String name, List<EmotionLevel2> children) {
        this.id = id;
        this.name = name;
        this.children = children;
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

    public List<EmotionLevel2> getChildren() {
        return children;
    }

    public void setChildren(List<EmotionLevel2> children) {
        this.children = children;
    }

    public void addChild(EmotionLevel2 child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(EmotionLevel2 child) {
        children.remove(child);
        child.setParent(null);
    }
}

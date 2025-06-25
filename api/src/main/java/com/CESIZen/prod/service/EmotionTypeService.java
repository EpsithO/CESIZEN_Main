package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.tracker.EmotionLevel1DTO;
import com.CESIZen.prod.dto.tracker.EmotionLevel2DTO;
import com.CESIZen.prod.entity.EmotionLevel1;
import com.CESIZen.prod.entity.EmotionLevel2;
import com.CESIZen.prod.repository.EmotionLevel1Repository;
import com.CESIZen.prod.repository.EmotionLevel2Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmotionTypeService {

    private final EmotionLevel1Repository level1Repo;
    private final EmotionLevel2Repository level2Repo;

    public EmotionTypeService(EmotionLevel1Repository level1Repo, EmotionLevel2Repository level2Repo) {
        this.level1Repo = level1Repo;
        this.level2Repo = level2Repo;
    }

    public List<EmotionLevel1DTO> getAllLevel1() {
        return level1Repo.findAll().stream().map(level1 -> {
            EmotionLevel1DTO dto = new EmotionLevel1DTO();
            dto.setId(level1.getId());
            dto.setName(level1.getName());

            List<EmotionLevel2DTO> children = level1.getChildren().stream().map(level2 -> {
                EmotionLevel2DTO childDTO = new EmotionLevel2DTO();
                childDTO.setId(level2.getId());
                childDTO.setName(level2.getName());
                childDTO.setParentId(level1.getId()); // Ajout utile côté front
                return childDTO;
            }).collect(Collectors.toList());

            dto.setChildren(children);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<EmotionLevel2DTO> getAllLevel2() {
        return level2Repo.findAll().stream().map(level2 -> {
            EmotionLevel2DTO dto = new EmotionLevel2DTO();
            dto.setId(level2.getId());
            dto.setName(level2.getName());
            dto.setParentId(level2.getParent().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public EmotionLevel1DTO createLevel1(String name) {
        EmotionLevel1 entity = new EmotionLevel1();
        entity.setName(name);
        EmotionLevel1 saved = level1Repo.save(entity);

        EmotionLevel1DTO dto = new EmotionLevel1DTO();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setChildren(Collections.emptyList());
        return dto;
    }

    @Transactional
    public EmotionLevel2DTO createLevel2(Long parentId, String name) {
        EmotionLevel1 parent = level1Repo.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Niveau 1 introuvable"));

        EmotionLevel2 level2 = new EmotionLevel2();
        level2.setName(name);
        level2.setParent(parent);
        EmotionLevel2 saved = level2Repo.save(level2);

        EmotionLevel2DTO dto = new EmotionLevel2DTO();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setParentId(parentId);
        return dto;
    }

    @Transactional
    public EmotionLevel1DTO updateLevel1(Long id, EmotionLevel1DTO dto) {
        EmotionLevel1 entity = level1Repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Niveau 1 introuvable"));
        entity.setName(dto.getName());
        EmotionLevel1 updated = level1Repo.save(entity);

        EmotionLevel1DTO result = new EmotionLevel1DTO();
        result.setId(updated.getId());
        result.setName(updated.getName());
        result.setChildren(updated.getChildren().stream().map(child -> {
            EmotionLevel2DTO childDTO = new EmotionLevel2DTO();
            childDTO.setId(child.getId());
            childDTO.setName(child.getName());
            childDTO.setParentId(updated.getId());
            return childDTO;
        }).collect(Collectors.toList()));

        return result;
    }

    @Transactional
    public EmotionLevel2DTO updateLevel2(Long id, EmotionLevel2DTO dto) {
        EmotionLevel2 entity = level2Repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Niveau 2 introuvable"));
        entity.setName(dto.getName());
        EmotionLevel2 updated = level2Repo.save(entity);

        EmotionLevel2DTO result = new EmotionLevel2DTO();
        result.setId(updated.getId());
        result.setName(updated.getName());
        result.setParentId(updated.getParent().getId());
        return result;
    }

    @Transactional
    public void deleteLevel1(Long id) {
        level1Repo.deleteById(id);
    }

    @Transactional
    public void deleteLevel2(Long id) {
        level2Repo.deleteById(id);
    }
}

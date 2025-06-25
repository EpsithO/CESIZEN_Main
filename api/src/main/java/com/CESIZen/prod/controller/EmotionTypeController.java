package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.tracker.EmotionLevel1DTO;
import com.CESIZen.prod.dto.tracker.EmotionLevel2DTO;
import com.CESIZen.prod.service.EmotionTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionTypeController {

    private final EmotionTypeService service;

    public EmotionTypeController(EmotionTypeService service) {
        this.service = service;
    }

    @GetMapping("/level1")
    public List<EmotionLevel1DTO> getAllLevel1() {
        return service.getAllLevel1();
    }

    @GetMapping("/level2")
    public List<EmotionLevel2DTO> getAllLevel2() {
        return service.getAllLevel2();
    }

    @PostMapping("/level1")
    public EmotionLevel1DTO createLevel1(@RequestBody EmotionLevel1DTO dto) {
        return service.createLevel1(dto.getName());  // ✅ FIX ici
    }

    @PostMapping("/level2")
    public EmotionLevel2DTO createLevel2(@RequestBody EmotionLevel2DTO dto) {
        return service.createLevel2(dto.getParentId(), dto.getName());  // ✅ FIX ici
    }

    @PutMapping("/level1/{id}")
    public EmotionLevel1DTO updateLevel1(@PathVariable Long id, @RequestBody EmotionLevel1DTO dto) {
        return service.updateLevel1(id, dto);
    }

    @PutMapping("/level2/{id}")
    public EmotionLevel2DTO updateLevel2(@PathVariable Long id, @RequestBody EmotionLevel2DTO dto) {
        return service.updateLevel2(id, dto);
    }

    @DeleteMapping("/level1/{id}")
    public void deleteLevel1(@PathVariable Long id) {
        service.deleteLevel1(id);
    }

    @DeleteMapping("/level2/{id}")
    public void deleteLevel2(@PathVariable Long id) {
        service.deleteLevel2(id);
    }
}

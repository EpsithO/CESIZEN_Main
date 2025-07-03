package com.CESIZen.prod.controller;

import com.CESIZen.prod.dto.tracker.EmotionTrackerRequestDto;
import com.CESIZen.prod.dto.tracker.EmotionTrackerResponseDto;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.service.EmotionTrackerService;
import com.CESIZen.prod.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trackers")
public class EmotionTrackerController {

    private final EmotionTrackerService trackerService;
    private final UserService userService;

    public EmotionTrackerController(EmotionTrackerService trackerService,
                                    UserService userService) {
        this.trackerService = trackerService;
        this.userService = userService;
    }

    // ✅ Liste des trackers de l'utilisateur connecté
    @GetMapping
    public List<EmotionTrackerResponseDto> getAll(Principal principal) {
        return trackerService.getTrackersForUser(getCurrentUser(principal));
    }

    // ✅ Créer un nouveau tracker
    @PostMapping
    public EmotionTrackerResponseDto create(@RequestBody EmotionTrackerRequestDto dto,
                                            Principal principal) {
        return trackerService.createTracker(dto, getCurrentUser(principal));
    }

    // ✅ Mettre à jour un tracker
    @PutMapping("/{id}")
    public EmotionTrackerResponseDto update(@PathVariable Long id,
                                            @RequestBody EmotionTrackerRequestDto dto,
                                            Principal principal) {
        return trackerService.updateTracker(id, dto, getCurrentUser(principal));
    }

    // ✅ Supprimer un tracker
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       Principal principal) {
        trackerService.deleteTracker(id, getCurrentUser(principal));
    }

    // ✅ Générer un rapport d'émotions
    @GetMapping("/report")
    public Map<String, Long> report(@RequestParam String period,
                                    Principal principal) {
        return trackerService.getEmotionReport(getCurrentUser(principal), period);
    }

    // 🔐 Récupère l'utilisateur connecté via le principal
    private User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }
}

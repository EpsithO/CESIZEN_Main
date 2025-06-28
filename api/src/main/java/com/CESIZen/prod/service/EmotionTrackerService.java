package com.CESIZen.prod.service;

import com.CESIZen.prod.dto.tracker.EmotionTrackerRequestDto;
import com.CESIZen.prod.dto.tracker.EmotionTrackerResponseDto;
import com.CESIZen.prod.entity.EmotionLevel2;
import com.CESIZen.prod.entity.EmotionTracker;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.EmotionLevel2Repository;
import com.CESIZen.prod.repository.EmotionTrackerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmotionTrackerService {

    private final EmotionTrackerRepository repository;
    private final EmotionLevel2Repository emotionRepo;

    public EmotionTrackerService(EmotionTrackerRepository repository, EmotionLevel2Repository emotionRepo) {
        this.repository = repository;
        this.emotionRepo = emotionRepo;
    }

    public List<EmotionTrackerResponseDto> getTrackersForUser(User user) {
        return repository.findByUserId(user.getId()).stream()
                .map(e -> new EmotionTrackerResponseDto(
                        e.getId(),
                        e.getEmotion().getName(),
                        e.getDescription(),
                        e.getDate().toString()  // ✅ conversion ici
                ))
                .collect(Collectors.toList());
    }

    public EmotionTrackerResponseDto createTracker(EmotionTrackerRequestDto dto, User user) {
        EmotionLevel2 emotion = emotionRepo.findById(dto.getEmotionId())
                .orElseThrow(() -> new RuntimeException("Émotion non trouvée"));

        EmotionTracker tracker = new EmotionTracker();
        tracker.setEmotion(emotion);
        tracker.setDescription(dto.getDescription());
        tracker.setDate(dto.getDate());
        tracker.setUser(user);

        EmotionTracker saved = repository.save(tracker);
        return new EmotionTrackerResponseDto(
                saved.getId(),
                saved.getEmotion().getName(),
                saved.getDescription(),
                saved.getDate().toString()  // ✅ conversion ici
        );
    }

    public EmotionTrackerResponseDto updateTracker(Long id, EmotionTrackerRequestDto dto, User user) {
        EmotionTracker tracker = repository.findById(id).orElseThrow();
        if (!tracker.getUser().getId().equals(user.getId())) throw new RuntimeException("Accès refusé");

        EmotionLevel2 emotion = emotionRepo.findById(dto.getEmotionId())
                .orElseThrow(() -> new RuntimeException("Émotion non trouvée"));

        tracker.setEmotion(emotion);
        tracker.setDescription(dto.getDescription());
        tracker.setDate(dto.getDate());

        EmotionTracker updated = repository.save(tracker);
        return new EmotionTrackerResponseDto(
                updated.getId(),
                updated.getEmotion().getName(),
                updated.getDescription(),
                updated.getDate().toString()  // ✅ conversion ici
        );
    }

    public void deleteTracker(Long id, User user) {
        EmotionTracker tracker = repository.findById(id).orElseThrow();
        if (!tracker.getUser().getId().equals(user.getId())) throw new RuntimeException("Accès refusé");

        repository.deleteById(id);
    }

    public Map<String, Long> getEmotionReport(User user, String period) {
        LocalDate end = LocalDate.now();
        LocalDate start;
        switch (period) {
            case "month" -> start = end.minusMonths(1);
            case "quarter" -> start = end.minusMonths(3);
            case "year" -> start = end.minusYears(1);
            default -> start = end.minusWeeks(1);
        }

        List<EmotionTracker> entries = repository.findByUserIdAndDateBetween(user.getId(), start, end);
        return entries.stream()
                .collect(Collectors.groupingBy(e -> e.getEmotion().getName(), Collectors.counting()));
    }
}

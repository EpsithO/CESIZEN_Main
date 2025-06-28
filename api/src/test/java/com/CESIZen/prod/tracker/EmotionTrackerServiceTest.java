package com.CESIZen.prod.tracker;

import com.CESIZen.prod.dto.tracker.EmotionTrackerRequestDto;
import com.CESIZen.prod.dto.tracker.EmotionTrackerResponseDto;
import com.CESIZen.prod.entity.EmotionLevel2;
import com.CESIZen.prod.entity.EmotionTracker;
import com.CESIZen.prod.entity.User;
import com.CESIZen.prod.repository.EmotionLevel2Repository;
import com.CESIZen.prod.repository.EmotionTrackerRepository;
import com.CESIZen.prod.service.EmotionTrackerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmotionTrackerServiceTest {

    @Mock private EmotionTrackerRepository trackerRepo;
    @Mock private EmotionLevel2Repository emotionRepo;
    @InjectMocks private EmotionTrackerService service;

    private User user;
    private EmotionLevel2 joy;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        joy = new EmotionLevel2();
        joy.setId(1L);
        joy.setName("Joie");
        // pas de setColor() dans l’entité -> on l’enlève
    }

    /* --------------------------------------------------------------- */
    private EmotionTracker tracker(Long id, String desc) {
        EmotionTracker t = new EmotionTracker();
        t.setId(id);
        t.setEmotion(joy);
        t.setDescription(desc);
        t.setUser(user);
        t.setDate(LocalDate.now());
        return t;
    }

    /* ====================== createTracker() ======================== */
    @Test
    void createTracker_shouldReturnDto() {
        EmotionTrackerRequestDto dto = new EmotionTrackerRequestDto();
        dto.setEmotionId(1L);
        dto.setDescription("Super journée");
        dto.setDate(LocalDate.now());

        when(emotionRepo.findById(1L)).thenReturn(Optional.of(joy));
        when(trackerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmotionTrackerResponseDto res = service.createTracker(dto, user);

        assertThat(res.getEmotionName()).isEqualTo("Joie");
        assertThat(res.getDescription()).isEqualTo("Super journée");
        verify(trackerRepo).save(any(EmotionTracker.class));
    }

    /* ====================== updateTracker() ======================== */
    @Test
    void updateTracker_shouldModifyDescription() {
        EmotionTrackerRequestDto dto = new EmotionTrackerRequestDto();
        dto.setEmotionId(1L);
        dto.setDescription("MAJ");
        dto.setDate(LocalDate.now());

        EmotionTracker existing = tracker(10L, "old");

        when(trackerRepo.findById(10L)).thenReturn(Optional.of(existing));
        when(emotionRepo.findById(1L)).thenReturn(Optional.of(joy));
        when(trackerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmotionTrackerResponseDto res =
                service.updateTracker(10L, dto, user);

        assertThat(res.getDescription()).isEqualTo("MAJ");
        verify(trackerRepo).save(existing);
    }

    /* ====================== deleteTracker() ======================== */
    @Test
    void deleteTracker_shouldCallRepository() {
        EmotionTracker existing = tracker(10L, "desc");
        when(trackerRepo.findById(10L)).thenReturn(Optional.of(existing));

        service.deleteTracker(10L, user);

        verify(trackerRepo).delete(existing);
    }

}

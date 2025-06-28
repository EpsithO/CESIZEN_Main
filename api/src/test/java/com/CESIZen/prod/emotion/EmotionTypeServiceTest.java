package com.CESIZen.prod.emotion;

import com.CESIZen.prod.entity.EmotionLevel1;
import com.CESIZen.prod.repository.EmotionLevel1Repository;
import com.CESIZen.prod.repository.EmotionLevel2Repository;
import com.CESIZen.prod.service.EmotionTypeService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmotionTypeServiceTest {

    @Mock EmotionLevel1Repository l1Repo;
    @Mock EmotionLevel2Repository l2Repo;
    @InjectMocks EmotionTypeService service;

    @BeforeEach void open(){ MockitoAnnotations.openMocks(this); }

    @Test
    void getAllLevel1_returnsDtos() {
        when(l1Repo.findAll()).thenReturn(List.of(new EmotionLevel1(1L,"Joie",List.of())));

        var list = service.getAllLevel1();

        assertThat(list).hasSize(1)
                        .first().extracting("name").isEqualTo("Joie");
    }
}

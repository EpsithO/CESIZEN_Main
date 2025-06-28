package com.CESIZen.prod.tracker;

import com.CESIZen.prod.security.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmotionTrackerControllerIT {

    @Autowired MockMvc mvc;
    @Autowired JwtUtils jwt;
    String userToken;

    @BeforeEach
    void token() { userToken = "Bearer " + jwt.generateToken("Mat25","USER"); }

    @Test
    void createTracker_shouldPersist() throws Exception {
        String body = """
          { "emotionId": 1, "description":"test","date":"2025-06-25"}""";

        mvc.perform(post("/api/trackers")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.description").value("test"));
    }
}

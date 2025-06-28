package com.CESIZen.prod.emotion;

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
class EmotionTypeControllerIT {

    @Autowired MockMvc mvc;
    @Autowired JwtUtils jwt;
    String admin;

    @BeforeEach void token(){ admin = "Bearer "+jwt.generateToken("Epsitho","ADMIN"); }

    @Test
    void createLevel1_shouldReturnDto() throws Exception {
        mvc.perform(post("/api/emotions/level1")
                .header("Authorization",admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Fierté\"}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("Fierté"));
    }
}

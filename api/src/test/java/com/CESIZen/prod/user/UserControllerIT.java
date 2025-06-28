package com.CESIZen.prod.user;

import com.CESIZen.prod.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired JwtUtils jwt;
    @Autowired MockMvc mvc;

    private String userToken;

    @BeforeEach
    void setUp() {
        // ‹Mat25› est ton user de démonstration / rôle USER
        userToken = "Bearer " + jwt.generateToken("Mat25", "USER");
    }

    @Test
    void shouldReturnProfileForLoggedUser() throws Exception {
        mvc.perform(get("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, userToken))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value("Mat25"));
    }
}

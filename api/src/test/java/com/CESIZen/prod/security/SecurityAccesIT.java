package com.CESIZen.prod.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessIT {

    @Autowired MockMvc mvc;
    @Autowired JwtUtils jwt;

    @Test
    void userCannotReachAdminRoute() throws Exception {
        String user = "Bearer "+jwt.generateToken("Mat","USER");
        mvc.perform(get("/api/emotions/level1").header("Authorization", user))
           .andExpect(status().isForbidden());
    }
}

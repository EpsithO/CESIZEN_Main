
package com.CESIZen.prod.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationEmotionTrackerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void create_and_get_tracker() throws Exception {
        // Assume a user "demo" already exists loaded by data.sql or previous IT
        var loginResp = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"demo\",\"password\":\"demo\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String token = mapper.readTree(loginResp.getContentAsString()).get("token").asText();

        // create tracker
        mvc.perform(post("/api/trackers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"emotionId\":1,\"description\":\"joyful\",\"date\":\"2025-06-28\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("joyful"));

        // list
        mvc.perform(get("/api/trackers")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("joyful"));
    }
}

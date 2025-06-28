package com.CESIZen.prod;

import org.springframework.test.context.ActiveProfiles;
import com.CESIZen.prod.entity.EmotionLevel1;
import com.CESIZen.prod.entity.EmotionLevel2;
import com.CESIZen.prod.repository.EmotionLevel1Repository;
import com.CESIZen.prod.repository.EmotionLevel2Repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.CESIZen.prod.config.TestSecurityConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmotionLevel1Repository level1Repo;

    @Autowired
    private EmotionLevel2Repository level2Repo;

    @Autowired
    private ObjectMapper mapper;

    private String randomUsername() {
        return "user_" + UUID.randomUUID();
    }

    private void register(String username, String email, String password) throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"email\":\"%s\",
                      \"password\":\"%s\"
                    }
                """.formatted(username, email, password)))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult res = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"password\":\"%s\"
                    }
                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = mapper.readTree(res.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    private Long createEmotion(String name) {
        EmotionLevel1 level1 = new EmotionLevel1();
        level1.setName("Positif");
        level1 = level1Repo.save(level1);
        EmotionLevel2 level2 = new EmotionLevel2();
        level2.setName(name);
        level2.setParent(level1);
        level2 = level2Repo.save(level2);
        return level2.getId();
    }

    // 1 ‑ Création utilisateur ‑ succès
    @Test
    @DisplayName("1 ‑ Création utilisateur ‑ succès")
    void createUserSuccess() throws Exception {
        String username = randomUsername();
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"email\":\"%s@example.com\",
                      \"password\":\"StrongPass123!\"
                    }
                """.formatted(username, username)))
                .andExpect(status().isCreated());
    }

    // 2 ‑ Création utilisateur ‑ email invalide
    @Test
    @DisplayName("2 ‑ Création utilisateur ‑ email invalide")
    void createUserInvalidEmail() throws Exception {
        String username = randomUsername();
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"email\":\"invalid-email\",
                      \"password\":\"StrongPass123!\"
                    }
                """.formatted(username)))
                .andExpect(status().isBadRequest());
    }

    // 3 ‑ Création utilisateur ‑ mot de passe faible
    @Test
    @DisplayName("3 ‑ Création utilisateur ‑ mot de passe faible")
    void createUserWeakPassword() throws Exception {
        String username = randomUsername();
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"email\":\"%s@example.com\",
                      \"password\":\"123\"
                    }
                """.formatted(username, username)))
                .andExpect(status().isBadRequest());
    }

    // 4 ‑ Création utilisateur ‑ email existant
    @Test
    @DisplayName("4 ‑ Création utilisateur ‑ email existant")
    void createUserEmailExists() throws Exception {
        String username1 = randomUsername();
        String email = username1 + "@example.com";
        register(username1, email, "StrongPass123!");
        String username2 = randomUsername();
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"email\":\"%s\",
                      \"password\":\"StrongPass123!\"
                    }
                """.formatted(username2, email)))
                .andExpect(status().isConflict());
    }

    // 5 ‑ Connexion ‑ succès
    @Test
    @DisplayName("5 ‑ Connexion ‑ succès")
    void loginSuccess() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"password\":\"StrongPass123!\"
                    }
                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // 6 ‑ Connexion ‑ mauvais mot de passe
    @Test
    @DisplayName("6 ‑ Connexion ‑ mauvais mot de passe")
    void loginWrongPassword() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s\",
                      \"password\":\"WrongPass!\"
                    }
                """.formatted(username)))
                .andExpect(status().isUnauthorized());
    }

    // 7 ‑ Connexion ‑ email inconnu
    @Test
    @DisplayName("7 ‑ Connexion ‑ email inconnu")
    void loginUnknownEmail() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"unknown\",
                      \"password\":\"whatever\"
                    }
                """))
                .andExpect(status().isNotFound());
    }

    // 8 ‑ Déconnexion
    @Test
    @DisplayName("8 ‑ Déconnexion")
    void logout() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    // 9 ‑ Récupération info utilisateur
    @Test
    @DisplayName("9 ‑ Récupération info utilisateur")
    void getCurrentUser() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    // 10 ‑ Modification email ‑ succès
    @Test
    @DisplayName("10 ‑ Modification email ‑ succès")
    void updateEmailSuccess() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(patch("/me/email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"email\":\"%s_new@example.com\"
                    }
                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(username + "_new@example.com"));
    }

    // 11 ‑ Modification email ‑ email invalide
    @Test
    @DisplayName("11 ‑ Modification email ‑ email invalide")
    void updateEmailInvalid() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(patch("/me/email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"email\":\"invalid-mail\"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    // 12 ‑ Modification email ‑ déjà utilisé
    @Test
    @DisplayName("12 ‑ Modification email ‑ déjà utilisé")
    void updateEmailAlreadyUsed() throws Exception {
        String user1 = randomUsername();
        register(user1, user1 + "@example.com", "StrongPass123!");
        String user2 = randomUsername();
        register(user2, user2 + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(user1, "StrongPass123!");
        mockMvc.perform(patch("/me/email")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"email\":\"%s@example.com\"
                    }
                """.formatted(user2)))
                .andExpect(status().isConflict());
    }

    // 13 ‑ Modification mot de passe ‑ succès
    @Test
    @DisplayName("13 ‑ Modification mot de passe ‑ succès")
    void updatePasswordSuccess() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(patch("/me/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"currentPassword\":\"StrongPass123!\",
                      \"newPassword\":\"NewPass123!\"
                    }
                """))
                .andExpect(status().isOk());
    }

    // 14 ‑ Modification mot de passe ‑ mauvais mot de passe actuel
    @Test
    @DisplayName("14 ‑ Modification mot de passe ‑ mauvais mot de passe actuel")
    void updatePasswordBadCurrent() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(patch("/me/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"currentPassword\":\"WrongCurrent!\",
                      \"newPassword\":\"NewPass123!\"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    // 15 ‑ Modification profil ‑ succès
    @Test
    @DisplayName("15 ‑ Modification profil ‑ succès")
    void updateProfile() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(patch("/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"username\":\"%s_updated\"
                    }
                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username + "_updated"));
    }

    // 16 ‑ Suppression utilisateur ‑ succès
    @Test
    @DisplayName("16 ‑ Suppression utilisateur ‑ succès")
    void deleteUser() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(delete("/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    // 17 ‑ Suppression utilisateur ‑ non autorisé
    @Test
    @DisplayName("17 ‑ Suppression utilisateur ‑ non autorisé")
    void deleteUserUnauthorized() throws Exception {
        mockMvc.perform(delete("/me"))
                .andExpect(status().isUnauthorized());
    }

    // 18 ‑ Récupération liste utilisateurs ‑ admin
    @Test
    @DisplayName("18 ‑ Récupération liste utilisateurs ‑ admin")
    void getUsersAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    // 19 ‑ Récupération liste utilisateurs ‑ non admin
    @Test
    @DisplayName("19 ‑ Récupération liste utilisateurs ‑ non admin")
    void getUsersNonAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .with(user("normal").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // 20 ‑ Filtrage utilisateurs par rôle
    @Test
    @DisplayName("20 ‑ Filtrage utilisateurs par rôle")
    void filterUsersByRole() throws Exception {
        mockMvc.perform(get("/users")
                        .queryParam("role", "user")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    // 21 ‑ Ajout émotion ‑ succès
    @Test
    @DisplayName("21 ‑ Ajout émotion ‑ succès")
    void addEmotionSuccess() throws Exception {
        Long emotionId = createEmotion("Joie");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        
        mockMvc.perform(post("/tracker")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                    "emotionId": %d,
                    "description": "Journée très joyeuse",
                    "date": "%s"
                    }
                """.formatted(emotionId, LocalDate.now())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Journée très joyeuse"))
                .andExpect(jsonPath("$.emotion").value("Joie"))
                .andExpect(jsonPath("$.date").exists());
    }

    // 22 - Ajout émotion - id invalide
    @Test
    @DisplayName("22 - Ajout émotion - id invalide")
    void addEmotionInvalidId() throws Exception {
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(post("/tracker")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":9999,
                      \"description\":\"Triste journée\",
                      \"date\":\"%s\"
                    }
                """.formatted(LocalDate.now())))
                .andExpect(status().isNotFound());
    }

    // 23 - Ajout émotion - date manquante
    @Test
    @DisplayName("23 - Ajout émotion - date manquante")
    void addEmotionMissingDate() throws Exception {
        Long emotionId = createEmotion("Tristesse");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(post("/tracker")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":%d,
                      \"description\":\"Pas de date\"
                    }
                """.formatted(emotionId)))
                .andExpect(status().isBadRequest());
    }

    // 24 - Ajout émotion - utilisateur inconnu
    @Test
    @DisplayName("24 - Ajout émotion - utilisateur inconnu")
    void addEmotionUnauthorized() throws Exception {
        mockMvc.perform(post("/tracker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":1,
                      \"description\":\"Pas autorisé\",
                      \"date\":\"%s\"
                    }
                """.formatted(LocalDate.now())))
                .andExpect(status().isUnauthorized());
    }

    // 25 - Modification émotion - succès
    @Test
    @DisplayName("25 - Modification émotion - succès")
    void updateEmotionSuccess() throws Exception {
        Long emotionId1 = createEmotion("Joie");
        Long emotionId2 = createEmotion("Colère");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        MvcResult result = mockMvc.perform(post("/tracker")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":%d,
                      \"description\":\"Avant\",
                      \"date\":\"%s\"
                    }
                """.formatted(emotionId1, LocalDate.now())))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        Long trackerId = node.get("id").asLong();
        mockMvc.perform(patch("/tracker/" + trackerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":%d,
                      \"description\":\"Après\",
                      \"date\":\"%s\"
                    }
                """.formatted(emotionId2, LocalDate.now())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Après"));
    }

    // 26 - Modification émotion - non trouvé
    @Test
    @DisplayName("26 - Modification émotion - non trouvé")
    void updateEmotionNotFound() throws Exception {
        Long emotionId = createEmotion("Surprise");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(patch("/tracker/9999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":%d,
                      \"description\":\"Inexistant\",
                      \"date\":\"%s\"
                    }
                """.formatted(emotionId, LocalDate.now())))
                .andExpect(status().isNotFound());
    }

    // 27 - Suppression émotion - succès
    @Test
    @DisplayName("27 - Suppression émotion - succès")
    void deleteEmotionSuccess() throws Exception {
        Long emotionId = createEmotion("Peur");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        MvcResult result = mockMvc.perform(post("/tracker")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      \"emotionId\":%d,
                      \"description\":\"À supprimer\",
                      \"date\":\"%s\"
                    }
                """.formatted(emotionId, LocalDate.now())))
                .andReturn();
        Long trackerId = mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(delete("/tracker/" + trackerId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    // 28 - Suppression émotion - non autorisé
    @Test
    @DisplayName("28 - Suppression émotion - non autorisé")
    void deleteEmotionUnauthorized() throws Exception {
        mockMvc.perform(delete("/tracker/1"))
                .andExpect(status().isUnauthorized());
    }

    // 29 - Récupération émotions utilisateur
    @Test
    @DisplayName("29 - Récupération émotions utilisateur")
    void getUserEmotions() throws Exception {
        Long emotionId = createEmotion("Gratitude");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(post("/tracker")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              \"emotionId\":%d,
              \"description\":\"Émotion test\",
              \"date\":\"%s\"
            }
        """.formatted(emotionId, LocalDate.now())))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/tracker")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emotion").value("Gratitude"));
    }

    // 30 - Statistiques émotions utilisateur
    @Test
    @DisplayName("30 - Statistiques émotions utilisateur")
    void getEmotionStats() throws Exception {
        Long emotionId = createEmotion("Colère");
        String username = randomUsername();
        register(username, username + "@example.com", "StrongPass123!");
        String token = loginAndGetToken(username, "StrongPass123!");
        mockMvc.perform(post("/tracker")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              \"emotionId\":%d,
              \"description\":\"Émotion colère\",
              \"date\":\"%s\"
            }
        """.formatted(emotionId, LocalDate.now())))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/tracker/report")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Colère").value(1));
    }
}

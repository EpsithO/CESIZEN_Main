package com.CESIZen.prod.user;   

import com.CESIZen.prod.dto.MessageDTO;
import com.CESIZen.prod.dto.user.UpdateUserDTO;
import com.CESIZen.prod.dto.user.UserDTO;
import com.CESIZen.prod.entity.*;
import com.CESIZen.prod.exception.NotFoundException;
import com.CESIZen.prod.repository.PasswordResetTokenRepository;
import com.CESIZen.prod.repository.UserRepository;
import com.CESIZen.prod.security.SecurityUtils;
import com.CESIZen.prod.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks UserService userService;

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock SecurityUtils securityUtils;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock Authentication authentication;

    private AutoCloseable closeable;
    private User user;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);

        Role role = new Role();
        role.setName(RoleEnum.USER);

        user = new User();
        user.setId(1L);
        user.setUsername("matheo");
        user.setEmail("matheo@mail.com");
        user.setPassword("hashed-pwd");
        user.setRole(role);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setDeleted(false);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // … tests inchangés sauf ⇩⇩⇩

    @Test
    void deleteCurrentUser_shouldSoftDelete() {
        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);

        MessageDTO msg = userService.deleteCurrentUser(authentication);

        assertTrue(user.isDeleted());
        assertEquals("Compte supprimé", msg.getMessage()); // ✅ getter
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserById_shouldSoftDeleteAndReturnMessage() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(user));

        MessageDTO msg = userService.deleteUserById(1L);

        assertTrue(user.isDeleted());
        assertEquals("Utilisateur supprimé", msg.getMessage()); // ✅ getter
    }

    @Test
    void requestDataDeletion_shouldDeleteTokensAndUser() {
        when(securityUtils.getCurrentUser(authentication)).thenReturn(user);

        MessageDTO msg = userService.requestDataDeletion(authentication);

        verify(passwordResetTokenRepository).deleteAllByUser(user);
        verify(userRepository).delete(user);
        assertEquals("Demande de suppression des données enregistrée", msg.getMessage()); // ✅ getter
    }
}

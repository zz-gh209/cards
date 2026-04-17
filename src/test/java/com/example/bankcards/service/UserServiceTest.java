package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void register_newUser_success() {
        String username = "newuser";
        String password = "pass";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User registered = userService.register(username, password, Role.USER);

        assertNotNull(registered);
        assertEquals(username, registered.getUsername());
        assertNotEquals(password, registered.getPassword()); // пароль закодирован
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsException() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.register("existing", "pass", Role.USER));
    }

    @Test
    void findByUsername_exists() {
        User user = new User();
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("john");
        assertEquals("john", found.getUsername());
    }

    @Test
    void findByUsername_notExists_throwsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByUsername("unknown"));
    }
}
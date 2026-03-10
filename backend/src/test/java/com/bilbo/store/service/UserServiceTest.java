package com.bilbo.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.bilbo.store.client.Auth0Client;
import com.bilbo.store.client.dto.Auth0User;
import com.bilbo.store.entites.User;
import com.bilbo.store.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Auth0Client auth0Client;

    @InjectMocks
    private UserService service;

    private final String testSub = "auth0|123";

    @Test
    void testProcessUser_NewUser() {
        Auth0User auth0User = new Auth0User();
        auth0User.setUserId(testSub);
        auth0User.setEmail("test@example.com");
        auth0User.setName("John");
        auth0User.setNickname("Johnny");

        when(auth0Client.getUser(testSub)).thenReturn(Mono.just(auth0User));
        when(userRepository.findBySub(testSub)).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setSub(testSub);
        savedUser.setEmail("test@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = service.processUser(testSub);

        assertNotNull(result);
        assertEquals(testSub, result.getSub());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testProcessUser_ExistingUser_NeedsUpdate() {
        Auth0User auth0User = new Auth0User();
        auth0User.setUserId(testSub);
        auth0User.setEmail("new@example.com");
        auth0User.setName("John");
        auth0User.setNickname("Johnny");

        User existingUser = new User();
        existingUser.setSub(testSub);
        existingUser.setUsername(testSub);
        existingUser.setEmail("old@example.com");
        existingUser.setFirstName("John");
        existingUser.setLastName("Johnny");

        when(auth0Client.getUser(testSub)).thenReturn(Mono.just(auth0User));
        when(userRepository.findBySub(testSub)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = service.processUser(testSub);

        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testProcessUser_ExistingUser_Unchanged() {
        Auth0User auth0User = new Auth0User();
        auth0User.setUserId(testSub);
        auth0User.setEmail("test@example.com");
        auth0User.setName("John");
        auth0User.setNickname("Johnny");

        User existingUser = new User();
        existingUser.setSub(testSub);
        existingUser.setUsername(testSub);
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("John");
        existingUser.setLastName("Johnny");

        when(auth0Client.getUser(testSub)).thenReturn(Mono.just(auth0User));
        when(userRepository.findBySub(testSub)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = service.processUser(testSub);

        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testProcessUser_Auth0ReturnsNull() {
        when(auth0Client.getUser(testSub)).thenReturn(Mono.empty());

        assertThrows(IllegalStateException.class, () -> service.processUser(testSub));
        verify(userRepository, never()).findBySub(any());
        verify(userRepository, never()).save(any());
    }
}

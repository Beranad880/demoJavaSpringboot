package com.example.demojavaspringboot.crud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user = new User(1L, "Jan", "jan@example.com", 5);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Jan", result.get(0).getName());
        assertEquals(5, result.get(0).getOrderCount());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User(1L, "Jan", "jan@example.com", 3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("jan@example.com", result.get().getEmail());
        assertEquals(3, result.get().getOrderCount());
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        User user = new User("Jan", "jan@example.com", 2);
        User savedUser = new User(1L, "Jan", "jan@example.com", 2);
        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.createUser(user);

        assertNotNull(result.getId());
        assertEquals("Jan", result.getName());
        assertEquals(2, result.getOrderCount());
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateAndSave() {
        User existingUser = new User(1L, "Jan", "jan@example.com", 2);
        User updatedDetails = new User("Jan Novak", "jan.novak@example.com", 10);
        User savedUser = new User(1L, "Jan Novak", "jan.novak@example.com", 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(savedUser);

        Optional<User> result = userService.updateUser(1L, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals("Jan Novak", result.get().getName());
        assertEquals("jan.novak@example.com", result.get().getEmail());
        assertEquals(10, result.get().getOrderCount());
    }

    @Test
    void deleteUser_whenExists_shouldDeleteAndReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean deleted = userService.deleteUser(1L);

        assertTrue(deleted);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_whenDoesNotExist_shouldReturnFalse() {
        when(userRepository.existsById(99L)).thenReturn(false);

        boolean deleted = userService.deleteUser(99L);

        assertFalse(deleted);
        verify(userRepository, never()).deleteById(99L);
    }
}

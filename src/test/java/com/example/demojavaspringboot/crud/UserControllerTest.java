package com.example.demojavaspringboot.crud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({WebConfig.class, ApiKeyInterceptor.class})
class UserControllerTest {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String VALID_API_KEY = "my-secret-api-key";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void requestWithoutApiKey_shouldReturn401Unauthorized() throws Exception {
        mockMvc.perform(get("/crud/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Neplatný nebo chybějící API klíč."));
    }

    @Test
    void requestWithInvalidApiKey_shouldReturn401Unauthorized() throws Exception {
        mockMvc.perform(get("/crud/users")
                        .header(API_KEY_HEADER, "wrong-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void getAllUsers_withValidApiKey_shouldReturnList() throws Exception {
        User user1 = new User(1L, "Jan Novak", "jan@example.com");
        User user2 = new User(2L, "Petr Svoboda", "petr@example.com");
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/crud/users")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Jan Novak"))
                .andExpect(jsonPath("$[1].name").value("Petr Svoboda"));
    }

    @Test
    void getUserById_whenFound_shouldReturnUser() throws Exception {
        User user = new User(1L, "Jan Novak", "jan@example.com");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/crud/users/1")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jan Novak"))
                .andExpect(jsonPath("$.email").value("jan@example.com"));
    }

    @Test
    void getUserById_whenNotFound_shouldReturn404() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/crud/users/99")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        User savedUser = new User(1L, "Jan Novak", "jan@example.com");
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/crud/users")
                        .header(API_KEY_HEADER, VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jan Novak\",\"email\":\"jan@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jan Novak"))
                .andExpect(jsonPath("$.email").value("jan@example.com"));
    }

    @Test
    void updateUser_whenFound_shouldReturnUpdatedUser() throws Exception {
        User updatedUser = new User(1L, "Jan Aktualizovany", "jan.updated@example.com");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/crud/users/1")
                        .header(API_KEY_HEADER, VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jan Aktualizovany\",\"email\":\"jan.updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jan Aktualizovany"))
                .andExpect(jsonPath("$.email").value("jan.updated@example.com"));
    }

    @Test
    void deleteUser_whenFound_shouldReturn204() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/crud/users/1")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenNotFound_shouldReturn404() throws Exception {
        when(userService.deleteUser(99L)).thenReturn(false);

        mockMvc.perform(delete("/crud/users/99")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isNotFound());
    }
}

package dev.rm.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import dev.rm.recipes.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserServiceTests {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private UserService userService;

  private static final String BASE_URL = "http://localhost:8092";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userService.setBackendUrl(BASE_URL);
  }

  @Test
  void testGetUserByIdSuccess() {
    // Arrange
    User user = new User(1L, "user1", "user1@example.com", "password",
        "password", "ROLE_USER");
    ResponseEntity<User> responseEntity = new ResponseEntity<>(user,
        HttpStatus.OK);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.GET),
        isNull(), eq(User.class)))
        .thenReturn(responseEntity);

    // Act
    User result = userService.getUserById(1L);

    // Assert
    assertNotNull(result);
    assertEquals("user1", result.getUsername());
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"),
        eq(HttpMethod.GET), isNull(), eq(User.class));
  }

  @Test
  void testCreateUserSuccess() {
    // Arrange
    User newUser = new User(null, "user2", "user2@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "created"),
        HttpStatus.CREATED);
    when(restTemplate.exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.createUser(newUser);

    // Assert
    assertTrue(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testUpdateUserFailure() {
    // Arrange
    User userToUpdate = new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "not_found"),
        HttpStatus.NOT_FOUND);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testDeleteUserSuccess() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertTrue(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class));
  }

  @Test
  void testDeleteUserFailure() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    when(restTemplate.exchange(eq(BASE_URL + "/users/999"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(999L);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/999"), eq(HttpMethod.DELETE), isNull(),
        eq(Void.class));
  }

  @Test
  void testGetUsersSuccess() {
    // Arrange
    List<User> expectedUsers = Collections
        .singletonList(new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER"));
    ResponseEntity<List<User>> responseEntity = new ResponseEntity<>(expectedUsers, HttpStatus.OK);
    when(restTemplate.exchange(eq(BASE_URL + "/users"), eq(HttpMethod.GET), isNull(),
        eq(new ParameterizedTypeReference<List<User>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    List<User> users = userService.getUsers();

    // Assert
    assertNotNull(users);
    assertEquals(1, users.size());
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users"), eq(HttpMethod.GET), isNull(),
        eq(new ParameterizedTypeReference<List<User>>() {
        }));
  }

  @Test
  void testCreateUserBadRequest() {
    // Arrange
    User newUser = new User(null, "user2", "user2@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "bad_request"),
        HttpStatus.BAD_REQUEST);
    when(restTemplate.exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.createUser(newUser);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testCreateUserInternalServerError() {
    // Arrange
    User newUser = new User(null, "user3", "user3@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "internal_error"),
        HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.createUser(newUser);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testDeleteUserBadRequest() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class));
  }

  @Test
  void testCreateUserException() {
    // Arrange
    User newUser = new User(null, "user4", "user4@example.com", "password", "password", "ROLE_USER");
    when(restTemplate.exchange(eq(BASE_URL + "/users"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenThrow(new RestClientException("Network error"));

    // Act
    boolean result = userService.createUser(newUser);

    // Assert
    assertFalse(result);
  }

  @Test
  void testDeleteUserException() {
    // Arrange
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenThrow(new RestClientException("Network error"));

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertFalse(result);
  }

  @Test
  void testUpdateUserSuccess() {
    // Arrange
    User userToUpdate = new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "updated"),
        HttpStatus.OK);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertTrue(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testUpdateUserNotFound() {
    // Arrange
    User userToUpdate = new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "not_found"),
        HttpStatus.NOT_FOUND);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testUpdateUserBadRequest() {
    // Arrange
    User userToUpdate = new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "bad_request"),
        HttpStatus.BAD_REQUEST);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testUpdateUserInternalServerError() {
    // Arrange
    User userToUpdate = new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "internal_error"),
        HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testUpdateUserException() {
    // Arrange
    User userToUpdate = new User(1L, "user1", "user1@example.com", "password", "password", "ROLE_USER");
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenThrow(new RestClientException("Network error"));

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertFalse(result);
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testUpdateUserInvalidUserData() {
    // Arrange: Invalid user data, e.g., missing required fields like username or
    // email.
    User userToUpdate = new User(1L, "", "", "password", "password", "ROLE_USER");
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(Map.of("status", "bad_request"),
        HttpStatus.BAD_REQUEST); // Invalid input

    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.updateUser(userToUpdate);

    // Assert
    assertFalse(result); // Expect failure because of invalid user data
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.PUT), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testDeleteUserNotFound() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertFalse(result); // Expect failure because the user was not found
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class));
  }

  @Test
  void testDeleteUserInternalServerError() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertFalse(result); // Expect failure because of a server error
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class));
  }

  @Test
  void testDeleteUserForbidden() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertFalse(result); // Expect failure because the user is not allowed to delete
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class));
  }

  @Test
  void testDeleteUserAlreadyDeleted() {
    // Arrange
    ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND); // Simulating user already deleted
    when(restTemplate.exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
        .thenReturn(responseEntity);

    // Act
    boolean result = userService.deleteUser(1L);

    // Assert
    assertFalse(result); // Expect failure as the user is already deleted (404)
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/users/1"), eq(HttpMethod.DELETE), isNull(), eq(Void.class));
  }
}

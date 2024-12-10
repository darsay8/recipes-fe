package dev.rm.recipes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import dev.rm.recipes.model.User;

import java.util.Map;

public class AuthServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private AuthService authService;

  private static final String BASE_URL = "http://localhost:8092";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    authService.setBackendUrl(BASE_URL);
  }

  @Test
  void testAuthenticateSuccess() {

    User user = new User(null, "user", "user@mail.com", "password", "password", "ROLE_USER");

    Map<String, Object> expectedResponse = Map.of(
        "token",
        "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpb",
        "username", "user",
        "email", "user@mail.com",
        "role", "ADMIN");

    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

    when(restTemplate.exchange(eq(BASE_URL + "/auth/login"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    Map<String, Object> result = authService.authenticate(user);

    assertNotNull(result);
    assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJlbWFpb", result.get("token"));
    assertEquals("user", result.get("username"));
    assertEquals("user@mail.com", result.get("email"));
    assertEquals("ADMIN", result.get("role"));
    verify(restTemplate, times(1)).exchange(eq(BASE_URL + "/auth/login"), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        }));
  }

  @Test
  void testAuthenticate_Failure() {

    User user = new User(1L, "user1", "user1@example.com", "password",
        "password", "ROLE_USER");

    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    when(restTemplate.exchange(eq(BASE_URL + "/auth/login"),
        eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    Map<String, Object> result = authService.authenticate(user);

    assertNull(result);
  }

  @Test
  void testRegister_Success() {

    User user = new User(1L, "user1", "user1@example.com", "password",
        "password", "ROLE_USER");
    Map<String, Object> mockResponse = Map.of(
        "token", "mockToken",
        "username", "username",
        "email", "email@example.com",
        "role", "USER");

    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.exchange(eq(BASE_URL + "/auth/register"),
        eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    Map<String, Object> result = authService.register(user);

    assertNotNull(result);
    assertEquals("mockToken", result.get("token"));
  }

  @Test
  void testRegister_Failure() {

    User user = new User(1L, "user1", "user1@example.com", "password",
        "password", "ROLE_USER");

    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    when(restTemplate.exchange(eq(BASE_URL + "/auth/register"),
        eq(HttpMethod.POST), any(HttpEntity.class),
        eq(new ParameterizedTypeReference<Map<String, Object>>() {
        })))
        .thenReturn(responseEntity);

    Map<String, Object> result = authService.register(user);

    assertNull(result);
  }
}

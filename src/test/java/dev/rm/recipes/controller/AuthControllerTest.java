package dev.rm.recipes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.AuthService;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  private MockHttpSession session;

  @BeforeEach
  public void setUp() {
    session = new MockHttpSession();
    session.setAttribute("token",
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImphbWVzQG1haWwuY29tIiwidXNlcm5hbWUiOiJqYW1lcyIsInJvbGVzIjoiQURNSU4iLCJzdWIiOiJqYW1lc0BtYWlsLmNvbSIsImlhdCI6MTczMzM0NTAxNiwiZXhwIjoxNzMzNDMxNDE2fQ.bD8YpWMxe3aUSAbmzuCJdTakESQ9paWhKkZhJhY9aTM");
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @Test
  void testLoginPage() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/login")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("login-view"));
  }

  @Test
  void testLogin_Success() throws Exception {
    User user = new User(1L, "user1", "user1@example.com",
        "password", "password", "USER");
    Map<String, Object> mockResponse = Map.of(
        "token", "mockToken",
        "username", "username",
        "email", "email@example.com",
        "role", "USER");

    when(authService.authenticate(any(User.class))).thenReturn(mockResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .session(session)
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/recipes"));
  }

  @Test
  void testLogin_Failure() throws Exception {
    User user = new User(1L, "user1", "user1@example.com",
        "password", "password", "USER");

    when(authService.authenticate(any(User.class))).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("login-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"));
  }

  @Test
  void testRegisterPage() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/register")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("register-view"));
  }

  @Test
  void testRegister_Success() throws Exception {
    User user = new User(1L, "user1", "user1@example.com",
        "password", "password", "USER");
    Map<String, Object> mockResponse = Map.of(
        "token", "mockToken",
        "username", "username",
        "email", "email@example.com",
        "role", "USER");

    when(authService.register(any(User.class))).thenReturn(mockResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/register")
        .session(session)
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/recipes"));
  }

  @Test
  void testRegister_Failure() throws Exception {
    User user = new User(1L, "user1", "user1@example.com",
        "password", "password", "USER");

    when(authService.register(any(User.class))).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/register")
        .session(session)
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("register-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"));
  }

  @Test
  void testLogin_Failure_InvalidCredentials() throws Exception {
    User user = new User(1L, "user1", "user1@example.com", "password", "password", "USER");

    when(authService.authenticate(any(User.class))).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("login-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
        .andExpect(MockMvcResultMatchers.model().attribute("error", "Invalid credentials"));
  }

  @Test
  void testLogin_Failure_MissingCredentials() throws Exception {
    User user = new User(1L, "", "", "password", "password", "USER");

    mockMvc.perform(MockMvcRequestBuilders.post("/login")
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("login-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
        .andExpect(MockMvcResultMatchers.model().attribute("error", "Invalid credentials"));
  }

  @Test
  void testRegister_Failure_PasswordMismatch() throws Exception {
    User user = new User(1L, "user1", "user1@example.com", "password", "differentPassword", "USER");

    mockMvc.perform(MockMvcRequestBuilders.post("/register")
        .session(session)
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("register-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
        .andExpect(MockMvcResultMatchers.model().attribute("error", "Passwords do not match."));
  }

  @Test
  void testRegister_Failure_ExistingUser() throws Exception {
    User user = new User(1L, "user1", "user1@example.com", "password", "password", "USER");

    when(authService.register(any(User.class))).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/register")
        .session(session)
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("register-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
        .andExpect(MockMvcResultMatchers.model().attribute("error", "Registration failed. Please try again."));
  }

  @Test
  void testRegister_Failure_MissingFields() throws Exception {
    User user = new User(1L, "", "", "", "", "USER");

    mockMvc.perform(MockMvcRequestBuilders.post("/register")
        .session(session)
        .flashAttr("user", user))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("register-view"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
        .andExpect(MockMvcResultMatchers.model().attribute("error", "Registration failed. Please try again."));
  }
}

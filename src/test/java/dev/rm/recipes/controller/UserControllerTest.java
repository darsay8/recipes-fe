package dev.rm.recipes.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dev.rm.recipes.model.User;
import dev.rm.recipes.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private MockHttpSession session;

  @BeforeEach
  public void setUp() {
    session = new MockHttpSession();
    session.setAttribute("token",
        "Bearer fewfweM");
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  void testGetUsersToAdmin() throws Exception {

    // Arrange
    List<User> users = List.of(new User(1L, "user1", "user1@example.com",
        "password", "password", "USER"));
    when(userService.getUsers()).thenReturn(users);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("admin-users"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("users"));

    verify(userService, times(1)).getUsers();
  }

  @Test
  void testShowEditUserForm() throws Exception {
    // Arrange
    User user = new User(1L, "user1", "user1@example.com",
        "password", "password", "USER");
    when(userService.getUserById(1L)).thenReturn(user);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/admin/users/1")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("admin-users"))
        .andExpect(MockMvcResultMatchers.model().attributeExists("user"));

    verify(userService, times(1)).getUserById(1L);
  }

  @Test
  void testCreateOrUpdateUser() throws Exception {
    // Arrange
    when(userService.createUser(any(User.class))).thenReturn(true);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user2")
        .param("email", "user2@example.com")
        .param("password", "password")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"));

    verify(userService, times(1)).createUser(any(User.class));
  }

  @Test
  void testDeleteUser() throws Exception {
    // Arrange
    when(userService.deleteUser(1L)).thenReturn(true);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/1")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"));

    verify(userService, times(1)).deleteUser(1L);
  }

  @Test
  void testGetUsersWhenTokenIsMissing() throws Exception {
    // Arrange: No token in session
    session.removeAttribute("token");

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login")); // Expecting a redirect to login

    verify(userService, never()).getUsers(); // Ensure userService is not called
  }

  @Test
  void testCreateOrUpdateUserWhenUpdatingExistingUser() throws Exception {
    // Arrange: Existing user mock
    User existingUser = new User(1L, "user1", "user1@example.com", "oldPassword", "oldPassword", "USER");
    when(userService.getUserById(1L)).thenReturn(existingUser);
    when(userService.createUser(any(User.class))).thenReturn(true);

    // Act & Assert: Perform update on existing user
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("id", "1")
        .param("username", "user1")
        .param("email", "user1@example.com")
        .param("password", "newPassword")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"));

    verify(userService, times(1)).createUser(argThat(user -> user.getPassword().equals("newPassword") &&
        user.getUserId() == null));
  }

  @Test
  void testDeleteUserWhenDeletionSucceeds() throws Exception {
    // Arrange: Mock service to simulate success
    when(userService.deleteUser(1L)).thenReturn(true); // Simulate successful deletion

    // Act & Assert: Attempt to delete user and check for redirection
    mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/1")
        .session(session)) // Pass the session
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect redirection (3xx)
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users")); // Redirect to /admin/users

    // Verify that deleteUser was called once with the correct userId
    verify(userService, times(1)).deleteUser(1L);
  }

  @Test
  void testCreateOrUpdateUserWhenTokenIsMissing() throws Exception {
    // Arrange: No token in session
    session.removeAttribute("token");

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user2")
        .param("email", "user2@example.com")
        .param("password", "password")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login")); // Expecting a redirect to login

    verify(userService, never()).createUser(any(User.class)); // Ensure userService is not called
  }

  @Test
  void testCreateOrUpdateUserWhenCreationFails() throws Exception {
    // Arrange
    when(userService.createUser(any(User.class))).thenReturn(false);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user2")
        .param("email", "user2@example.com")
        .param("password", "password")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"));

    verify(userService, times(1)).createUser(any(User.class));
  }

  @Test
  void testDeleteUserWhenDeletionFails() throws Exception {
    // Arrange: Mock service to simulate failure
    when(userService.deleteUser(1L)).thenReturn(false); // Simulate failed deletion

    // Act & Assert: Attempt to delete user and check for redirection
    mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/1")
        .session(session)) // Pass the session
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect redirection (3xx)
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users")); // Redirect to /admin/users

    // Verify that deleteUser was called once with the correct userId
    verify(userService, times(1)).deleteUser(1L);
  }

  @Test
  void testCreateUserSuccessfully() throws Exception {
    // Arrange: Valid user input and successful user creation
    when(userService.createUser(any(User.class))).thenReturn(true);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user2")
        .param("email", "user2@example.com")
        .param("password", "password")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection()) // Expect redirection
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users")) // Redirect back to the user list
        .andExpect(MockMvcResultMatchers.flash().attributeExists("message")) // Ensure a success message is set
        .andExpect(MockMvcResultMatchers.flash().attribute("message", "User successfully created."));

    verify(userService, times(1)).createUser(any(User.class)); // Ensure the user creation service was called
  }

  @Test
  void testAccessToAdminUsersPageWhenTokenIsMissing() throws Exception {
    // Arrange
    session.removeAttribute("token");

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
        .session(session))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
  }

  @Test
  void testCreateOrUpdateUserWhenPasswordIsMissingForNewUser() throws Exception {
    // Act & Assert: Try to create a new user without a password
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user3")
        .param("email", "user3@example.com")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
        .andExpect(MockMvcResultMatchers.flash().attribute("message", "Failed to create user. Please try again."));

  }

  @Test
  void testCreateOrUpdateUserWhenUpdateFails() throws Exception {
    // Arrange: Mock failure for user update
    when(userService.getUserById(1L))
        .thenReturn(new User(1L, "user1", "user1@example.com", "oldPassword", "oldPassword", "USER"));
    when(userService.updateUser(any(User.class))).thenReturn(false);

    // Act & Assert: Try to update an existing user
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("id", "1")
        .param("username", "user1")
        .param("email", "user1@example.com")
        .param("password", "newPassword")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
        .andExpect(MockMvcResultMatchers.flash().attribute("message", "Failed to create user. Please try again."));

  }

  @Test
  void testCreateOrUpdateUserWhenPasswordIsNotProvidedForUpdate() throws Exception {
    // Arrange: Existing user mock
    User existingUser = new User(1L, "user1", "user1@example.com", "oldPassword", "oldPassword", "USER");
    when(userService.getUserById(1L)).thenReturn(existingUser);
    when(userService.updateUser(any(User.class))).thenReturn(true);

    // Act & Assert: Perform update on existing user without providing a new
    // password
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("id", "1")
        .param("username", "user1")
        .param("email", "user1@example.com")
        .param("role", "USER")) // No password param
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"));

  }

  @Test
  void testCreateOrUpdateUserWithEmailAlreadyInUse() throws Exception {
    // Arrange: Mock the user service to simulate that the email already exists
    when(userService.createUser(any(User.class))).thenReturn(false); // Fail user creation due to email conflict

    // Act & Assert: Try to create a user with an already used email
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user2")
        .param("email", "user1@example.com") // Same email as an existing user
        .param("password", "password")
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
        .andExpect(MockMvcResultMatchers.flash().attribute("message", "Failed to create user. Please try again."));

    verify(userService, times(1)).createUser(any(User.class));
  }

  @Test
  void testCreateOrUpdateUserWithShortPassword() throws Exception {
    // Arrange: Password too short
    when(userService.createUser(any(User.class))).thenReturn(false); // Simulate failure

    // Act & Assert: Try creating a user with a password that's too short (less than
    // 6 characters)
    mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
        .session(session)
        .param("username", "user2")
        .param("email", "user2@example.com")
        .param("password", "short") // Password too short
        .param("role", "USER"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/users"))
        .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
        .andExpect(MockMvcResultMatchers.flash().attribute("message", "Failed to create user. Please try again."));

    verify(userService, times(1)).createUser(any(User.class));
  }

}

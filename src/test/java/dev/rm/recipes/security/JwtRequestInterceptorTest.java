package dev.rm.recipes.security;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.http.client.*;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class JwtRequestInterceptorTest {

  @Mock
  private HttpSession mockSession;
  @Mock
  private ClientHttpRequest mockRequest;
  @Mock
  private ClientHttpRequestExecution mockExecution;

  private JwtRequestInterceptor interceptor;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    interceptor = new JwtRequestInterceptor(mockSession);
  }

  @Test
  public void testInterceptAddsAuthorizationHeaderWhenTokenExists() throws IOException {
    // Arrange
    String mockToken = "mock-jwt-token";
    when(mockSession.getAttribute("token")).thenReturn(mockToken);

    HttpHeaders mockHeaders = mock(HttpHeaders.class);
    when(mockRequest.getHeaders()).thenReturn(mockHeaders);

    byte[] body = new byte[0];

    // Act
    interceptor.intercept(mockRequest, body, mockExecution);

    // Assert
    verify(mockHeaders).add("Authorization", "Bearer " + mockToken);
    verify(mockHeaders).add("Content-Type", "application/json");
    verify(mockExecution).execute(mockRequest, body);
  }

  @Test
  public void testInterceptDoesNotAddAuthorizationHeaderWhenTokenIsNull() throws IOException {
    // Arrange
    when(mockSession.getAttribute("token")).thenReturn(null);

    HttpHeaders mockHeaders = mock(HttpHeaders.class);
    when(mockRequest.getHeaders()).thenReturn(mockHeaders);

    byte[] body = new byte[0];

    // Act
    interceptor.intercept(mockRequest, body, mockExecution);

    // Assert
    verify(mockHeaders, never()).add("Authorization", "Bearer ");
    verify(mockHeaders).add("Content-Type", "application/json");
    verify(mockExecution).execute(mockRequest, body);
  }

  @Test
  public void testInterceptDoesNotAddAuthorizationHeaderWhenTokenIsEmpty() throws IOException {
    // Arrange
    when(mockSession.getAttribute("token")).thenReturn("");

    HttpHeaders mockHeaders = mock(HttpHeaders.class);
    when(mockRequest.getHeaders()).thenReturn(mockHeaders);

    byte[] body = new byte[0];

    // Act
    interceptor.intercept(mockRequest, body, mockExecution);

    // Assert
    verify(mockHeaders, never()).add("Authorization", "Bearer ");
    verify(mockHeaders).add("Content-Type", "application/json");
    verify(mockExecution).execute(mockRequest, body);
  }
}

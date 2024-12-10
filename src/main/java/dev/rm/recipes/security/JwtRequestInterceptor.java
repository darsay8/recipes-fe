package dev.rm.recipes.security;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import jakarta.servlet.http.HttpSession;

public class JwtRequestInterceptor implements ClientHttpRequestInterceptor {

  private final HttpSession session;

  public JwtRequestInterceptor(HttpSession session) {
    this.session = session;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    String token = (String) session.getAttribute("token");

    // Always add Content-Type
    request.getHeaders().add("Content-Type", "application/json");

    if (token != null && !token.isEmpty()) {
      request.getHeaders().add("Authorization", "Bearer " + token);
    }

    return execution.execute(request, body);
  }
}

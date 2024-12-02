package dev.rm.recipes.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

  @Value("${backend.url}")
  private String backendUrl;

  private final RestTemplate restTemplate;

  public AuthService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Map<String, Object> authenticate(User user) {

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        backendUrl + "/auth/login",
        HttpMethod.POST,
        new HttpEntity<>(user),
        new ParameterizedTypeReference<Map<String, Object>>() {
        });

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }

    return null;
  }

  public Map<String, Object> register(User user) {

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        backendUrl + "/auth/register",
        HttpMethod.POST,
        new HttpEntity<>(user),
        new ParameterizedTypeReference<Map<String, Object>>() {
        });

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }

    return null;
  }

}

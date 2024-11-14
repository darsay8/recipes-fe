package dev.rm.recipes.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
@Service
public class UserService {

  @Value("${backend.url}")
  private String backendUrl;

  private final RestTemplate restTemplate;

  public UserService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String authenticate(User user) {
    log.info("AUTHENTICATING: " + user);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        backendUrl + "/auth/login",
        HttpMethod.POST,
        entity,
        new ParameterizedTypeReference<Map<String, Object>>() {
        });

    log.info("RESPONSE: " + response);

    if (response.getStatusCode() == HttpStatus.OK) {
      Map<String, Object> body = response.getBody();
      return (String) body.get("token");
    }

    return null;
  }

  public String register(User user) {
    log.info("REGISTERING: " + user);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        backendUrl + "/auth/register",
        HttpMethod.POST,
        entity,
        new ParameterizedTypeReference<Map<String, Object>>() {
        });

    log.info("RESPONSE: " + response);

    if (response.getStatusCode() == HttpStatus.OK) {
      Map<String, Object> body = response.getBody();
      return (String) body.get("token");
    }

    return null;
  }
}
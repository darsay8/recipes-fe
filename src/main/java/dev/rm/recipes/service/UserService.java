package dev.rm.recipes.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import dev.rm.recipes.model.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import java.util.List;
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

  public Map<String, Object> authenticate(User user) {
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

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }

    return null;
  }

  public Map<String, Object> register(User user) {
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
      return response.getBody();
    }

    return null;
  }

  public List<User> getUsers(String token) {
    log.info("GETTING USERS: " + token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<User[]> response = restTemplate.exchange(
        backendUrl + "/users",
        HttpMethod.GET,
        entity,
        User[].class);

    log.info("RESPONSE: " + response);

    if (response.getStatusCode() == HttpStatus.OK) {
      return List.of(response.getBody());
    }

    return null;
  }

  public User getUserById(String token, Long userId) {
    log.info("GETTING USER BY ID: " + userId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<User> response = restTemplate.exchange(
        backendUrl + "/users/" + userId,
        HttpMethod.GET,
        entity,
        User.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }

    return null;
  }

  public boolean createUser(String token, User user) {
    log.info("CREATING USER: " + user);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    try {
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          backendUrl + "/users",
          HttpMethod.POST,
          entity,
          new ParameterizedTypeReference<Map<String, Object>>() {
          });

      log.info("RESPONSE: " + response);

      if (response.getStatusCode() == HttpStatus.CREATED) {
        return true;
      } else {
        log.error("Failed to create user. Status: " + response.getStatusCode());
        return false;
      }
    } catch (RestClientException e) {
      log.error("Error while creating user", e);
      return false;
    }
  }

  public boolean updateUser(String token, User user) {
    log.info("UPDATING USER: " + user);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<User> entity = new HttpEntity<>(user, headers);

    try {
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          backendUrl + "/users/" + user.getUserId(),
          HttpMethod.PUT,
          entity,
          new ParameterizedTypeReference<Map<String, Object>>() {
          });

      log.info("RESPONSE: " + response);

      if (response.getStatusCode() == HttpStatus.OK) {
        return true;
      } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.warn("User with ID " + user.getUserId() + " not found.");
        return false;
      } else {
        log.error("Failed to update user with ID " + user.getUserId() + ". Status: " + response.getStatusCode());
        return false;
      }
    } catch (RestClientException e) {
      log.error("Error while updating user with ID: " + user.getUserId(), e);
      return false;
    }
  }

  public boolean deleteUser(String token, Long userId) {
    log.info("DELETING USER: " + userId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + token);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<Void> response = restTemplate.exchange(
          backendUrl + "/users/" + userId,
          HttpMethod.DELETE,
          entity,
          Void.class);

      log.info("RESPONSE: " + response);

      if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
        return true;
      } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.warn("User with ID " + userId + " not found.");
        return false;
      } else {
        log.error("Failed to delete user with ID " + userId + ". Status: " + response.getStatusCode());
        return false;
      }
    } catch (RestClientException e) {
      log.error("Error while deleting user with ID: " + userId, e);
      return false;
    }
  }

}
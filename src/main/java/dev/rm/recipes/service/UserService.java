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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Slf4j
@Service
public class UserService {

  @Value("${backend.url}")
  private String backendUrl;

  private final RestTemplate restTemplate;

  public UserService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<User> getUsers() {

    ResponseEntity<List<User>> response = restTemplate.exchange(
        backendUrl + "/users",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<User>>() {
        });

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }

    return null;
  }

  public User getUserById(Long userId) {

    ResponseEntity<User> response = restTemplate.exchange(
        backendUrl + "/users/" + userId,
        HttpMethod.GET,
        null,
        User.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    }

    return null;
  }

  public boolean createUser(User user) {

    try {
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          backendUrl + "/users",
          HttpMethod.POST,
          new HttpEntity<>(user),
          new ParameterizedTypeReference<Map<String, Object>>() {
          });

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

  public boolean updateUser(User user) {

    try {
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          backendUrl + "/users/" + user.getUserId(),
          HttpMethod.PUT,
          new HttpEntity<>(user),
          new ParameterizedTypeReference<Map<String, Object>>() {
          });

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

  public boolean deleteUser(Long userId) {
    try {
      ResponseEntity<Void> response = restTemplate.exchange(
          backendUrl + "/users/" + userId,
          HttpMethod.DELETE,
          null,
          Void.class);

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
# ====================================================================
# Feature: Authentication Module – API Tests
# Tester: Asarak A.
# Module: Authentication + Sales (access control)
# ====================================================================
@API @AuthAPI @Asarak
Feature: Authentication API Tests

  # ------------------------------------------------------------------
  # TC_AUTH_API_01
  # ------------------------------------------------------------------
  @TC_AUTH_API_01
  Scenario: TC_AUTH_API_01 – Admin can login via API and receive JWT token
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "admin", "password": "admin123"}
      """
    Then the response status should be 200
    And the response body should contain "token"

  # ------------------------------------------------------------------
  # TC_AUTH_API_02
  # ------------------------------------------------------------------
  @TC_AUTH_API_02
  Scenario: TC_AUTH_API_02 – Auth API returns error when Admin password is missing
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "admin", "password": ""}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_03
  # ------------------------------------------------------------------
  @TC_AUTH_API_03
  Scenario: TC_AUTH_API_03 – Auth API returns 401 for valid Admin username with wrong password
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "admin", "password": "wrongpassword"}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_04
  # ------------------------------------------------------------------
  @TC_AUTH_API_04
  Scenario: TC_AUTH_API_04 – Auth API returns 401 when both fields are empty
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "", "password": ""}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_05
  # ------------------------------------------------------------------
  @TC_AUTH_API_05
  Scenario: TC_AUTH_API_05 – Auth API returns 401 for fully invalid credentials
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "hacker", "password": "hacker123"}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_06
  # ------------------------------------------------------------------
  @TC_AUTH_API_06
  Scenario: TC_AUTH_API_06 – User can authenticate via API with valid credentials
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "testuser", "password": "test123"}
      """
    Then the response status should be 200
    And the response body should contain "token"

  # ------------------------------------------------------------------
  # TC_AUTH_API_07
  # ------------------------------------------------------------------
  @TC_AUTH_API_07
  Scenario: TC_AUTH_API_07 – Auth API returns 401 for valid User username with wrong password
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "user", "password": "badpassword"}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_08
  # ------------------------------------------------------------------
  @TC_AUTH_API_08
  Scenario: TC_AUTH_API_08 – Auth API returns 401 when username is missing
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "", "password": "somepassword"}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_09
  # ------------------------------------------------------------------
  @TC_AUTH_API_09
  Scenario: TC_AUTH_API_09 – Auth API returns 401 when password field is empty
    When I send a POST request to "/api/auth/login" with body:
      """
      {"username": "user", "password": ""}
      """
    Then the response status should be 401

  # ------------------------------------------------------------------
  # TC_AUTH_API_10
  # ------------------------------------------------------------------
  @TC_AUTH_API_10
  Scenario: TC_AUTH_API_10 – Auth API returns 401 when request body is empty
    When I send a POST request to "/api/auth/login" with body:
      """
      {}
      """
    Then the response status should be 401

  

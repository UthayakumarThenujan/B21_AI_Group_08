# ====================================================================
# Feature: Authentication Module – UI Tests
# Tester: Asarak A.
# Module: Authentication
# ====================================================================
@UI @AuthUI @Asarak
Feature: Authentication UI Tests

  Background:
    Given the application is running

  # ------------------------------------------------------------------
  # TC_AUTH_UI_01
  # ------------------------------------------------------------------
  @TC_AUTH_UI_01
  Scenario: TC_AUTH_UI_01 – Admin can login with valid credentials
    Given I am on the login page
    When I enter username "admin" and password "admin123"
    And I click the Login button
    Then I should be redirected to the Admin dashboard
    And the dashboard modules and navigation options should be visible

  # ------------------------------------------------------------------
  # TC_AUTH_UI_02
  # ------------------------------------------------------------------
  @TC_AUTH_UI_02
  Scenario: TC_AUTH_UI_02 – Password required validation when password is empty (Admin)
    Given I am on the login page
    When I enter username "admin" and leave password empty
    And I click the Login button
    Then a validation message for password should be displayed
    And I should remain on the login page

  # ------------------------------------------------------------------
  # TC_AUTH_UI_03
  # ------------------------------------------------------------------
  @TC_AUTH_UI_03
  Scenario: TC_AUTH_UI_03 – Username required validation when username is empty
    Given I am on the login page
    When I leave username empty and enter password "admin123"
    And I click the Login button
    Then a validation message for username should be displayed
    And I should remain on the login page

  # ------------------------------------------------------------------
  # TC_AUTH_UI_04
  # ------------------------------------------------------------------
  @TC_AUTH_UI_04
  Scenario: TC_AUTH_UI_04 – Login fails with invalid username and password
    Given I am on the login page
    When I enter username "invalidUser" and password "invalidPass"
    And I click the Login button
    Then an error message "Invalid username or password" should be displayed
    And I should remain on the login page

  # ------------------------------------------------------------------
  # TC_AUTH_UI_05
  # ------------------------------------------------------------------
  @TC_AUTH_UI_05
  Scenario: TC_AUTH_UI_05 – Login fails with valid admin username and invalid password
    Given I am on the login page
    When I enter username "admin" and password "wrongPassword"
    And I click the Login button
    Then an error message "Invalid username or password" should be displayed
    And I should remain on the login page

  # ------------------------------------------------------------------
  # TC_AUTH_UI_06
  # ------------------------------------------------------------------
  @TC_AUTH_UI_06
  Scenario: TC_AUTH_UI_06 – User can login with valid credentials
    Given I am on the login page
    When I enter username "testuser" and password "test123"
    And I click the Login button
    Then I should be redirected to the User dashboard
    And the dashboard modules and navigation options should be visible

  # ------------------------------------------------------------------
  # TC_AUTH_UI_07
  # ------------------------------------------------------------------
  @TC_AUTH_UI_07
  Scenario: TC_AUTH_UI_07 – Validation messages shown when both fields are empty
    Given I am on the login page
    When I leave both username and password fields empty
    And I click the Login button
    Then a validation message for username should be displayed
    And a validation message for password should be displayed
    And I should remain on the login page

  # ------------------------------------------------------------------
  # TC_AUTH_UI_08
  # ------------------------------------------------------------------
  @TC_AUTH_UI_08
  Scenario: TC_AUTH_UI_08 – Password required validation when password field is empty (User)
    Given I am on the login page
    When I enter username "user" and leave password empty
    And I click the Login button
    Then a validation message for password should be displayed
    And I should remain on the login page

  # ------------------------------------------------------------------
  # TC_AUTH_UI_09
  # ------------------------------------------------------------------
  @TC_AUTH_UI_09
  Scenario: TC_AUTH_UI_09 – User can logout successfully
    Given I am logged in as User
    When I click the Logout button
    Then I should be redirected to the login page
    And a logout confirmation message should be displayed

  # ------------------------------------------------------------------
  # TC_DASH_UI_10
  # ------------------------------------------------------------------
  @TC_DASH_UI_10
  Scenario: TC_DASH_UI_10 – Active navigation menu highlighted on Dashboard
    Given I am logged in as User
    Then the active navigation menu item should be highlighted correctly on the Dashboard

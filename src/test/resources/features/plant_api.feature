# ====================================================================
# Feature: Plant Management – API Tests
# Tester: Pirapanchan R.
# Module: Plant Management
# ====================================================================
@API @PlantAPI @Pirapanchan
Feature: Plant Management API Tests

  # ------------------------------------------------------------------
  # TC_PLA_API_01
  # ------------------------------------------------------------------
  @TC_PLA_API_01
  Scenario: TC_PLA_API_01 – Admin can retrieve plant list via API
    Given I have a valid Admin JWT token
    When I send a GET request to "/api/plants"
    Then the response status should be 200
    And the response is a JSON array or paginated response

  # ------------------------------------------------------------------
  # TC_PLA_API_02
  # ------------------------------------------------------------------
  @TC_PLA_API_02
  Scenario: TC_PLA_API_02 – Admin can create a new plant via API
    Given I have a valid Admin JWT token
    And a category exists with name "PlantCategory"
    When I send a POST request to "/api/plants/category/{createdCategoryId}" with body:
      """
      {
        "name": "Rose",
        "description": "A beautiful flower",
        "price": 10.50,
        "quantity": 100
      }
      """
    Then the response status should be 201
    And the response body should contain "Rose"
    And I save the created plant ID as "createdPlantId"

  # ------------------------------------------------------------------
  # TC_PLA_API_03
  # ------------------------------------------------------------------
  @TC_PLA_API_03
  Scenario: TC_PLA_API_03 – Validation error when plant name is empty
    Given I have a valid Admin JWT token
    And a category exists with name "PlantValidationCat"
    When I send a POST request to "/api/plants/category/{createdCategoryId}" with body:
      """
      {
        "name": "",
        "description": "Missing name",
        "price": 5.0,
        "quantity": 10
      }
      """
    Then the response status should be 400

  # ------------------------------------------------------------------
  # TC_PLA_API_04
  # ------------------------------------------------------------------
  @TC_PLA_API_04
  Scenario: TC_PLA_API_04 – Admin can update plant details via API
    Given I have a valid Admin JWT token
    And a plant exists in the system
    When I send a PUT request to "/api/plants/{createdPlantId}" with body:
      """
      {
        "name": "Updated Rose",
        "description": "Updated description",
        "price": 15.0,
        "quantity": 80
      }
      """
    Then the response status should be 200
    And the response body should contain "Updated Rose"

  # ------------------------------------------------------------------
  # TC_PLA_API_05
  # ------------------------------------------------------------------
  @TC_PLA_API_05
  Scenario: TC_PLA_API_05 – Admin can delete a plant via API
    Given I have a valid Admin JWT token
    And a deletable plant exists in the system
    When I send a DELETE request to "/api/plants/{deletablePlantId}"
    Then the response status should be 200

  # ------------------------------------------------------------------
  # TC_PLA_API_06
  # ------------------------------------------------------------------
  @TC_PLA_API_06
  Scenario: TC_PLA_API_06 – User can retrieve plant list via API
    Given I have a valid User JWT token
    When I send a GET request to "/api/plants"
    Then the response status should be 200

  # ------------------------------------------------------------------
  # TC_PLA_API_07
  # ------------------------------------------------------------------
  @TC_PLA_API_07
  Scenario: TC_PLA_API_07 – User cannot create a plant via API
    Given I have a valid User JWT token
    And a category exists with name "UserTryCat"
    When I send a POST request to "/api/plants/category/{createdCategoryId}" with body:
      """
      {
        "name": "UserPlant",
        "description": "Not allowed",
        "price": 1.0,
        "quantity": 1
      }
      """
    Then the response status should be 403

  # ------------------------------------------------------------------
  # TC_PLA_API_08
  # ------------------------------------------------------------------
  @TC_PLA_API_08
  Scenario: TC_PLA_API_08 – User cannot update a plant via API
    Given I have a valid User JWT token
    And a plant exists in the system
    When I send a PUT request to "/api/plants/{createdPlantId}" with body:
      """
      {
        "name": "Hacked Plant",
        "description": "Not allowed",
        "price": 0.01,
        "quantity": 1
      }
      """
    Then the response status should be 403

  # ------------------------------------------------------------------
  # TC_PLA_API_09
  # ------------------------------------------------------------------
  @TC_PLA_API_09
  Scenario: TC_PLA_API_09 – User cannot delete a plant via API
    Given I have a valid User JWT token
    And a plant exists in the system
    When I send a DELETE request to "/api/plants/{createdPlantId}"
    Then the response status should be 403

  # ------------------------------------------------------------------
  # TC_PLA_API_10
  # ------------------------------------------------------------------
  @TC_PLA_API_10
  Scenario: TC_PLA_API_10 – Unauthorized access to plant API without JWT token
    When I send a GET request to "/api/plants" without authentication
    Then the response status should be 401

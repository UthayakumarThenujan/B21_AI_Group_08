# ====================================================================
# Feature: Sales Management – API Tests
# Tester: Sharhaan M.F.M.
# Module: Sales Management
# ====================================================================
@API @SalesAPI @Sharhaan
Feature: Sales Management API Tests

  # ------------------------------------------------------------------
  # TC_SAL_API_01
  # ------------------------------------------------------------------
  @TC_SAL_API_01
  Scenario: TC_SAL_API_01 – Admin can create a sales record via API
    Given I have a valid Admin JWT token
    And a plant exists in the system
    When I send a POST request to "/api/sales/plant/{createdPlantId}?quantity=2" with body:
      """
      {}
      """
    Then the response status should be 201
    And I save the created sales ID as "createdSalesId"

  # ------------------------------------------------------------------
  # TC_SAL_API_02
  # ------------------------------------------------------------------
  @TC_SAL_API_02
  Scenario: TC_SAL_API_02 – Validation error when non-existing plant ID is used in sale
    Given I have a valid Admin JWT token
    When I send a POST request to "/api/sales/plant/99999999?quantity=1" with body:
      """
      {}
      """
    Then the response status should be 400

  # ------------------------------------------------------------------
  # TC_SAL_API_03
  # ------------------------------------------------------------------
  @TC_SAL_API_03
  Scenario: TC_SAL_API_03 – Admin can delete a sales record via API
    Given I have a valid Admin JWT token
    And a sales record exists in the system
    When I send a DELETE request to "/api/sales/{createdSalesId}"
    Then the response status should be 200

  # ------------------------------------------------------------------
  # TC_SAL_API_04
  # ------------------------------------------------------------------
  @TC_SAL_API_04
  Scenario: TC_SAL_API_04 – Sales records can be sorted via API using sort parameter
    Given I have a valid Admin JWT token
    When I send a GET request to "/api/sales?sort=quantity,desc"
    Then the response status should be 200
    And the sales records should be sorted by quantity in descending order

  # ------------------------------------------------------------------
  # TC_SAL_API_05
  # ------------------------------------------------------------------
  @TC_SAL_API_05
  Scenario: TC_SAL_API_05 – Validation error when invalid pagination size is used
    Given I have a valid Admin JWT token
    When I send a GET request to "/api/sales?page=0&size=0"
    Then the response status should be 400

  # ------------------------------------------------------------------
  # TC_SAL_API_06
  # ------------------------------------------------------------------
  @TC_SAL_API_06
  Scenario: TC_SAL_API_06 – User can retrieve sales records via API
    Given I have a valid User JWT token
    When I send a GET request to "/api/sales"
    Then the response status should be 200

  # ------------------------------------------------------------------
  # TC_SAL_API_09
  # ------------------------------------------------------------------
  @TC_SAL_API_09
  Scenario: TC_SAL_API_09 – User cannot update a sales record via API
    Given I have a valid User JWT token
    And a sales record exists in the system
    When I send a PUT request to "/api/sales/{createdSalesId}" with body:
      """
      {"quantity": 99}
      """
    Then the response status should be 403

  # ------------------------------------------------------------------
  # TC_SAL_API_10
  # ------------------------------------------------------------------
  @TC_SAL_API_10
  Scenario: TC_SAL_API_10 – Unauthorized access to sales API without JWT token
    When I send a GET request to "/api/sales" without authentication
    Then the response status should be 401

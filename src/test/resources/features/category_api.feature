# ====================================================================
# Feature: Category Management – API Tests
# Tester: Thenujan U.
# Module: Category Management
# SRS Rule: Category name must be between 3 and 10 characters.
# ====================================================================
@API @CategoryAPI @Thenujan
Feature: Category Management API Tests

  # ------------------------------------------------------------------
  # TC_CAT_API_01 – Admin can create a category via API
  # Name: "NewCat003" (9 chars – valid per SRS 3–10 rule)
  # ------------------------------------------------------------------
  @TC_CAT_API_01
  Scenario: TC_CAT_API_01 – Admin can create a category via API
    Given I have a valid Admin JWT token

    When I send a POST request to "/api/categories" with body:
      """
      {"name": "Cat{timestamp}"}
      """

    Then the response status should be 201
    And I save the created category ID as "createdCategoryId"

  # ------------------------------------------------------------------
  # TC_CAT_API_02 – Category name length validation (3–10 chars rule)
  # "ab" = 2 chars (too short), "Verylongcat" = 11 chars (too long)
  # ------------------------------------------------------------------
  @TC_CAT_API_02
  Scenario Outline: TC_CAT_API_02 – Category name length validation returns 400
    Given I have a valid Admin JWT token
    When I send a POST request to "/api/categories" with body:
      """
      {"name": "<name>"}
      """
    Then the response status should be 400

    Examples:
      | name        |
      | ab          |
      | Verylongcat |

  # ------------------------------------------------------------------
  # TC_CAT_API_03 – Admin can update a category via API
  # ------------------------------------------------------------------
  @TC_CAT_API_03
  Scenario: TC_CAT_API_03 – Admin can update a category via API
    Given I have a valid Admin JWT token
    And a category exists with name "UpdateMe"
    When I send a PUT request to "/api/categories/{createdCategoryId}" with body:
      """
      {"name": "Updated"}
      """
    Then the response status should be 200
    And the response body should contain "Updated"

  # ------------------------------------------------------------------
  # TC_CAT_API_04 – Category name is required
  # SRS: Category Name → Required
  # ------------------------------------------------------------------
  @TC_CAT_API_04
  Scenario: TC_CAT_API_04 – Create category without name returns 400

    Given I have a valid Admin JWT token

    When I send a POST request to "/api/categories" with body:
      """
      {
        "name": ""
      }
      """

    Then the response status should be 400

  # ------------------------------------------------------------------
  # TC_CAT_API_05 – Paginated category API returns correct structure
  # ------------------------------------------------------------------
  @TC_CAT_API_05
  Scenario: TC_CAT_API_05 – Paginated category API returns correct structure
    Given I have a valid Admin JWT token
    When I send a GET request to "/api/categories/page?page=0&size=5&sort=id,asc"
    Then the response status should be 200
    And the response body should contain "content"

  # ------------------------------------------------------------------
  # TC_CAT_API_06 – GET all categories returns list (any authenticated user)
  # ------------------------------------------------------------------
  @TC_CAT_API_06
  Scenario: TC_CAT_API_06 – GET all categories returns list
    Given I have a valid User JWT token
    When I send a GET request to "/api/categories"
    Then the response status should be 200
    And the response is a JSON array

  # ------------------------------------------------------------------
  # TC_CAT_API_07 – Normal user cannot update category via API
  # Manual check: user is blocked (403 expected)
  # App log shows API returned 200 for testuser → this is a real app bug
  # Test FAILS to confirm bug: API does not enforce RBAC for PUT /api/categories
  # ------------------------------------------------------------------
  @TC_CAT_API_07
  Scenario: TC_CAT_API_07 – Normal user cannot update category via API
    Given I have a valid User JWT token
    And a category exists with name "UserTryUpd"
    When I send a PUT request to "/api/categories/{createdCategoryId}" with body:
      """
      {"name": "Hacked"}
      """
    Then the response status should be 403

  # ------------------------------------------------------------------
  # TC_CAT_API_08 – Normal user cannot delete category via API
  # Manual check: user is blocked (403 expected)
  # App log shows API returned 204 for testuser → this is a real app bug
  # Test FAILS to confirm bug: API does not enforce RBAC for DELETE /api/categories
  # ------------------------------------------------------------------
  @TC_CAT_API_08
  Scenario: TC_CAT_API_08 – Normal user cannot delete category via API
    Given I have a valid User JWT token
    And a category exists with name "UserTryDel"
    When I send a DELETE request to "/api/categories/{createdCategoryId}"
    Then the response status should be 403

  # ------------------------------------------------------------------
  # TC_CAT_API_09 – GET category by valid ID returns correct data
  # ------------------------------------------------------------------
  @TC_CAT_API_09
  Scenario: TC_CAT_API_09 – GET category by valid ID returns correct data
    Given I have a valid User JWT token
    And a category exists with name "GetById"
    When I send a GET request to "/api/categories/{createdCategoryId}"
    Then the response status should be 200
    And the response body should contain "GetById"

  # ------------------------------------------------------------------
  # TC_CAT_API_10 – GET category by invalid ID returns 404
  # ------------------------------------------------------------------
  @TC_CAT_API_10
  Scenario: TC_CAT_API_10 – GET category by invalid ID returns 404
    Given I have a valid User JWT token
    When I send a GET request to "/api/categories/99999999"
    Then the response status should be 404

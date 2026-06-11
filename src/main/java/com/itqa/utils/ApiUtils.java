package com.itqa.utils;

import com.itqa.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Utility class for RestAssured API calls with Allure integration.
 */
public class ApiUtils {

    static {
        RestAssured.baseURI = ConfigManager.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Returns a base request spec with Allure filter attached.
     */
    public static RequestSpecification given() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON);
    }

    /**
     * Returns a request spec with Bearer token.
     */
    public static RequestSpecification givenWithToken(String token) {
        return given()
                .header("Authorization", "Bearer " + token);
    }

    /**
     * Login and return JWT token.
     */
    public static String getToken(String username, String password) {
        Response response = given()
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .post("/api/auth/login");

        if (response.statusCode() == 200) {
            return response.jsonPath().getString("token");
        }
        throw new RuntimeException("Login failed for user: " + username
                + " | Status: " + response.statusCode()
                + " | Body: " + response.body().asString());
    }

    public static String getAdminToken() {
        return getToken(ConfigManager.getAdminUsername(), ConfigManager.getAdminPassword());
    }

    public static String getUserToken() {
        return getToken(ConfigManager.getUserUsername(), ConfigManager.getUserPassword());
    }
}

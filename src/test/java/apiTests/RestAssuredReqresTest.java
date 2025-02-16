package apiTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class RestAssuredReqresTest {
    private String userId;

    @BeforeClass
    public void setup() {
        String baseUrl = getProperty("base.url");
        if (baseUrl == null) {
            throw new RuntimeException("Base URL is null. Check if config.properties is correctly placed in src/test/resources/");
        }
        RestAssured.baseURI = baseUrl;
        System.out.println("Base URL loaded: " + baseUrl);
    }

    private String getProperty(String key) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("config.properties file not found in resources folder");
                return null;
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
        return properties.getProperty(key);
    }

    @Test(priority = 1)
    public void createUser() {
        String requestBody = "{\"name\": \"John Doe\", \"job\": \"Software Engineer\", \"age\": 24}";
        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(201)
                    .extract().response();

            userId = response.jsonPath().getString("id");
            Assert.assertNotNull(userId, "User ID should not be null");
            System.out.println("User created with ID: " + userId);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            Assert.fail("User creation failed");
        }
    }

    @Test(priority = 2, dependsOnMethods = {"createUser"})
    public void retrieveUser() {
        String existingUserId = "2"; // Using a valid user ID from Reqres API
        try {
            Response response = given()
                    .when()
                    .get("/users/" + existingUserId)
                    .then()
                    .statusCode(200)
                    .extract().response();

            Assert.assertNotNull(response.jsonPath().getString("data"), "User data should not be null");
            System.out.println("User retrieved: " + response.asString());
        } catch (Exception e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            Assert.fail("User retrieval failed");
        }
    }

    @Test(priority = 3, dependsOnMethods = {"retrieveUser"})
    public void updateUser() {
        String updatedRequestBody = "{\"name\": \"John Updated\", \"job\": \"Senior Engineer\"}";
        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(updatedRequestBody)
                    .when()
                    .put("/users/" + userId)
                    .then()
                    .statusCode(200)
                    .extract().response();

            Assert.assertEquals(response.jsonPath().getString("job"), "Senior Engineer", "Job title should be updated");
            System.out.println("User updated: " + response.asString());
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            Assert.fail("User update failed");
        }
    }

    @Test(priority = 4, dependsOnMethods = {"updateUser"})
    public void deleteUser() {
        try {
            Response response = given()
                    .when()
                    .delete("/users/" + userId)
                    .then()
                    .statusCode(204)
                    .extract().response();

            Assert.assertEquals(response.statusCode(), 204, "User should be successfully deleted");
            System.out.println("User deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            Assert.fail("User deletion failed");
        }
    }
}

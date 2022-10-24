import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class LoginUser {
    private String login;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        String json = getRandomUserJson();
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then().assertThat()
                .and()
                .statusCode(200);
    }

    private String getRandomUserJson() {
        login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        String json = String.format("{\"email\": \"%s\"," +
                "  \"password\": \"password\", \"name\": \"Username\"}", login);
        return json;
    }

    @Test
    @DisplayName("User can log in")
    public void userCanLogIn() throws IOException {
        String json = String.format("{\"email\": \"%s\",\"password\":\"password\"}", login);
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then().assertThat().body("accessToken", notNullValue())
                .and().body("refreshToken", notNullValue())
                .statusCode(200);
    }

    @Test
    @DisplayName("User can not log in with incorrect password")
    public void userCannotLogInWithIncorrectPassword() throws IOException {
        String json = String.format("{\"email\": \"%s\",\"password\":\"password123\"}", login);
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then().assertThat().body("accessToken", nullValue())
                .and().body("refreshToken", nullValue())
                .statusCode(401);
    }

    @After
    public void cleanUser() {
        if (login == null) {
            return;
        }
        String json = String.format("{\"email\": \"%s\",\"password\":\"password\"}", login);
        var response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/login");
        response.then().assertThat().statusCode(200);
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        String accessTokenToDelete = jsonObject.get("accessToken").toString();

        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessTokenToDelete)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
    }
}





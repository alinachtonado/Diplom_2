import org.junit.Before;
import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;

import static io.restassured.RestAssured.given;

public class CreateUserTest {
    private String login;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Can create unique user")
    public void canCreateUniqueUser() {
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

    @Test
    @DisplayName ("Can not duplicate user")
    public void cannotCreateDuplicateTest() {
        String json = getRandomUserJson();
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then().statusCode(200);

        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then().statusCode(403);
    }

    @Test
    @DisplayName ("Can not create user without password")
    public void cannotCreateUserWithoutPasswordTest() {
        File json = new File("src/test/resources/newUserWithoutPassword.json");
        given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then().statusCode(403);
    }

    private String getRandomUserJson(){
        login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        String json = String.format("{\"email\": \"%s\"," +
                "  \"password\": \"password\", \"name\": \"Username\"}", login);
        return json;
    }
}

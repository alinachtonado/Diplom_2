import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class EditUserData {
    private String login;
    private String accessToken;

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
        userLogIn();
    }

    private String getRandomUserJson() {
        login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        String json = String.format("{\"email\": \"%s\"," +
                "  \"password\": \"password\", \"name\": \"Username\"}", login);
        return json;
    }

    private void userLogIn() {
        String json = String.format("{\"email\": \"%s\",\"password\":\"password\"}", login);
        var response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/auth/login");
        response.then().assertThat().statusCode(200);
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        accessToken = jsonObject.get("accessToken").toString();
    }

    @Test
    @DisplayName("Edit user name data")
    public void editUserNameData(){
        String json = String.format("{\"email\":\"%s\",\"name\":\"Username2\"}", login);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user ")
                .then().assertThat()
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Edit user email data")
    public void editUserEmailData(){
        login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        String json = String.format("{\"email\":\"%s\",\"name\":\"Username\"}", login);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user ")
                .then().assertThat()
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Edit unauthorized user data")
    public void editUserDataNotAuth(){
        String json = String.format("{\"email\":\"%s\",\"name\":\"Username2\"}", login);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user ")
                .then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
}

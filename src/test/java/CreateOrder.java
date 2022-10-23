import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;

public class CreateOrder {
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
    @DisplayName("Auth user create order")
    public void userCreateOrder() {
        String json = String.format("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}");
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Unauth user create order")
    public void unauthUserCreateOrder() {
        String json = String.format("{\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\",\"61c0c5a71d1f82001bdaaa6f\"]}");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("User create order without ingredients")
    public void userCreateOrderWithoutIngredients() {
        String json = String.format("{\"ingredients\": []}");
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("User create order with invalid ingredients hash")
    public void userCreateOrderWithInvalidHash() {
        String json = String.format("{\"ingredients\": [\"61c0c5a71d1f82001bdaa+++\",\"61c0c5a71d1f82001bdaa___\"]}");
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .and()
                .statusCode(500);
    }
}
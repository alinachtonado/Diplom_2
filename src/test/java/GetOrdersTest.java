import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class GetOrdersTest {
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
    @DisplayName("Get order list")
    public void getOrderList() {
        String json = String.format("{\"email\":\"%s\",\"name\":\"Username2\"}", login);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .get("/api/orders")
                .then().assertThat()
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Get order list unauthorized user")
    public void getOrderListUnauth() {
        String json = String.format("{\"email\":\"%s\",\"name\":\"Username2\"}", login);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .get("/api/orders")
                .then().assertThat()
                .and()
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


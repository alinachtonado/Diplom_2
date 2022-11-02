import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CreateOrderTest {
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = User.createRandomUser();
        UserOperations.createUser(user)
                .then().assertThat()
                .statusCode(200);
        accessToken = UserOperations.loginUserAndGetAccessToken(user);
    }


    @Test
    @DisplayName("Auth user create order")
    public void userCreateOrder() {
        Map<String, String[]> body = new HashMap<String, String[]>();
        body.put("ingredients", new String[] { "61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"});
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(body)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Unauthorized user create order")
    public void unauthUserCreateOrder() {
        Map<String, String[]> body = new HashMap<String, String[]>();
        body.put("ingredients", new String[] { "61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"});
        given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("User create order without ingredients")
    public void userCreateOrderWithoutIngredients() {
        Map<String, String[]> body = new HashMap<String, String[]>();
        body.put("ingredients", new String[0]);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(body)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    @DisplayName("User create order with invalid ingredients hash")
    public void userCreateOrderWithInvalidHash() {
        Map<String, String[]> body = new HashMap<String, String[]>();
        body.put("ingredients", new String[] { "61c0c5a71d1f82001bdaa+++", "61c0c5a71d1f82001bdaa___"});
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(body)
                .when()
                .post("/api/orders")
                .then().assertThat()
                .statusCode(500);
    }

    @After
    public void cleanUser() {
        UserOperations.deleteUserByToken(accessToken);
    }
}
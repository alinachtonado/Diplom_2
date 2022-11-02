import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class GetOrdersTest {
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
    @DisplayName("Get order list")
    public void getOrderList() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get("/api/orders")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Get order list unauthorized user")
    public void getOrderListUnauth() {
        given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/orders")
                .then().assertThat()
                .statusCode(401);
    }

    @After
    public void cleanUser() {
        UserOperations.deleteUserByToken(accessToken);
    }
}


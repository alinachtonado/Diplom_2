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
        String[] ingridients = new String[] { "61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        UserOperations.createOrder(ingridients, accessToken)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Unauthorized user create order")
    public void unauthUserCreateOrder() {
        String[] ingridients = new String[] { "61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        UserOperations.createOrder(ingridients)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("User create order without ingredients")
    public void userCreateOrderWithoutIngredients() {
        String[] ingridients = new String[0];
        UserOperations.createOrder(ingridients, accessToken)
                .then().assertThat()
                .statusCode(400);
    }

    @Test
    @DisplayName("User create order with invalid ingredients hash")
    public void userCreateOrderWithInvalidHash() {
        String[] ingridients = new String[] { "61c0c5a71d1f82001bdaa+++", "61c0c5a71d1f82001bdaa___"};
        UserOperations.createOrder(ingridients)
                .then().assertThat()
                .statusCode(500);
    }

    @After
    public void cleanUser() {
        UserOperations.deleteUserByToken(accessToken);
    }
}
import io.qameta.allure.junit4.DisplayName;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import io.restassured.RestAssured;
import org.junit.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class LoginUserTest {
    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        user = User.createRandomUser();
        UserOperations.createUser(user)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("User can log in")
    public void userCanLogIn() throws IOException {
        UserOperations.loginUser(user)
                .then().assertThat()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .statusCode(200);
    }

    @Test
    @DisplayName("User can not log in with incorrect password")
    public void userCannotLogInWithIncorrectPassword() throws IOException {
        UserOperations.loginUser(new User(user.getEmail(), "password123", "Name"))
                .then().assertThat()
                .body("accessToken", nullValue())
                .and()
                .body("refreshToken", nullValue())
                .statusCode(401);
    }

    @After
    public void cleanUser() {
        UserOperations.deleteUser(user);
        user = null;
    }
}

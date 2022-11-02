import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class EditUserDataTest {
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
    @DisplayName("Edit user name data")
    public void editUserNameData(){
        Map<String, String> body = new HashMap<String, String>();
        body.put("name", "Username2");
        UserOperations.editUser(body, accessToken)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Edit user email data")
    public void editUserEmailData(){
        Map<String, String> body = new HashMap<String, String>();
        String newEmail = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        body.put("email", newEmail);
        UserOperations.editUser(body, accessToken)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Edit unauthorized user data")
    public void editUserDataNotAuth(){
        Map<String, String> body = new HashMap<String, String>();
        body.put("name", "Username2");
        given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .patch("/api/auth/user")
                .then().assertThat().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }

    @After
    public void cleanUser() {
        UserOperations.deleteUserByToken(accessToken);
    }
}

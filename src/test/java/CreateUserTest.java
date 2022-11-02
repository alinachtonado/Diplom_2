import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import io.restassured.RestAssured;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class CreateUserTest {
    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Can create unique user")
    public void canCreateUniqueUser() {
        user = User.createRandomUser();
        UserOperations.createUser(user)
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Can not duplicate user")
    public void cannotCreateDuplicateTest() {
        user = User.createRandomUser();
        UserOperations.createUser(user)
                .then().assertThat()
                .statusCode(200);
        UserOperations.createUser(user)
                .then().assertThat()
                .statusCode(403);
    }

    @Test
    @DisplayName("Can not create user without password")
    public void cannotCreateUserWithoutPasswordTest() {
        String login = String.format("a%s@gmail.com", java.util.UUID.randomUUID());
        user = new User(login, "", "Username");
        UserOperations.createUser(user)
                .then().assertThat().statusCode(403);
        user = null;
    }

    @After
    public void cleanUser() {
        UserOperations.deleteUser(user);
        user = null;
    }
}

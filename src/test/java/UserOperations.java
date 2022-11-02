import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserOperations{
    public static Response createUser(User user){
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    public static Response loginUser(User user){
        Map<String, String> body = new HashMap<String, String>();
        body.put("email", user.getEmail());
        body.put("password", user.getPassword());
        return given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/api/auth/login");
    }

    public static String loginUserAndGetAccessToken(User user){
        var response = loginUser(user);
        response.then().assertThat().statusCode(200);
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        return jsonObject.get("accessToken").toString();
    }

    public static void deleteUser(User user){
        if (user == null){
            return;
        }

        String accessTokenToDelete = loginUserAndGetAccessToken(user);
        deleteUserByToken(accessTokenToDelete);
    }

    public static Response editUser(Map<String, String> body, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(body)
                .when()
                .patch("/api/auth/user");
    }

    public static void deleteUserByToken(String accessToken) {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
    }
}
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderOperations{
    public static Response createOrder(String[] ingridients, String accessToken){
        Map<String, String[]> body = new HashMap<String, String[]>();
        body.put("ingredients", ingridients);
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(body)
                .when()
                .post("/api/orders");
    }

    public static Response createOrder(String[] ingridients){
        Map<String, String[]> body = new HashMap<String, String[]>();
        body.put("ingredients", ingridients);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/orders");
    }

    public static Response getOrders(String accessToken){
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get("/api/orders");
    }

    public static Response getOrders(){
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/orders");
    }
}
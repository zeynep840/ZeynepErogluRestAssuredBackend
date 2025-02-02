package api.utils;

import io.restassured.RestAssured;

public class ApiBase {
    public static final String BASE_URL = "https://petstore.swagger.io/v2";

    static {
        RestAssured.baseURI = BASE_URL; // API Base URL doğrudan burada tanımlandı
    }

    public static long generateUniqueId() {
        return System.currentTimeMillis() / 1000;
    }
}

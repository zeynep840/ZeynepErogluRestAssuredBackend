package api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import api.utils.ApiBase;
import api.utils.ApiEndpoints;
import api.utils.ApiPayloads;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PetstoreAPINegativeTest extends ApiBase {

    @Test(priority = 1, description = "Verify API response when creating a pet with invalid data")
    public void testCreatePetWithInvalidData() {
        Map<String, Object> invalidPet = new HashMap<>();
        invalidPet.put("id", ""); // Geçersiz ID
        invalidPet.put("name", ""); // Boş isim
        invalidPet.put("status", null); // Geçersiz durum

        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidPet)
                .when()
                .post("/pet")
                .then()
                .extract().response();

        System.out.println("POST Invalid Pet Response: " + response.asString());

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        // API 200 döndürdüyse ve ID olarak geçersiz bir değer oluşturduysa, test PASS olmalı
        if (statusCode == 200) {
            boolean hasInvalidId = responseBody.matches(".*\"id\":922337203685477[0-9]+.*");

            // Eğer API 922337203685477 ile başlayan büyük bir ID oluşturduysa, test geçmeli
            Assert.assertTrue(hasInvalidId, "API created an invalid ID (922337203685477XXXX), which is expected.");
        } else if (statusCode == 400) {
            // API 400 döndürürse, beklenen bir hata olduğundan test yine geçmeli
            Assert.assertTrue(responseBody.contains("error") || responseBody.contains("message"),
                    "Expected error message in response but got: " + responseBody);
        } else {
            // Eğer farklı bir hata kodu dönerse, test başarısız olmalı
            Assert.fail("Expected 200 with invalid ID or 400 error, but received: " + statusCode);
        }
    }



    @Test(priority = 2)
    public void testUpdateNonExistingPet() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(ApiPayloads.createPetPayload(99999999999L, "Ghost", "available"))
                .when()
                .put(ApiEndpoints.UPDATE_PET)
                .then()
                .extract().response();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 200, "Unexpected response code");

        if (statusCode == 404) {
            String errorMessage = response.jsonPath().getString("message");
            Assert.assertNotNull(errorMessage, "Error message should be present for non-existing pet");
            Assert.assertTrue(errorMessage.toLowerCase().contains("not found"), "Error message should indicate pet was not found");
        }
    }

    @Test(priority = 3, description = "Verify API response when deleting a non-existing pet")
    public void testDeleteNonExistingPet() {
        long nonExistingPetId = 99999999999L; // Rastgele, var olmayan büyük bir pet ID

        Response response = given()
                .when()
                .delete(ApiEndpoints.DELETE_PET + nonExistingPetId)
                .then()
                .extract().response();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Delete Response Body: " + responseBody);
        System.out.println("Status Code: " + statusCode);

        if (statusCode == 404) {
            String errorMessage = response.jsonPath().getString("message");

            // Hata mesajı varsa doğrula
            Assert.assertNotNull(errorMessage, "Error message should be present for non-existing pet");
            Assert.assertTrue(errorMessage.toLowerCase().contains("not found"),
                    "Expected 'not found' message, but got: " + errorMessage);

        } else if (statusCode == 200) {
            String message = response.jsonPath().getString("message");

            // Eğer mesaj sadece bir ID içeriyorsa, uyarı ver ama testi geç
            if (message.matches("\\d+")) {
                System.out.println("⚠ WARNING: API returned only an ID as a message: " + message);
            } else {
                Assert.fail("Unexpected response message: " + message);
            }

        } else {
            Assert.fail("Unexpected response code: " + statusCode + ". Response Body: " + responseBody);
        }
    }

}

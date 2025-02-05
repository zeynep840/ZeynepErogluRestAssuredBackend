package api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import api.utils.ApiBase;
import api.utils.ApiEndpoints;
import api.utils.ApiPayloads;

import java.math.BigInteger;
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


    @Test(priority = 2, description = "Verify API response when updating a non-existing pet with invalid data")
    public void testUpdateNonExistingPet() {
        Map<String, Object> invalidPet = new HashMap<>();
        invalidPet.put("id", ""); // Geçersiz ID
        invalidPet.put("name", ""); // Boş isim
        invalidPet.put("status", null); // Geçersiz durum

        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidPet)
                .when()
                .put(ApiEndpoints.UPDATE_PET)
                .then()
                .extract().response();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Update Response Body: " + responseBody);
        System.out.println("Status Code: " + statusCode);
        if (statusCode == 200) {
            // API'nin döndürdüğü ID'yi string olarak al ve kontrol et
            String returnedId = response.jsonPath().getString("id");

            // "922" içerdiğini doğrula
            Assert.assertTrue(returnedId.contains("922"),
                    "API did not return an ID containing '922'. Returned ID: " + returnedId);

            System.out.println("Test Passed: API returned an ID containing '922': ");
        }
        else if (statusCode == 400) {
            // API 400 döndürürse, beklenen hata mesajının geldiğini doğrula
            Assert.assertTrue(responseBody.contains("error") || responseBody.contains("message"),
                    "Expected error message in response but got: " + responseBody);
            System.out.println(" Test Passed: API returned 400 with an error message.");
        }
        else {
            // Eğer farklı bir hata kodu dönerse, test başarısız olmalı
            Assert.fail(" Unexpected response code: " + statusCode + ". Response Body: " + responseBody);
        }
    }



    @Test(priority = 3, description = "Verify API response when deleting a non-existing pet with a very large ID")
    public void testDeleteNonExistingPet() {
        // Aşırı büyük bir pet ID (BigInteger olarak tutulup String'e çevriliyor)
        String bigNumber = "99999999999999999999999999999999999999999999999999999";

        try {
            Response response = given()
                    .when()
                    .delete(ApiEndpoints.DELETE_PET + bigNumber) // ID'yi String olarak ekledik
                    .then()
                    .extract().response();

            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString();

            System.out.println("Delete Response Body: " + responseBody);
            System.out.println("Status Code: " + statusCode);

            // Eğer API 404 döndürdüyse, bu beklenen bir durumdur ve test PASS olur
            if (statusCode == 404) {
                String errorMessage = response.jsonPath().getString("message");

                Assert.assertNotNull(errorMessage, "Error message should be present for non-existing pet");
                Assert.assertTrue(errorMessage.toLowerCase().contains("not found"),
                        "Expected 'not found' message, but got: " + errorMessage);
                System.out.println("Test Passed: API returned 404 - Not Found as expected.");
            }
            else {
                Assert.fail("Unexpected response code: " + statusCode + ". Response Body: " + responseBody);
            }
        } catch (Exception e) {
            // Eğer API büyük sayı nedeniyle hata veriyorsa, istisnayı yakala ve 404 olup olmadığını kontrol et
            System.out.println("Exception caught: " + e.getMessage());
            Assert.assertTrue(e.getMessage().contains("404"), "Expected 404 Not Found exception, but got: " + e.getMessage());
        }
    }

    @Test(priority = 4, description = "Verify API response when requesting a non-existing pet")
    public void testGetNonExistingPet() {
        // Rastgele, var olmayan büyük bir pet ID
        String nonExistingPetId = "99999999999999999999999999999999999999999999999999999";

        try {
            Response response = given()
                    .when()
                    .get(ApiEndpoints.GET_PET_BY_ID + nonExistingPetId)
                    .then()
                    .extract().response();

            int statusCode = response.getStatusCode();
            String responseBody = response.getBody().asString();

            System.out.println("Delete Response Body: " + responseBody);
            System.out.println("Status Code: " + statusCode);

            // Eğer API 404 döndürdüyse, bu beklenen bir durumdur ve test PASS olur
            if (statusCode == 404) {
                String errorMessage = response.jsonPath().getString("message");

                Assert.assertNotNull(errorMessage, "Error message should be present for non-existing pet");
                Assert.assertTrue(errorMessage.toLowerCase().contains("not found"),
                        "Expected 'not found' message, but got: " + errorMessage);
                System.out.println("Test Passed: API returned 404 - Not Found as expected.");
            }
            else {
                Assert.fail("Unexpected response code: " + statusCode + ". Response Body: " + responseBody);
            }
        } catch (Exception e) {
            // Eğer API büyük sayı nedeniyle hata veriyorsa, istisnayı yakala ve 404 olup olmadığını kontrol et
            System.out.println("Exception caught: " + e.getMessage());
            Assert.assertTrue(e.getMessage().contains("404"), "Expected 404 Not Found exception, but got: " + e.getMessage());
        }
    }



}

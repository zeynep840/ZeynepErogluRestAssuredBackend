package api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import api.utils.ApiBase;
import api.utils.ApiEndpoints;
import api.utils.ApiPayloads;

import static io.restassured.RestAssured.given;

public class PetstoreAPITest extends ApiBase {
    private long petId;

    @BeforeClass
    public void setup() {
        // ApiBase içinde zaten baseURI tanımlı olduğu için setup() metoduna gerek yok
    }

    @Test(priority = 1)
    public void testCreatePet() {
        petId = generateUniqueId();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(ApiPayloads.createPetPayload(petId, "Buddy", "available"))
                .when()
                .post(ApiEndpoints.CREATE_PET)
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Created Pet ID: " + petId);
        Assert.assertEquals(response.jsonPath().getLong("id"), petId);
    }

    @Test(priority = 2, dependsOnMethods = "testCreatePet")
    public void testGetPetById() {
        Response response = given()
                .when()
                .get(ApiEndpoints.GET_PET_BY_ID + petId)
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.jsonPath().getLong("id"), petId);
    }

    @Test(priority = 3, dependsOnMethods = "testCreatePet")
    public void testUpdatePet() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(ApiPayloads.createPetPayload(petId, "Max", "sold"))
                .when()
                .put(ApiEndpoints.UPDATE_PET)
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.jsonPath().getString("name"), "Max");
    }

    @Test(priority = 4, dependsOnMethods = "testCreatePet")
    public void testDeletePet() {
        Response deleteResponse = given()
                .when()
                .delete(ApiEndpoints.DELETE_PET + petId)
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(deleteResponse.jsonPath().getString("message"), String.valueOf(petId));
    }




}

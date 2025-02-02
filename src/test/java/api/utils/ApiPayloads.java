package api.utils;

import java.util.HashMap;
import java.util.Map;

public class ApiPayloads {
    public static Map<String, Object> createPetPayload(long id, String name, String status) {
        Map<String, Object> pet = new HashMap<>();
        pet.put("id", id);
        pet.put("name", name);
        pet.put("status", status);
        return pet;
    }

    public static Map<String, Object> createInvalidPetPayload() {
        Map<String, Object> invalidPet = new HashMap<>();
        invalidPet.put("id", "");
        invalidPet.put("name", "");
        invalidPet.put("status", null);
        return invalidPet;
    }
}
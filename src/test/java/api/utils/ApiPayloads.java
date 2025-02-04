package api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static Map<String, Object> createPetPayload(long id, String name, String status, String url, int tagNumber, String tagName, int categoryNumber, String categoryName) {
        Map<String, Object> pet = new HashMap<>();
        pet.put("id", id);
        pet.put("name", name);
        pet.put("status", status);

        Map<String, Object> category = new HashMap<>();
        category.put("id", categoryNumber);
        category.put("name", categoryName);
        pet.put("category", category);

        List<String> photoUrls = new ArrayList<>();
        photoUrls.add(url);
        pet.put("photoUrls", photoUrls);

        Map<String, Object> tag = new HashMap<>();
        tag.put("id", tagNumber);
        tag.put("name", tagName);

        List<Map<String, Object>> tags = new ArrayList<>();
        tags.add(tag);
        pet.put("tags", tags);

        return pet;
    }
}
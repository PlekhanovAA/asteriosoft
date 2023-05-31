package com.asteriosoft.utils;

import com.asteriosoft.entities.Banner;
import com.asteriosoft.entities.Category;
import com.asteriosoft.exceptions.CustomException;
import com.asteriosoft.repository.BannerRepository;
import com.asteriosoft.repository.CategoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Component
public class JsonHelper {
    Logger logger = LoggerFactory.getLogger(JsonHelper.class);

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BannerRepository bannerRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public Banner getBannerFromObject(Object objectBanner) {
        Map<String, Object> map = getMapWithObjectProperties(objectBanner);
        requiredBannerFieldsVerify(map);
        Banner banner = new Banner();
        banner.setName(String.valueOf(map.get("name")));
        try {
            banner.setPrice(new BigDecimal(Long.parseLong(String.valueOf(map.get("price")))));
        } catch (NumberFormatException e) {
            logger.error("VALUE price IS NOT CORRECT: {}", e.getMessage());
            throw new CustomException("VALUE price IS NOT CORRECT");
        }
        if (map.get("categories") != null) {
            Set<Category> categoriesSet = categoryRepository.findByNameInAndIsDeletedFalse((ArrayList<String>) map.get("categories"));
            banner.setCategories(categoriesSet);
        }
        banner.setIsDeleted(false);

        return banner;
    }

    public Category getCategoryFromObject(Object objectCategory) {
        Map<String, Object> map = getMapWithObjectProperties(objectCategory);
        requiredCategoryFieldsVerify(map);
        Category category = new Category();
        category.setName(String.valueOf(map.get("name")));
        category.setRequestId(String.valueOf(map.get("requestId")));
        category.setIsDeleted(false);

        return category;
    }

    private Map<String, Object> getMapWithObjectProperties(Object objectBanner) {
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(objectMapper.writeValueAsString(objectBanner), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            logger.error("JSON IS NOT CORRECT: {}", e.getMessage());
            throw new CustomException("JSON IS NOT CORRECT");
        }
        return map;
    }

    private void requiredBannerFieldsVerify(Map<String, Object> map) {
        if (map.get("name") == null || map.get("price") == null) {
            logger.error("REQUIRED FIELDS MISSING");
            throw new CustomException("REQUIRED FIELDS MISSING");
        }
        if (String.valueOf(map.get("name")).length() > 255) {
            logger.error("FIELD VALUES ARE TOO LONG");
            throw new CustomException("FIELD VALUES ARE TOO LONG");
        }
        if (bannerRepository.findByNameIgnoreCase(String.valueOf(map.get("name"))) != null) {
            logger.error("ATTEMPT TO USE AN EXISTING BANNER NAME");
            throw new CustomException("A BANNER WITH THAT NAME ALREADY EXIST");
        }
    }

    private void requiredCategoryFieldsVerify(Map<String, Object> map) {
        if (map.get("name") == null || map.get("requestId") == null) {
            logger.error("REQUIRED FIELDS MISSING");
            throw new CustomException("REQUIRED FIELDS MISSING");
        }
        if (String.valueOf(map.get("name")).length() > 255 ||
                String.valueOf(map.get("requestId")).length() > 64) {
            logger.error("FIELD VALUES ARE TOO LONG");
            throw new CustomException("FIELD VALUES ARE TOO LONG");
        }
        if (categoryRepository.findByNameIgnoreCase(String.valueOf(map.get("name"))) != null) {
            logger.error("ATTEMPT TO USE AN EXISTING CATEGORY NAME");
            throw new CustomException("A CATEGORY WITH THAT NAME ALREADY EXIST");
        }
        if (categoryRepository.findByRequestIdIgnoreCase(String.valueOf(map.get("requestId"))) != null) {
            logger.error("ATTEMPT TO USE AN EXISTING CATEGORY REQUEST ID");
            throw new CustomException("A CATEGORY WITH THAT REQUEST ID ALREADY EXIST");
        }
    }

}

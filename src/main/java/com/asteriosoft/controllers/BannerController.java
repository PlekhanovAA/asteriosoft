package com.asteriosoft.controllers;

import com.asteriosoft.entities.Banner;
import com.asteriosoft.services.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BannerController {
    Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    BannerService bannerService;

    @GetMapping("/bid")
    public ResponseEntity<Object> getBanner(@RequestParam List<String> cat, @RequestHeader HttpHeaders headers) {
        logger.info("get bid");
        return bannerService.getByCategories(cat, headers);
    }

    @GetMapping("/banners")
    public ResponseEntity<List<Banner>> getAllBanners() {
        logger.info("GET all banners");
        return bannerService.getAll();
    }

    @GetMapping("/banners/filter")
    public ResponseEntity<List<Banner>> bannerSearch(@RequestParam String searchText) {
        logger.info("GET search banners for: {}", searchText);
        return bannerService.filterByName(searchText);
    }

    @PostMapping(value = "/banner")
    public ResponseEntity<Object> createBanner(@RequestBody Object bannerObject) {
        logger.info("POST create banner");
        return bannerService.create(bannerObject);
    }

    @PostMapping("/banner/{id}")
    public ResponseEntity<Object> updateBanner(@PathVariable("id") Long id, @RequestBody Object bannerObject) {
        logger.info("POST update  banner: {}", id);
        return bannerService.update(id, bannerObject);
    }

    @GetMapping("/banner/{id}")
    public ResponseEntity<Object> deleteBanner(@PathVariable("id") long id) {
        logger.info("GET delete  banner: {}", id);
        return bannerService.delete(id);
    }

}

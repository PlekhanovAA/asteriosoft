package com.asteriosoft.services;

import com.asteriosoft.entities.Banner;
import com.asteriosoft.entities.Category;
import com.asteriosoft.entities.CategoryBanner;
import com.asteriosoft.repository.BannerRepository;
import com.asteriosoft.repository.CategoryBannerRepository;
import com.asteriosoft.utils.BannerFindHelper;
import com.asteriosoft.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BannerService {

    @Autowired
    BannerRepository bannerRepository;
    @Autowired
    BannerFindHelper bannerFindHelper;
    @Autowired
    CategoryBannerRepository categoryBannerRepository;
    @Autowired
    JsonHelper jsonHelper;

    public ResponseEntity<Object> getByCategories(List<String> catRequestIdList, HttpHeaders headers) {
        return bannerFindHelper.startHelping(catRequestIdList, headers);
    }

    public ResponseEntity<List<Banner>> getAll() {
        return new ResponseEntity<>(bannerRepository.findBannersByIsDeletedFalse(), HttpStatus.OK);
    }

    public ResponseEntity<List<Banner>> filterByName(String searchText) {
        return new ResponseEntity<>(bannerRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(searchText), HttpStatus.OK);
    }

    public ResponseEntity<Object> create(Object bannerObject) {
        Banner newBanner = jsonHelper.getBannerFromObject(bannerObject);
        bannerRepository.save(newBanner);
        return new ResponseEntity<>(newBanner, HttpStatus.OK);
    }

    public ResponseEntity<Object> update(Long id, Object bannerObject) {
        Optional<Banner> isExistBanner = Optional.ofNullable(bannerRepository.findByIdAndIsDeletedFalse(id));
        if (isExistBanner.isPresent()) {
            Banner updatedBanner = jsonHelper.getBannerFromObject(bannerObject);
            updatedBanner.setId(id);
            processingUpdateBanner(updatedBanner);
            return new ResponseEntity<>("BANNER IS UPDATED", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("BANNER NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    private void processingUpdateBanner(Banner banner) {
        List<Long> newCategories = banner.getCategories().stream().map(Category::getId).toList();
        List<CategoryBanner> oldCategoryBanner = categoryBannerRepository.findByBannerId(banner.getId());
        List<Long> forDeleteCategoryBanner = oldCategoryBanner.stream().
                filter(ocb -> !newCategories.contains(ocb.getCategoryId())).
                map(CategoryBanner::getId).
                toList();
        categoryBannerRepository.deleteAllById(forDeleteCategoryBanner);
        bannerRepository.save(banner);
    }

    public ResponseEntity<Object> delete(Long id) {
        Optional<Banner> isExistBanner = Optional.ofNullable(bannerRepository.findByIdAndIsDeletedFalse(id));
        if (isExistBanner.isPresent()) {
            processingDeleteBanner(isExistBanner.get());
            return new ResponseEntity<>("BANNER IS DELETED", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("BANNER NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    private void processingDeleteBanner(Banner banner) {
        List<CategoryBanner> categoryBanners = categoryBannerRepository.findByBannerId(banner.getId());
        categoryBannerRepository.deleteAllById(categoryBanners.stream().map(CategoryBanner::getId).toList());
        banner.setIsDeleted(true);
        bannerRepository.save(banner);
    }

}

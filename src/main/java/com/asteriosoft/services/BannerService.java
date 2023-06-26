package com.asteriosoft.services;

import com.asteriosoft.entities.Banner;
import com.asteriosoft.entities.Category;
import com.asteriosoft.entities.CategoryBanner;
import com.asteriosoft.repository.BannerRepository;
import com.asteriosoft.repository.CategoryBannerRepository;
import com.asteriosoft.repository.CategoryRepository;
import com.asteriosoft.utils.BannerFindHelper;
import com.asteriosoft.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BannerService {

    @Autowired
    CacheManager cacheManager;
    @Autowired
    BannerRepository bannerRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryBannerRepository categoryBannerRepository;
    @Autowired
    BannerFindHelper bannerFindHelper;
    @Autowired
    JsonHelper jsonHelper;

    private final String bannerCacheName = "banners";

    public ResponseEntity<Object> getByCategories(List<String> catRequestIdList, HttpHeaders headers) {
        return bannerFindHelper.startHelping(catRequestIdList, headers);
    }

    public ResponseEntity<List<Banner>> getAll() {
        List<Banner> result = bannerRepository.findBannersByIsDeletedFalse();
        for(Banner banner : result) {
            List<Long> categoryIdList = categoryBannerRepository.findByBannerId(banner.getId()).stream().
                    map(CategoryBanner::getCategoryId)
                    .toList();
            banner.setCategories(categoryRepository.findAllById(categoryIdList));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<List<Banner>> filterByName(String searchText) {
        return new ResponseEntity<>(bannerRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(searchText), HttpStatus.OK);
    }

    public ResponseEntity<Object> create(Object bannerObject) {
        Banner newBanner = jsonHelper.getBannerFromObject(bannerObject);
        newBanner = processingCreateBanner(newBanner);
        return new ResponseEntity<>(newBanner, HttpStatus.OK);
    }

    @Transactional
    private Banner processingCreateBanner(Banner banner) {
        banner = bannerRepository.save(banner);
        List<CategoryBanner> newCategoryBannerList = new ArrayList<>();
        for(Category category : banner.getCategories()) {
            CategoryBanner cb = CategoryBanner.builder().bannerId(banner.getId()).categoryId(category.getId()).build();
            newCategoryBannerList.add(cb);
        }
        categoryBannerRepository.saveAll(newCategoryBannerList);
        return banner;
    }

    public ResponseEntity<Object> update(Long id, Object bannerObject) {
        Optional<Banner> isExistBanner = Optional.ofNullable(bannerRepository.findByIdAndIsDeletedFalse(id));
        if (isExistBanner.isPresent()) {
            Banner updatedBanner = jsonHelper.getBannerFromObject(bannerObject);
            updatedBanner.setId(id);
            prepareUpdateBanner(updatedBanner);
            return new ResponseEntity<>("BANNER IS UPDATED", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("BANNER NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

    private void prepareUpdateBanner(Banner banner) {
        List<Long> newCategoryIdList = banner.getCategories().stream().map(Category::getId).toList();
        List<Long> oldCategoryIdList = categoryBannerRepository.findByBannerId(banner.getId()).stream().
                map(CategoryBanner::getCategoryId).
                toList();
        List<Long> forDeleteCategoryBanner = oldCategoryIdList.stream().
                filter(oc -> !newCategoryIdList.contains(oc)).
                toList();
        List<Long> forCreateCategoryBannerCategoryIdList = newCategoryIdList.stream().
                filter(nc -> !oldCategoryIdList.contains(nc)).
                toList();
        List<CategoryBanner> newCategoryBannerList = new ArrayList<>();
        for(Long categoryId : forCreateCategoryBannerCategoryIdList) {
            CategoryBanner cb = CategoryBanner.builder().bannerId(banner.getId()).categoryId(categoryId).build();
            newCategoryBannerList.add(cb);
        }
        processingUpdateBanner(banner, forDeleteCategoryBanner, newCategoryBannerList);
    }

    @Transactional
    private void processingUpdateBanner(Banner banner,
                                        List<Long> forDeleteCategoryBanner,
                                        List<CategoryBanner> newCategoryBannerList) {
        categoryBannerRepository.deleteAllById(forDeleteCategoryBanner);
        categoryBannerRepository.saveAll(newCategoryBannerList);
        bannerRepository.save(banner);
        final Cache cache = cacheManager.getCache(bannerCacheName);
        if (cache == null) {
            throw new IllegalArgumentException("invalid cache name: " + bannerCacheName);
        }
        cache.put(banner.getId(), banner);
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
        final Cache cache = cacheManager.getCache(bannerCacheName);
        if (cache == null) {
            throw new IllegalArgumentException("invalid cache name: " + bannerCacheName);
        }
        cache.evict(banner.getId());
    }

}

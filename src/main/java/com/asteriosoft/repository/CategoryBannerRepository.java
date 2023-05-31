package com.asteriosoft.repository;

import com.asteriosoft.entities.CategoryBanner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryBannerRepository extends CrudRepository<CategoryBanner, Long> {
    List<CategoryBanner> findByBannerId(Long bannerId);
}

package com.asteriosoft.repository;

import com.asteriosoft.entities.Banner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends CrudRepository<Banner, Long> {
    List<Banner> findBannersByIsDeletedFalse();
    Banner findByIdAndIsDeletedFalse(Long id);
    Banner findByNameIgnoreCase(String name);
    List<Banner> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
}

package com.asteriosoft.cache;

import com.asteriosoft.entities.Banner;
import com.asteriosoft.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig
public class BannerCache {

    @Autowired
    BannerRepository bannerRepository;

    @Cacheable(cacheNames = {"banners"})
    public List<Banner> getBanners(List<Long> bannerIdForShowList) {
        return bannerRepository.findAllById(bannerIdForShowList);
    }

}

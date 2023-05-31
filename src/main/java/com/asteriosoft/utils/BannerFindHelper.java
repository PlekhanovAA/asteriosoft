package com.asteriosoft.utils;

import com.asteriosoft.entities.Banner;
import com.asteriosoft.entities.Category;
import com.asteriosoft.entities.CategoryBanner;
import com.asteriosoft.entities.LogRecord;
import com.asteriosoft.repository.BannerRepository;
import com.asteriosoft.repository.CategoryBannerRepository;
import com.asteriosoft.repository.CategoryRepository;
import com.asteriosoft.repository.LogRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BannerFindHelper {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BannerRepository bannerRepository;
    @Autowired
    CategoryBannerRepository categoryBannerRepository;
    @Autowired
    LogRecordRepository logRecordRepository;

    public ResponseEntity<Object> startHelping(List<String> catRequestIdList, HttpHeaders headers) {
        LogRecord logRecord = new LogRecord();
        logRecord.setRequestTime(new Date());
        logRecord.setCategoryIds(String.join(",", catRequestIdList));

        return headersVerify(catRequestIdList, logRecord, headers);
    }

    private ResponseEntity<Object> headersVerify(List<String> catRequestIds, LogRecord logRecord, HttpHeaders headers) {
        if (headers.get("user-agent") == null || headers.getHost() == null || headers.getHost().getHostString() == null) {
            logRecord.setNoContentReason("INCORRECT HEADERS IN REQUEST");
            return finishHelping("INCORRECT HEADERS IN REQUEST", logRecord, HttpStatus.BAD_REQUEST);
        }
        logRecord.setUserAgent(String.join(",", String.valueOf(headers.get("user-agent"))));
        logRecord.setUserIp(headers.getHost().getHostString());

        return catRequestIdsVerify(catRequestIds, logRecord);
    }

    private ResponseEntity<Object> catRequestIdsVerify(List<String> catRequestIds, LogRecord logRecord) {
        List<Category> categoryList = categoryRepository.findByRequestIdInAndIsDeletedFalse(catRequestIds);
        if (categoryList.size() == 0) {
            logRecord.setNoContentReason("INCORRECT CATS");
            return finishHelping("INCORRECT CATS", logRecord, HttpStatus.BAD_REQUEST);
        }

        return historyVerify(categoryList, logRecord);
    }

    private ResponseEntity<Object> historyVerify(List<Category> categoryList, LogRecord logRecord) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        List<LogRecord> history = logRecordRepository.findByUserIpAndUserAgentAndRequestTimeGreaterThan(
                logRecord.getUserIp(), logRecord.getUserAgent(), new Date(today.getTimeInMillis()));
        List<Long> bannerIdsAlreadyShown = history.stream().map(LogRecord::getBannerId).toList();
        List<CategoryBanner> categoryBannerList = categoryBannerRepository.findByCategoryIdIn(categoryList.stream().map(Category::getId).toList());
        Set<Long> allBannerIdFromCategoriesList = categoryBannerList.stream().map(CategoryBanner::getBannerId).collect(Collectors.toSet());
        List<Long> bannerIdForShowList = allBannerIdFromCategoriesList.stream().filter(b -> !bannerIdsAlreadyShown.contains(b)).toList();
        return findBanner(bannerIdForShowList, logRecord);
    }

    private ResponseEntity<Object> findBanner(List<Long> bannerIdForShowList, LogRecord logRecord) {
        if (bannerIdForShowList.size() == 0) {
            logRecord.setNoContentReason("NO BANNERS");
            return finishHelping(null, logRecord, HttpStatus.NO_CONTENT);
        }
        Optional<Banner> mostExpensiveBanner = bannerRepository.findAllById(bannerIdForShowList).stream().sorted().findFirst();
        return smallPreparing(mostExpensiveBanner.get(), logRecord);
    }

    private ResponseEntity<Object> smallPreparing(Banner banner, LogRecord logRecord) {
        logRecord.setBannerId(banner.getId());
        logRecord.setBannerPrice(banner.getPrice());

        List<Long> categoryIdList = categoryBannerRepository.findByBannerId(banner.getId()).stream().
                map(CategoryBanner::getCategoryId)
                .toList();
        banner.setCategories(categoryRepository.findAllById(categoryIdList));

        return finishHelping(banner, logRecord, HttpStatus.OK);
    }

    private ResponseEntity<Object> finishHelping(Object body, LogRecord logRecord, HttpStatus status) {
        logRecordRepository.save(logRecord);
        return new ResponseEntity<>(body, status);
    }

}

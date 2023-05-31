package com.asteriosoft.repository;

import com.asteriosoft.entities.LogRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LogRecordRepository extends CrudRepository<LogRecord, Long> {
    List<LogRecord> findByUserIpAndUserAgentAndRequestTimeGreaterThan(String userIp, String userAgent, Date timestamp);
}

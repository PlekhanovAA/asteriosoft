package com.asteriosoft.repository;

import com.asteriosoft.security.entities.AuthorityLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityLogRepository extends CrudRepository<AuthorityLog, Long> {
    AuthorityLog findTopByUsernameAndExpiredFalseOrderByCreatedAtDesc(String userName);
}

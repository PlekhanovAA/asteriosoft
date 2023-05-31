package com.asteriosoft.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "log_record")
@ToString
@Getter
@Setter
public class LogRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_ip")
    private String userIp;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "banner_id")
    private Long bannerId;
    @Column(name = "category_ids")
    private String categoryIds;
    @Column(name = "banner_price")
    private BigDecimal bannerPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "request_time")
    private Date requestTime;
    @Column(name = "no_content_reason")
    private String noContentReason;
}

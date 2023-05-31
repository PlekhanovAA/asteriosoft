package com.asteriosoft.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category_banner")
@Getter
@Setter
public class CategoryBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "banner_id")
    private Long bannerId;
}

package com.asteriosoft.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "category_banner")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBanner implements Serializable {
    @Serial
    private static final long serialVersionUID = -4432831257381375197L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "banner_id")
    private Long bannerId;
}

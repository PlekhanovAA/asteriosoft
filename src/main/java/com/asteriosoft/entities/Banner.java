package com.asteriosoft.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "banner")
@Getter
@Setter
public class Banner implements Comparable<Banner>, Serializable {
    @Serial
    private static final long serialVersionUID = 6706327863003241785L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @ManyToMany()
    @JoinTable(
            name = "category_banner",
            joinColumns = {@JoinColumn(name = "banner_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    @JsonManagedReference
    Set<Category> categories = new HashSet<>();

    @Override
    public int compareTo(Banner otherBanner) {
        return otherBanner.getPrice().compareTo(getPrice());
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Banner)) {
            return false;
        }
        Banner banner = (Banner) o;
        boolean idEquals = (this.id == null && banner.id == null)
                || (this.id != null && this.id.equals(banner.id));
        boolean nameEquals = (this.name == null && banner.name == null)
                || (this.name != null && this.name.equals(banner.name));
        return idEquals && nameEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Banner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", isDeleted=" + isDeleted +
                '}';
    }
}

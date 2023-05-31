package com.asteriosoft.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = -7222588415508558408L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "request_id")
    private String requestId;
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Category)) {
            return false;
        }
        Category category = (Category) o;
        boolean idEquals = (this.id == null && category.id == null)
                || (this.id != null && this.id.equals(category.id));
        boolean nameEquals = (this.name == null && category.name == null)
                || (this.name != null && this.name.equals(category.name));
        boolean requestIdEquals = (this.requestId == null && category.requestId == null)
                || (this.requestId != null && this.requestId.equals(category.requestId));
        return idEquals && nameEquals && requestIdEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, requestId);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", requestId='" + requestId + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}

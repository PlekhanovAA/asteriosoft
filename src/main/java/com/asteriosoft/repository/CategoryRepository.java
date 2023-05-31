package com.asteriosoft.repository;

import com.asteriosoft.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoriesByIsDeletedFalse();
    Category findByIdAndIsDeletedFalse(Long id);
    List<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
    Category findByNameIgnoreCase(String name);
    Category findByRequestIdIgnoreCase(String requestId);
    List<Category> findByNameInAndIsDeletedFalse(List<String> names);
    List<Category> findByRequestIdInAndIsDeletedFalse(List<String> requestIdList);
}

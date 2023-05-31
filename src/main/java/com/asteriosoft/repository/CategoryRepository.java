package com.asteriosoft.repository;

import com.asteriosoft.entities.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findCategoriesByIsDeletedFalse();
    Category findByIdAndIsDeletedFalse(Long id);
    List<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
    Category findByNameIgnoreCase(String name);
    Category findByRequestIdIgnoreCase(String requestId);
    Set<Category> findByNameInAndIsDeletedFalse(List<String> names);
    List<Category> findByRequestIdInAndIsDeletedFalse(List<String> requestIdList);
}

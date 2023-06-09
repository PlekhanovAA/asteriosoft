package com.asteriosoft.services;

import com.asteriosoft.entities.Category;
import com.asteriosoft.exceptions.CustomException;
import com.asteriosoft.repository.CategoryRepository;
import com.asteriosoft.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    JsonHelper jsonHelper;

    public ResponseEntity<List<Category>> getAll() {
        return new ResponseEntity<>(categoryRepository.findCategoriesByIsDeletedFalse(), HttpStatus.OK);
    }

    public ResponseEntity<List<Category>> filterByName(String searchText) {
        return new ResponseEntity<>(categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(searchText), HttpStatus.OK);
    }

    public ResponseEntity<Object> create(Object categoryObject) {
        Category newCategory = jsonHelper.getCategoryFromObject(categoryObject);
        categoryRepository.save(newCategory);
        return new ResponseEntity<>(newCategory, HttpStatus.OK);
    }

    public ResponseEntity<Object> update(Long id, Object categoryObject) {
        Optional<Category> isExistCategory = Optional.ofNullable(categoryRepository.findByIdAndIsDeletedFalse(id));
        if (isExistCategory.isPresent()) {
            Category updatedCategory = jsonHelper.getCategoryFromObject(categoryObject);
            updatedCategory.setId(id);
            categoryRepository.save(updatedCategory);
            return new ResponseEntity<>("CATEGORY IS UPDATED", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("CATEGORY NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Object> delete(Long id) {
        Optional<Category> isExistCategory = Optional.ofNullable(categoryRepository.findByIdAndIsDeletedFalse(id));
        if (isExistCategory.isPresent()) {
            Category category = isExistCategory.get();
            isExistNotDeletedBannersVerify(category);
            category.setIsDeleted(true);
            categoryRepository.save(category);
            return new ResponseEntity<>("CATEGORY IS DELETED", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("CATEGORY NOT EXIST", HttpStatus.BAD_REQUEST);
        }
    }

    private void isExistNotDeletedBannersVerify(Category category) {
        if (category.getBanners().stream().anyMatch(banner -> !banner.getIsDeleted())) {
            throw new CustomException("IMPOSSIBLE TO DELETE A CATEGORY. THERE ARE RELATED BANNERS");
        }
    }

}

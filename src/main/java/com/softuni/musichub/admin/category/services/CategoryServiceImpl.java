package com.softuni.musichub.admin.category.services;

import com.softuni.musichub.admin.category.entities.Category;
import com.softuni.musichub.admin.category.models.bindingModels.AddCategory;
import com.softuni.musichub.admin.category.models.bindingModels.EditCategory;
import com.softuni.musichub.admin.category.models.views.CategoryView;
import com.softuni.musichub.admin.category.repositories.CategoryRepository;
import com.softuni.musichub.admin.category.services.CategoryService;
import com.softuni.musichub.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final MapperUtil mapperUtil;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               MapperUtil mapperUtil) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public void addCategory(AddCategory addCategory) {
        Category category = this.mapperUtil.getModelMapper()
                .map(addCategory, Category.class);
        this.categoryRepository.save(category);
    }

    @Override
    public CategoryView findByName(String categoryName) {
        Category category = this.categoryRepository.findByName(categoryName);
        if (category == null) {
            return null;
        }

        CategoryView categoryView = this.mapperUtil.getModelMapper()
                .map(category, CategoryView.class);
        return categoryView;
    }

    @Override
    public Page<CategoryView> findAll(Pageable pageable) {
        Page<Category> categoryPage = this.categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        List<CategoryView> categoryViews = this.mapperUtil
                .convertAll(categories, CategoryView.class);
        Long totalElements = categoryPage.getTotalElements();
        Page<CategoryView> categoryViewPage = new PageImpl<>(categoryViews, pageable,
                totalElements);
        return categoryViewPage;
    }

    @Override
    public List<CategoryView> findAll() {
        Iterable<Category> categories = this.categoryRepository.findAll();
        List<CategoryView> categoryViews = this.mapperUtil.convertAll(categories, CategoryView.class);
        return categoryViews;
    }

    @Override
    public CategoryView findById(Long categoryId) {
        Optional<Category> optionalCategory = this.categoryRepository
                .findById(categoryId);
        if (!optionalCategory.isPresent()) {
            return null;
        }

        Category category = optionalCategory.get();
        CategoryView categoryView = this.mapperUtil.getModelMapper()
                .map(category, CategoryView.class);
        return categoryView;
    }

    @Override
    public void deleteById(Long categoryId) {
        boolean isCategoryExists = this.categoryRepository.existsById(categoryId);
        if (!isCategoryExists) {
            return;
        }

        this.categoryRepository.deleteById(categoryId);
    }

    @Override
    public void edit(EditCategory editCategory, Long id) {
        boolean isCategoryExists = this.categoryRepository.existsById(id);
        if (!isCategoryExists) {
            return;
        }

        Category category = this.mapperUtil.getModelMapper()
                .map(editCategory, Category.class);
        category.setId(id);
        this.categoryRepository.save(category);
    }
}
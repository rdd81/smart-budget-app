package com.smartbudget.service;
import com.smartbudget.dto.CategoryResponse;
import com.smartbudget.entity.Category;
import com.smartbudget.entity.CategoryType;
import com.smartbudget.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies category retrieval and caching logic.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CategoryServiceTest.Config.class)
class CategoryServiceTest {

    @Configuration
    @EnableCaching
    @Import(CategoryService.class)
    static class Config {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("categories");
        }

        @Bean
        CategoryRepository categoryRepository() {
            return Mockito.mock(CategoryRepository.class);
        }
    }

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category groceries;

    @BeforeEach
    void setUp() {
        groceries = new Category("Groceries", CategoryType.EXPENSE, "Food and household items");
        groceries.setId(UUID.randomUUID());
        when(categoryRepository.findAll(any(Sort.class))).thenReturn(List.of(groceries));
    }

    @Test
    void getAllCategories_ShouldCacheAndMapResults() {
        List<CategoryResponse> firstCall = categoryService.getAllCategories();
        List<CategoryResponse> secondCall = categoryService.getAllCategories();

        assertThat(firstCall).hasSize(1);
        assertThat(firstCall.get(0).getName()).isEqualTo("Groceries");
        assertThat(firstCall.get(0).getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(secondCall).hasSize(1);

        verify(categoryRepository, times(1)).findAll(any(Sort.class));
    }
}

package com.smartbudget.repository;

import com.smartbudget.entity.Category;
import com.smartbudget.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Category entity.
 * Provides CRUD operations and custom query methods for categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Find all categories of a specific type.
     *
     * @param type the category type (INCOME or EXPENSE)
     * @return a list of categories matching the specified type
     */
    List<Category> findByType(CategoryType type);

    /**
     * Find a category by name.
     *
     * @param name the category name to search for
     * @return a list of categories with the specified name
     */
    List<Category> findByName(String name);
}

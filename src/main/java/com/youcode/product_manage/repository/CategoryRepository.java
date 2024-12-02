package com.youcode.product_manage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.youcode.product_manage.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByName(String name);
}

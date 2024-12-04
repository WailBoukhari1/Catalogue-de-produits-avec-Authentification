package com.youcode.product_manage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.youcode.product_manage.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByDesignation(String designation);
    boolean existsByDesignationAndIdNot(String designation, Long id);
    Page<Product> findByDesignationContainingIgnoreCase(String designation, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByDesignationContainingIgnoreCaseAndCategoryId(String designation, Long categoryId, Pageable pageable);
}

package com.quinbay.inventory.repository;

import com.quinbay.inventory.model.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {

    List<ProductHistory> findByProductIdOrderByModifiedOnDesc(Long productId);
}
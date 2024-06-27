package com.quinbay.inventory.repository;

import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCode(String code);
}

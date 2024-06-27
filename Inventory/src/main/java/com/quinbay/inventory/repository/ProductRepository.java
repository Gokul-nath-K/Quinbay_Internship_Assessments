package com.quinbay.inventory.repository;

import com.quinbay.inventory.model.*;
import org.springframework.data.jpa.repository.*;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByCode(String code);
}
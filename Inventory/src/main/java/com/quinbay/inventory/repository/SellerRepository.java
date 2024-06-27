package com.quinbay.inventory.repository;

import com.quinbay.inventory.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}

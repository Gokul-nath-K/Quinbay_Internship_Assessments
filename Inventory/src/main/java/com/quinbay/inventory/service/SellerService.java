package com.quinbay.inventory.service;

import com.quinbay.inventory.DTO.SellerRequestDTO;
import com.quinbay.inventory.model.Seller;

import java.util.List;

public interface SellerService {

    List<Seller> getAllSellers();

    public Seller getById(Long id);

    Seller createSeller(SellerRequestDTO seller);

    Seller updateSeller(Long id, Seller seller);

    void deleteSeller(Long id);
}

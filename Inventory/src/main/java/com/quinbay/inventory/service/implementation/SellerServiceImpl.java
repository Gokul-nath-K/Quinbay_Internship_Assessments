package com.quinbay.inventory.service.implementation;

import com.quinbay.inventory.DTO.SellerRequestDTO;
import com.quinbay.inventory.exceptions.SellerException;
import com.quinbay.inventory.model.Seller;
import com.quinbay.inventory.repository.SellerRepository;
import com.quinbay.inventory.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    SellerRepository sellerRepository;

    @Override
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    @Override
    public Seller getById(Long id) {

        return sellerRepository.findById(id)
                .orElseThrow(()-> new SellerException("Seller not found with id: " + id));
    }

    @Override
    public Seller createSeller(SellerRequestDTO sellerRequest) {

        Seller seller = Seller.builder()
                .name(sellerRequest.getName())
                .email(sellerRequest.getEmail())
                .phoneNumber(sellerRequest.getPhone_number())
                .products(new ArrayList<>())
                .build();

        return sellerRepository.save(seller);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) {
        return sellerRepository.findById(id).map(existingSeller -> {
            existingSeller.setName(seller.getName());
            existingSeller.setEmail(seller.getEmail());
            existingSeller.setPhoneNumber(seller.getPhoneNumber());
            return sellerRepository.save(existingSeller);
        }).orElseThrow(() -> new SellerException("Seller not found with id " + id));
    }

    @Override
    public void deleteSeller(Long id) {
        if (!sellerRepository.existsById(id)) {
            throw new SellerException("Seller not found with id " + id);
        }
        sellerRepository.deleteById(id);
    }
}

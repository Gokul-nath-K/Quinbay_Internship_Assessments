package com.quinbay.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private long id;
    private String code;
    private String name;
    private double price;
    private long quantity;
    private Category category;
}
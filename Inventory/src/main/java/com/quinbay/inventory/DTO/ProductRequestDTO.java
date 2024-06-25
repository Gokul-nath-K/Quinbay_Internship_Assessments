package com.quinbay.inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    private String productId;
    private String productName;
    private double productPrice;
    private long productQuantity;
    private String categoryId;
}

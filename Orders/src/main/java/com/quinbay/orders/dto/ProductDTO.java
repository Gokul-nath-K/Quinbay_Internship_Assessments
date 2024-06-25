package com.quinbay.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private long id;
    private String productId;
    private String productName;
    private double productPrice;
    private long productQuantity;
    private CategoryDTO category;
}

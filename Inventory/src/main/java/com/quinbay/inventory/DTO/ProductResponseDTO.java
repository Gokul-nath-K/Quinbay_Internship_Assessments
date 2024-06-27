package com.quinbay.inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String code;
    private String name;
    private Double price;
    private Long quantity;
    private Long sellerId;
    private String categoryCode;
}

package com.quinbay.orders.model;

import com.quinbay.orders.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    private long id;
    private String code;
    private double totalPrice;
    private long numberOfItems;
    private long totalQuantity;
    private Date orderedOn;
    private List<ProductDTO> products;
}

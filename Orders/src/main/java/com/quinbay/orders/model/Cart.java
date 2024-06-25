package com.quinbay.orders.model;

import com.quinbay.orders.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {

    private long id;
    private String cartId;
    private List<ProductDTO> products;
}

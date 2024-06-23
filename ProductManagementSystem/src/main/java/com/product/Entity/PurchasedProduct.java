package com.product.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasedProduct {

    private long purchaseId;
    private String productId;
    private String productName;
    private long quantity;
    private double productPrice;
    private double totalPrice;
}

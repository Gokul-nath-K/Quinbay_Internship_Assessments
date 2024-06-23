package com.product.Dto;

import com.product.Entity.PurchasedProduct;
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
public class OrdersDTO {

    private long id;
    private double totalPrice;
    private long numberOfItems;
    private String orderStatus;
    private Date orderedOn;
    private Date updatedOn;
    private List<PurchasedProduct> purchasedProducts;
}

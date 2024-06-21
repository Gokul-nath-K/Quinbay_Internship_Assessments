package com.product.Entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders {

    private long id;
    private String order_id;
    private double totalPrice;
    private long numberOfItems;
    private String orderStatus;
    private Date orderedOn;
    private Date updatedOn;
}

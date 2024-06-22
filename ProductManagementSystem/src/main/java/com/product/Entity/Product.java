package com.product.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class    Product {

    private long id;
    private String product_id;
    private String product_name;
    private double product_price;
    private long product_stock;
    private boolean isDeleted;
    private Category category;


    public Product(String productIdPrefix, String productName, double productPrice, long productStock, boolean isDeleted, Category category) {
        this.product_id = productIdPrefix + "_";
        this.product_name = productName;
        this.product_price = productPrice;
        this.product_stock = productStock;
        this.isDeleted = isDeleted;
        this.category = category;
    }
}

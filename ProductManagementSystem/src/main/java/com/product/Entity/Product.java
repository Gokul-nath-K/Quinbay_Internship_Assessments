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


    @Override
    public String toString() {
        return id + "," + product_id + "," + product_name + "," + product_price + "," + product_stock + "," + isDeleted;
    }

    public static Product fromCSV(String csvLine) {
        String[] fields = csvLine.split(",");
        long id = Long.parseLong(fields[0]);
        String product_id = fields[1];
        String name = fields[2];
        double price = Double.parseDouble(fields[3]);
        long quantity = Long.parseLong(fields[4]);
        boolean isDeleted = Boolean.parseBoolean(fields[5]);
//        return new Product(id, product_id, name, price, quantity, isDeleted);
        return null;
    }
}

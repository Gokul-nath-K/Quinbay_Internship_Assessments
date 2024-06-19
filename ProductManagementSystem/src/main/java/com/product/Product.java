package com.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private long product_id;
    private String product_name;
    private double product_price;
    private long product_stock;
    private boolean isDeleted;



    @Override
    public String toString() {
        return product_id + "," + product_name + "," + product_price + "," + product_stock + "," + isDeleted;
    }

    public static Product fromCSV(String csvLine) {
        String[] fields = csvLine.split(",");
        long id = Long.parseLong(fields[0]);
        String name = fields[1];
        double price = Double.parseDouble(fields[2]);
        long quantity = Long.parseLong(fields[3]);
        boolean isDeleted = Boolean.parseBoolean(fields[4]);
        return new Product(id, name, price, quantity, isDeleted);
    }
}

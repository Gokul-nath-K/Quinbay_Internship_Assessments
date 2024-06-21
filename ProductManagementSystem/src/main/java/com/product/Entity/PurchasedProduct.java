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


    @Override
    public String toString() {
        return purchaseId + "," +
                productId + "," +
                productName + "," +
                quantity + "," +
                productPrice + "," +
                totalPrice;
    }

    public static PurchasedProduct fromCSV(String csvLine) {
        String[] fields = csvLine.split(",");
        if (fields.length != 6) {
            throw new IllegalArgumentException("Invalid CSV line: " + csvLine);
        }

        long purchaseId = Long.parseLong(fields[0].trim());
        String productId = fields[1];
        String productName = fields[2].trim();
        long quantity = Long.parseLong(fields[3].trim());
        double productPrice = Double.parseDouble(fields[4].trim());
        double totalPrice = Double.parseDouble(fields[5].trim());

        return new PurchasedProduct(purchaseId, productId, productName, quantity, productPrice, totalPrice);
    }
}

package com.product.Services.Impl;

import com.product.Entity.Product;
import com.product.Entity.PurchasedProduct;
import com.product.Services.FileHandler;
import com.product.Services.ProductFileServices;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductFileServiceImplementation implements ProductFileServices {

    static long purchaseId = 0;

    public ProductFileServiceImplementation(String filename, String purchaseFile) {

        purchaseId = fileHandler.size(purchaseFile);
        loadProductFile(filename);
    }

    private void loadProductFile(String filename) {

        productList = fileHandler.readProductsFromCSV(filename);
    }

    List<Product> productList = new ArrayList<>();

    FileHandler fileHandler = new FileHandler();

    @Override
    public void addProduct(Product product, String filename) {

        fileHandler.writeProductsToCSV(product, filename, true);

        loadProductFile(filename);
    }

    @Override
    public Product viewProductById(long id, String fileName) {

        for(Product product : productList) {

            if(product.getProduct_id() == id && !product.isDeleted()) {

                display(product);
                return product;
            }
        }
        return null;
    }

    @Override
    public int viewAllProducts(String fileName) {

        int count = 0;

        productList = fileHandler.readProductsFromCSV(fileName);
        for(Product product : productList) {
                display(product);
                ++count;
        }
        return count ;
    }

    @Override
    public void updateById(long id, String field, String value, int updateType, String filename) {

        for (Product product : productList) {

            if (product.getProduct_id() == id) {

                if(product.isDeleted()) {

                    System.out.println("Product unavailable!");
                    return;
                }
                if(field.equalsIgnoreCase("stock")) {

                    try {

                        if(updateType == 1) {

                            product.setProduct_stock(product.getProduct_stock() + Long.parseLong(value));
                        }
                        else {

                            product.setProduct_stock(Long.parseLong(value));
                        }
                    }
                    catch (Exception exception) {

                        System.out.println("Invalid stock value!");
                    }
                }
                else if(field.equalsIgnoreCase("price")) {

                    try {

                        if(updateType == 1)
                            product.setProduct_price(product.getProduct_price() + Double.parseDouble(value));
                        else
                            product.setProduct_price(Double.parseDouble(value));
                    }
                    catch (Exception exception) {

                        System.out.println("Invalid price value!");
                    }
                }
                break;
            }
        }

        fileHandler.writeProductsToCSV(productList, filename, false);
    }


    @Override
    public boolean purchaseProduct(long id, int quantity, String productFile, String filename) {

        boolean found = false;

        if (quantity <= 0) {
            System.out.println("Invalid quantity");
            return false;
        }

        for (Product product : productList) {

            if (product.getProduct_id() == id) {

                found = true;

                if(product.isDeleted()) {

                    System.out.println("Product unavailable!");
                    return false;
                }
                if(product.getProduct_stock() >= quantity) {

                    PurchasedProduct purchasedProduct = PurchasedProduct.builder()
                            .productId(product.getProduct_id())
                            .purchaseId(purchaseId++)
                            .productName(product.getProduct_name())
                            .productPrice(product.getProduct_price())
                            .quantity(quantity)
                            .totalPrice(quantity * product.getProduct_price())
                            .build();

                    product.setProduct_stock(product.getProduct_stock() - quantity);

                    updateById(product.getProduct_id(), "stock", Long.toString(product.getProduct_stock()), 0, productFile);

                    fileHandler.writeProductsToCSV(purchasedProduct, filename, true);

                    display(purchasedProduct);

                    System.out.println("Product purchase successfully");

                    return true;
                }
                else {
                    System.out.println("Product is out of stock");
                    return  false;
                }
            }
        }
        if(!found) {

            System.out.println("Product not found");
        }
        return  false;
    }

    @Override
    public void getPurchaseProductList(String fileName) {

        List<PurchasedProduct> purchaseList = fileHandler.readPurchasedProductsFromCSV(fileName);

        for(PurchasedProduct product : purchaseList) {

            display(product);
        }
    }

    @Override
    public void removeProductById(long id, String productFile) {

        List<Product> products = fileHandler.readProductsFromCSV(productFile);

        for (Product product : products) {
            if (product.getProduct_id() == id) {
                product.setDeleted(true);
            }
        }

        fileHandler.writeProductsToCSV(products, productFile, false);
    }

    @Override
    public boolean isProductExist(long id, String fileName) {

        List<Product> products = fileHandler.readProductsFromCSV(fileName);

        boolean found = false;

        for(Product product: products) {

            if(product.getProduct_id() == id) {
                found = true;
                break;
            }
        }

        return found;
    }


    static void display(Product product) {

        System.out.printf("Product Details:%nID: %d%nName: %s%nPrice: %.2f%nQuantity: %d%nIsDeleted: %b%n",
                product.getProduct_id(),
                product.getProduct_name(),
                product.getProduct_price(),
                product.getProduct_stock(),
                product.isDeleted());
        System.out.println();
    }

    public static void display(PurchasedProduct product) {
        System.out.printf("Purchased Product Details:%n" +
                        "Purchase ID: %d%n" +
                        "Product ID: %d%n" +
                        "Product Name: %s%n" +
                        "Quantity: %d%n" +
                        "Product Price: %.2f%n" +
                        "Total Price: %.2f%n",
                product.getPurchaseId(),
                product.getProductId(),
                product.getProductName(),
                product.getQuantity(),
                product.getProductPrice(),
                product.getTotalPrice());
        System.out.println();
    }

}

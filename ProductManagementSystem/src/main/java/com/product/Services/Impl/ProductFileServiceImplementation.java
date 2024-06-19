package com.product.Services.Impl;

import com.product.Product;
import com.product.Services.FileHandler;
import com.product.Services.ProductFileServices;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductFileServiceImplementation implements ProductFileServices {


    List<Product> productList = new ArrayList<>();

    FileHandler fileHandler = new FileHandler();

    @Override
    public void addProduct(Product product, String filename) {

        boolean isEmpty = fileHandler.isFileEmpty(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            if (isEmpty) {
                writer.write("ID,Name,Price,Quantity,isDeleted");
                writer.newLine();
            }

            display(product);
            writer.write(product.toString());
            writer.newLine();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Product viewProductById(long id, String fileName) {

        productList = fileHandler.readProductsFromCSV(fileName);

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
            if(!product.isDeleted()) {

                display(product);
                ++count;
            }
        }
        return count ;
    }

    @Override
    public void updateStockById(long id, long stock, String filename) {

        List<Product> products = fileHandler.readProductsFromCSV(filename);

        for (Product product : products) {
            if (product.getProduct_id() == id) {
                product.setProduct_stock(stock);
                break;
            }
        }

        fileHandler.writeProductsToCSV(products, filename, false);
    }

    @Override
    public void updatePriceById(long id, double price, String fileName) {

        List<Product> products = fileHandler.readProductsFromCSV(fileName);

        for (Product product : products) {
            if (product.getProduct_id() == id) {
                product.setProduct_price(price);
                break;
            }
        }

        fileHandler.writeProductsToCSV(products, fileName, false);
    }

    @Override
    public boolean purchaseProduct(long id, int quantity, String productFile, String filename) {

        List<Product> productList = fileHandler.readProductsFromCSV(productFile);
        List<Product> purchaseList = fileHandler.readProductsFromCSV(filename);

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

                    Product temp_product = product;
                    temp_product.setProduct_stock(quantity);

                    purchaseList.add(temp_product);
                    product.setProduct_stock(product.getProduct_stock() - quantity);

                    updateStockById(product.getProduct_id(), product.getProduct_stock(), productFile);

                    fileHandler.writeProductsToCSV(purchaseList, filename, false);

                    System.out.printf("Name: %s%nPrice: %.2f%nStock: %d%nPurchasePrice: %2f%n",
                            product.getProduct_name(),
                            product.getProduct_price(),
                            quantity,
                            product.getProduct_price() * quantity);
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

        List<Product> purchaseList = fileHandler.readProductsFromCSV(fileName);

        for(Product product : purchaseList) {

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

        System.out.printf("Product Details:%nID: %d%nName: %s%nPrice: %.2f%nQuantity: %d%n",
                product.getProduct_id(),
                product.getProduct_name(),
                product.getProduct_price(),
                product.getProduct_stock());
    }
}

package com.product.Utils;

import com.product.Entity.Product;
import com.product.Entity.PurchasedProduct;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHandler {


    public void writeProductsToCSV(List<Product> products, String filename, boolean append) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {
            writer.write("ID,Name,Price,Quantity,isDeleted");
            writer.newLine();
            for (Product product : products) {
                writer.write(product.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeProductsToCSV(Product product, String filename, boolean append) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {
            if (isFileEmpty(filename)) {
                writer.write("ID,Prod_ID,Name,Price,Quantity,isDeleted");
                writer.newLine();
            }
            writer.write(product.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeProductsToCSV(PurchasedProduct product, String filename, boolean append) {


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {
            if (isFileEmpty(filename)) {
                writer.write("purchaseId,productId,productName,quantity,productPrice,totalPrice");
                writer.newLine();
            }
            writer.write(product.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Product> readProductsFromCSV(String filename) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Product product = Product.fromCSV(line);
                products.add(product);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return products;
    }

    public List<PurchasedProduct> readPurchasedProductsFromCSV(String filename) {
        List<PurchasedProduct> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                PurchasedProduct product = PurchasedProduct.fromCSV(line);
                products.add(product);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return products;
    }

    public boolean isFileEmpty(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                return !scanner.hasNextLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return true;
    }

    public int size(String fileName) {

        int lineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while (br.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());        }

        return lineCount;
        }


    public  void clearFile(String filename) {
        try (FileWriter writer = new FileWriter(filename, false)) {
            System.out.println();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}

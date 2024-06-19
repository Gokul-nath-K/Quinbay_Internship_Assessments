package com.product.Services;

import com.product.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHandler {


    public void writeProductsToCSV(List<Product> products, String filename, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("ID,Name,Price,Quantity,isDeleted");
            writer.newLine();
            for (Product product : products) {
                writer.write(product.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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


    public  void clearFile(String filename) {
        try (FileWriter writer = new FileWriter(filename, false)) {
            System.out.println();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}

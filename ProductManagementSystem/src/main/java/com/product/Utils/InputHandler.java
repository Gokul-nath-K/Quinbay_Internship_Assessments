package com.product.Utils;

import com.product.Dto.OrderItemDTO;
import com.product.Entity.Category;
import com.product.Entity.Product;
import com.product.Main;
import com.product.Services.Impl.CategoryServiceImpl;
import com.product.Services.Impl.ProductDbServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputHandler {

    List<Product> order_items = new ArrayList<>();

    CategoryServiceImpl categoryService = new CategoryServiceImpl();

    ProductDbServiceImpl productDbService = new ProductDbServiceImpl();


    public Product addProductInput() {

        List<Category> categories = categoryService.getAllCategories();


        if(categories.size() == 0) {

            System.out.println("Please enter category first!");
            return null;
        }
        System.out.println("Enter name: ");
        String name = Main.sc.nextLine();

        System.out.println("Enter stock: ");
        long stock = Main.sc.nextLong();

        if (stock <= 0) {
            System.out.println("Enter a valid stock value!");
            return null;
        }

        System.out.println("Enter price: ");
        double price = Main.sc.nextDouble();

        if (price <= 0) {
            System.out.println("Enter a valid price!");
            return null;
        }


        long cat_id;

        while(true) {

            System.out.println();
            System.out.println("List of Categories: ");
            for(Category category : categories) {

                System.out.println("Id : " + category.getId() + "\tName: " + category.getName());
            }
            System.out.println();

            System.out.println("Enter category ID: ");
            cat_id = Main.sc.nextLong();

            Category temp = categoryService.getCategoryById(cat_id);

            if(categories.contains(temp) || cat_id == -1) {
                break;
            }
            else {

                System.out.println("Enter valid Category! or [-1] to exit!");
            }
        }

        if(cat_id == -1) return null;

        Product product = new Product("PROD", name, price, stock, false, categoryService.getCategoryById(cat_id));

        return product;
    }

    public String viewProductByIdInput() {

        System.out.println("Enter id of the product:");
        String id = Main.sc.nextLine();

        return id;
    }
    public List<OrderItemDTO> getOrderInput() {

        Main.sc.nextLine();
        List<OrderItemDTO> products = new ArrayList<>();
        while (true) {

            System.out.println("Enter productId of product to be purchased: ");
            String p_id = Main.sc.nextLine();

            Product product = productDbService.getProductById(p_id);

            if(product == null) {
                return null;
            }
            if(product.isDeleted()) {

                System.out.println("Product unavailable!");
                return null;
            }

            long quantity = productDbService.getProductQuantity(p_id);

            System.out.println("Available stock: " + quantity);

            System.out.println("Enter the quantity: ");
            int p_quantity = Main.sc.nextInt();
            Main.sc.nextLine();

            if(p_quantity > quantity) {

                System.out.println("Entered quantity exceeds available stock.");
                System.out.println("Available stock: " + quantity);
                return null;
            }

            Double price = productDbService.getProductPrice(p_id);
            if(price != null) {
                double p_price = price.doubleValue();
                products.add(new OrderItemDTO(p_id, p_quantity, p_price));
            }
            else {

                continue;
            }

            System.out.println("Do you want to add more products? (yes/no)");
            String response = Main.sc.nextLine();
            if (response.equalsIgnoreCase("no")) {
                break;
            }
        }
        return products;
    }

}

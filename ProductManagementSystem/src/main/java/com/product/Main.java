package com.product;

import com.product.Dto.OrderItemDTO;
import com.product.Entity.Category;
import com.product.Entity.Product;
import com.product.Services.Impl.*;
import com.product.Utils.DatabaseConnection;
import com.product.Utils.InputHandler;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

@RequiredArgsConstructor
public class Main {

    ProductDbServiceImpl productDbService = new ProductDbServiceImpl();
    OrderDbServiceImpl orderDbService = new OrderDbServiceImpl();

    static long id;

    static long cat_uid = new Random().nextInt(0, 1000);

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        DatabaseConnection app = new DatabaseConnection();
        app.connectPostgresDb();

        InputHandler inputHandler = new InputHandler();

        ProductDbServiceImpl productDbService = new ProductDbServiceImpl();
        OrderDbServiceImpl orderDbService = new OrderDbServiceImpl();
        CategoryServiceImpl categoryService = new CategoryServiceImpl();

        boolean flag;

        do {
            flag = false;

            System.out.println("[1] Add new Product\n[2] View the Product\n[3] View all products\n[4] Update Stock\n[5] Update price\n[6] Purchase product\n[7] Remove product\n[8] View purchase list\n[9] Add category\n[10] Exit");
            System.out.print("\nEnter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {


                case 1:

                    sc.nextLine();


                    List<Category> categories = categoryService.getAllCategories();


                    if(categories.size() == 0) {

                        System.out.println("Please enter category first!");
                        break;
                    }
                    System.out.println("Enter name: ");
                    String name = sc.nextLine();

                    System.out.println("Enter stock: ");
                    long stock = sc.nextLong();

                    if (stock <= 0) {
                        System.out.println("Enter a valid stock value!");
                        break;
                    }

                    System.out.println("Enter price: ");
                    double price = sc.nextDouble();

                    if (price <= 0) {
                        System.out.println("Enter a valid price!");
                        break;
                    }


                    long cat_id;

                    while(true) {

                        for(Category category : categories) {

                            System.out.println("Id : " + category.getId() + "\tName: " + category.getName());
                        }
                        System.out.println();

                        System.out.println("Enter category ID: ");
                        cat_id = sc.nextLong();

                        Category temp = categoryService.getCategoryById(cat_id);

                        if(categories.contains(temp)) {
                            break;
                        }
                        else {

                            System.out.println("Enter valid Category!");
                        }
                    }



                    Product product = new Product("PROD", name, price, stock, false, categoryService.getCategoryById(cat_id));

                    if(product == null)
                            break;
                    productDbService.addProduct(product);
                    break;

                case 2:

                    sc.nextLine();
                    String p_id = inputHandler.viewProductByIdInput();

                    product = productDbService.getProductById(p_id);

                    if(product != null)
                        productDbService.display(product);

                    break;



                case 3:

                    List<Product> products = productDbService.getAllProducts();

                    if(!products.isEmpty())
                        products.stream().forEach(productDbService::display);
                    else
                        System.out.println("Product list is empty! Please add product first");

                    break;

                case 4:


                    if(productDbService.getSizeOfCollection() == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    sc.nextLine();

                    System.out.println("Enter product_id of product to be updated: ");
                    String us_id = sc.nextLine();

                    System.out.println("Do you want to [1]update or [0]replace stock value: ");
                    int updateType = sc.nextInt();

                    if(updateType != 0 && updateType != 1) {
                        System.out.println("Enter valid option!");
                        break;
                    }

                    System.out.println("Enter the new stock value: ");
                    long us_stock = sc.nextLong();

                    if(us_stock <= 0){
                        System.out.println("Enter valid stock value!");
                        break;
                    }

                    if(productDbService.updateById(us_id, "stock", Long.toString(us_stock), updateType)) {

                        System.out.println("Updated successfully");
                    }
                    else {

                        System.out.println("Update failed!");
                    }
                    break;


                case 5:

                    if(productDbService.getSizeOfCollection() == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    sc.nextLine();

                    System.out.println("Enter product_id of product to be updated: ");
                    us_id = sc.nextLine();

                    System.out.println("Do you want to [1]update or [0]replace price  value: ");
                    updateType = sc.nextInt();

                    if(updateType != 0 && updateType != 1) {
                        System.out.println("Enter valid option!");
                        break;
                    }

                    System.out.println("Enter the new price value: ");
                    price = sc.nextLong();

                    if(price <= 0){
                        System.out.println("Enter valid price value!");
                        break;
                    }

                    if(productDbService.updateById(us_id, "price", Double.toString(price), updateType)) {

                        System.out.println("Updated successfully");
                    }
                    else {

                        System.out.println("Update failed!");
                    }
                    break;


                case 6:

                    List<OrderItemDTO> orderItems = inputHandler.getOrderInput();
                    orderDbService.purchaseProducts(orderItems);
                    break;

                case 8:

                    orderDbService.displayAllOrdersWithPurchasedProducts();
                    break;

                case 7:

                    sc.nextLine();
                    if(productDbService.getSizeOfCollection() == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    System.out.println("Enter id of the product to be removed:");
                    String id = sc.nextLine();

                    productDbService.removeProductById(id);
                    break;

                case 9:
                    sc.nextLine();
                    System.out.println("Enter category name: ");
                    name = sc.nextLine();

                    categoryService.addCategory(new Category(cat_uid, name));
                    break;

                default:
                    System.out.println("invalid input!");
            }

            System.out.println("Do you want to continue [1] | [0]");
            choice = sc.nextInt();
            if(choice == 1) {

                flag = true;
            }

        }
        while(flag);
    }
}

package com.product;

import com.product.Dto.OrderItemDTO;
import com.product.Dto.OrdersDTO;
import com.product.Entity.Category;
import com.product.Entity.Product;
import com.product.Services.CategoryServices;
import com.product.Services.Impl.*;
import com.product.Services.OrderDbServices;
import com.product.Services.ProductDbServices;
import com.product.Utils.InputHandler;
import com.product.Utils.XlsFileUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

@RequiredArgsConstructor
public class Main {

    public static Scanner sc = new Scanner(System.in);

    static long cat_uid = new Random().nextInt(0, 1000);

    public static void main(String[] args) {

        ProductDbServices productDbService = new ProductDbServiceImpl();
        OrderDbServices orderDbService = new OrderDbServiceImpl();
        CategoryServices categoryService = new CategoryServiceImpl();

        InputHandler inputHandler = new InputHandler();

        boolean flag;

        do {

            System.out.println("""
                    [1] Add new Product
                    [2] View the Product
                    [3] View all products
                    [4] Update Stock
                    [5] Update price
                    [6] Purchase product
                    [7] View purchase list
                    [8] Add category
                    [9] Remove product
                    [10] Get order details
                    [11] Export to excel
                    [12] Exit""");

            System.out.print("\nEnter your choice: ");
            int choice = sc.nextInt();
            String ch = "";

            switch (choice) {

                case 1:

                    sc.nextLine();
                    Product product = inputHandler.addProductInput();
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
                        products.forEach(productDbService::display);
                    else
                        System.out.println("Product list is empty! Please add product first");

                    break;

                case 4:

                    if(productDbService.getSizeOfCollection() == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    sc.nextLine();

                    String us_id;

                    while(true) {

                        System.out.println("Enter product_id of product to be updated: ");
                        us_id = sc.nextLine();

                        if(us_id.trim().equals("-1"))
                            break;
                        if(productDbService.isProductExist(us_id) == null) {

                            System.out.println("Enter valid id or [-1] to exit");
                        }
                        else {

                            boolean isDeleted = productDbService.getProductById(us_id).isDeleted();
                            if (isDeleted) {
                                System.out.println("Product unavailable!");
                                us_id = "-1";
                                break;
                            }

                            productDbService.display(productDbService.getProductById(us_id));
                            break;
                        }
                    }
                    if(us_id.trim().equals("-1"))
                        break;

                    System.out.println("Do you want to [1]update | [0]replace stock value: ");
                    int updateType = sc.nextInt();

                    if(updateType != 0 && updateType != 1) {
                        System.out.println("Enter valid option!");
                        break;
                    }

                    System.out.println("Enter stock value to be updated: ");
                    long us_stock = sc.nextLong();

                    if(us_stock <= 0){
                        System.out.println("Enter valid stock value!");
                        break;
                    }

                    if(productDbService.updateById(us_id, "stock", Long.toString(us_stock), updateType))
                        System.out.println("Updated successfully");
                    else
                        System.out.println("Update failed!");

                    break;


                case 5:

                    if(productDbService.getSizeOfCollection() == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    sc.nextLine();

                    while(true) {

                        System.out.println("Enter product_id of product to be updated: ");
                        us_id = sc.nextLine();

                        if(us_id.trim().equals("-1"))
                            break;
                        if(productDbService.isProductExist(us_id) == null) {

                            System.out.println("Enter valid id or [-1] to exit");
                        }
                        else {

                            boolean isDeleted = productDbService.getProductById(us_id).isDeleted();
                            if (isDeleted) {
                                System.out.println("Product unavailable!");
                                us_id = "-1";
                                break;
                            }
                            productDbService.display(productDbService.getProductById(us_id));
                            break;
                        }
                    }
                    if(us_id.trim().equals("-1"))
                        break;

                    System.out.println("Do you want to [1]update or [0]replace price  value: ");
                    updateType = sc.nextInt();

                    if(updateType != 0 && updateType != 1) {
                        System.out.println("Enter valid option!");
                        break;
                    }

                    System.out.println("Enter the new price value: ");
                    double price = sc.nextLong();

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

                    if(orderItems == null) {

                        System.out.println("Order cancelled!");
                        break;
                    }
                    orderDbService.purchaseProducts(orderItems);
                    break;

                case 7:

                    orderDbService.displayAllOrdersWithPurchasedProducts();
                    break;

                case 9:

                    sc.nextLine();
                    if(productDbService.getSizeOfCollection() == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    System.out.println("Enter id of the product to be removed:");
                    String id = sc.nextLine();

                    productDbService.removeProductById(id);
                    break;

                case 8:

                    sc.nextLine();
                    System.out.println("Enter category name: ");
                    String name = sc.nextLine();

                    categoryService.addCategory(new Category(cat_uid, name));
                    break;

                case 10:

                    System.out.println("Enter order-id:");
                    long orderId = sc.nextLong();

                    if(orderDbService.getOrderById(orderId) == null) {

                        System.out.println("Invalid order id!");
                        break;
                    }

                    OrdersDTO ordersDTO = orderDbService.getOrderById(orderId);

                    orderDbService.displayOrdersWithPurchasedProducts(ordersDTO);
                    break;

                case 11:

                    XlsFileUtils xlsFileUtils = new XlsFileUtils();
                    xlsFileUtils.exportDatabaseToExcel();;
                    break;

                case 12:

                    sc.nextLine();
                    System.out.println("Are you sure, you want to exit? [yes | no]");
                    ch = sc.nextLine();
                    break;

                default:
                    System.out.println("invalid input!");
            }

            if(ch.equalsIgnoreCase("yes")) {

                System.out.println("Thank you!");
                break;
            }
            else {
                flag = true;
            }

            if(choice != 10) {

                System.out.println("Do you want to continue [1] | [0]");
                choice = sc.nextInt();
                flag = choice == 1;
            }

        }
        while(flag);
    }
}

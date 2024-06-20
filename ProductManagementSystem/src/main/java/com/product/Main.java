package com.product;

import com.product.Entity.Product;
import com.product.Services.FileHandler;
import com.product.Services.Impl.ProductFileServiceImplementation;
import com.product.Services.Impl.ProductServiceImplementation;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class Main {

    static long id;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        final FileHandler fileHandler = new FileHandler();

        final String fileName = "/Users/gokulnathk/Documents/product.csv";
        final String purchaseFileName = "/Users/gokulnathk/Documents/purchase.csv";

        id = fileHandler.size(fileName);

        final ProductServiceImplementation productServiceImplementation = new ProductServiceImplementation();

        final ProductFileServiceImplementation productFileServiceImplementation = new ProductFileServiceImplementation(fileName, purchaseFileName);

        boolean useFile = true;

        boolean flag;

        do {
            flag = false;

            System.out.println("[1] Add new Product\n[2] View the Product\n[3] View all products\n[4] Update Stock\n[5] Update price\n[6] Purchase product\n[7] Remove product\n[8] View purchase list\n[9] Exit");
            System.out.print("\nEnter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {


                case 1:

                    sc.nextLine();
                    System.out.println("Enter name: ");
                    String name = sc.nextLine();

                    System.out.println("Enter stock");
                    long stock = sc.nextLong();

                    if(stock <= 0){
                        System.out.println("Enter valid stock value!");
                        break;
                    }

                    System.out.println("Enter price: ");
                    double price = sc.nextDouble();

                    if(price <= 0){
                        System.out.println("Enter valid price!");
                        break;
                    }

                    Product product = new Product(++id, name, price, stock, false);

                    if(useFile) {

                        productFileServiceImplementation.addProduct(product, fileName);
                    }
                    else {

                        productServiceImplementation.addProduct(product);
                    }

                    System.out.println("Product added successfully");
                    break;

                case 2:

                    System.out.println("Enter id of the product:");
                    long id = sc.nextLong();

                    Product product1 = null;

                    if(useFile) {

                        product1 = productFileServiceImplementation.viewProductById(id, fileName);
                    }
                    else {

                        productServiceImplementation.viewProductById(id);
                    }

                    if(product1 == null) {

                        System.out.println("Product does not exist!");
                    }
                    break;


                case 3:

                    if(fileHandler.isFileEmpty(fileName)) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    int count = productFileServiceImplementation.viewAllProducts(fileName);

                    if(count == 0) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    break;

                case 4:


                    if(fileHandler.isFileEmpty(fileName)) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }


                    System.out.println("Enter product_id of product to be updated: ");
                    long us_id = sc.nextLong();

                    if(!productFileServiceImplementation.isProductExist(us_id, fileName)) {

                        System.out.println("Product not found");
                        break;
                    }

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
                    boolean isUpdated = productFileServiceImplementation.updateById(us_id, "stock", Long.toString(us_stock), updateType, fileName);

                    if(isUpdated) {

                        System.out.println("Updated successfully");
                    }
                    else {

                        System.out.println("Update failed!");
                    }
                    break;

                case 5:

                    if(fileHandler.isFileEmpty(fileName)) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }

                    System.out.println("Enter product_id of product to be updated: ");
                    long up_id = sc.nextLong();

                    if(!productFileServiceImplementation.isProductExist(up_id, fileName)) {

                        System.out.println("Product not found");
                        break;
                    }

                    System.out.println("Do you want to [1]update or [0]replace price value: ");
                    updateType = sc.nextInt();

                    if(updateType != 0 && updateType != 1) {
                        System.out.println("Enter valid option!");
                        break;
                    }

                    System.out.println("Enter the new price value: ");
                    long up_price = sc.nextLong();

                    if(up_price <= 0){
                        System.out.println("Enter valid price!");
                        break;
                    }
                    productFileServiceImplementation.updateById(up_id, "price", Long.toString(up_price), updateType, fileName);
                    break;

                case 6:

                    if(fileHandler.isFileEmpty(fileName)) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }

                    System.out.println("Enter productId of product to be purchased: ");
                    long p_id = sc.nextLong();

                    System.out.println("Enter the quantity: ");
                    int p_quantity = sc.nextInt();

                    productFileServiceImplementation.purchaseProduct(p_id, p_quantity, fileName, purchaseFileName);
                    break;

                case 8:

                    productFileServiceImplementation.getPurchaseProductList(purchaseFileName);
                    break;

                case 7:

                    if(fileHandler.isFileEmpty(fileName)) {

                        System.out.println("Product list is empty! Please add product first");
                        break;
                    }
                    System.out.println("Enter id of the product to be removed:");
                    id = sc.nextLong();

                    if(!productFileServiceImplementation.isProductExist(id, fileName)) {

                        System.out.println("Product not found");
                        break;
                    }
                    productFileServiceImplementation.removeProductById(id, fileName);
                    System.out.println("Product removed successfully");
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

//        fileHandler.clearFile(fileName);
//        fileHandler.clearFile(purchaseFileName);

    }
}

package com.product.Services.Impl;

import com.product.Entity.Product;
import com.product.Services.ProductService;

import java.util.ArrayList;

public class ProductServiceImplementation implements ProductService {


    ArrayList<Product> productList = new ArrayList<>();
    ArrayList<Product> purchaseList = new ArrayList<>();

    @Override
    public void addProduct(Product product) {

        productList.add(product);
    }

    @Override
    public Product viewProductById(long id) {

        for(Product product : productList) {

            if(product.getProduct_id() == id) {

                display(product);
                return product;
            }
        }

        return null;

    }

    @Override
    public void viewAllProducts() {

        for(Product product : productList) {

            display(product);

        }
    }

    @Override
    public void updateStockById(long id, long stock) {

        for (Product product : productList) {

            if (product.getProduct_id() == id) {

                System.out.println("Before Update : ");
                display(product);

                product.setProduct_stock(stock);

                System.out.println("Updated Product : ");
                display(product);
            }
        }
    }
    public void updatePriceById(long id, double price) {

        for(Product product : productList) {

            if(product.getProduct_id() == id) {

                System.out.println("Before Update : ");
                display(product);

                product.setProduct_price(price);

                System.out.println("Updated Product : ");
                display(product);
            }
        }
    }

    @Override
    public boolean purchaseProduct(long id, int quantity) {

        if (quantity <= 0) {

            System.out.println("Invalid quantity");
            return false;
        }

        for (Product product : productList) {

            if (product.getProduct_id() == id) {

                if(product.getProduct_stock() >= quantity) {

                    Product temp_product = product;
                    temp_product.setProduct_stock(quantity);
                    purchaseList.add(temp_product);

                    product.setProduct_stock(product.getProduct_stock() - quantity);

                    System.out.printf("Name: %s%nPrice: %.2f%nStock: %d%nPurchasePrice: %2f%n",
                            product.getProduct_name(),
                            product.getProduct_price(),
                            product.getProduct_stock(),
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
        return  false;
    }

    @Override
    public void getPurchaseProductList() {
        for(Product product : purchaseList) {

            display(product);

        }
    }

    @Override
    public void removeProductById(long id) {

        Product productToBeRemoved = null;

        for(Product product : productList) {

            if(product.getProduct_id() == id) {

                productToBeRemoved = product;
                System.out.println("Removed: " + true);
            }
        }

        if(productToBeRemoved != null)
            productList.remove(productToBeRemoved);
    }

    @Override
    public boolean isEmpty() {
        return productList.isEmpty();
    }


    static void display(Product product) {

        System.out.printf("Product Details:%nID: %d%nName: %s%nPrice: %.2f%nStock: %d%n",
                product.getProduct_id(),
                product.getProduct_name(),
                product.getProduct_price(),
                product.getProduct_stock());
    }
}

package com.product.Services;

import com.product.Product;

public interface ProductService {

    public void addProduct(Product product);

    public Product viewProductById(long id);

    public void viewAllProducts();

    public void updateStockById(long id, long stock);

    public void updatePriceById(long id, double price);

    public boolean purchaseProduct(long id, int quantity);

    public void getPurchaseProductList();

    public void removeProductById(long id);

    public boolean isEmpty();

}

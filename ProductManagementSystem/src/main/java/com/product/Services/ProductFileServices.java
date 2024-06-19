package com.product.Services;

import com.product.Product;

public interface ProductFileServices {

    public void addProduct(Product product, String filename);

    public Product viewProductById(long id, String fileName);

    public int viewAllProducts(String fileName);

    public void updateStockById(long id, long stock, String fileName);

    public void updatePriceById(long id, double price, String fileName);

    public boolean purchaseProduct(long id, int quantity, String productFile, String fileName);

    public void getPurchaseProductList(String fileName);

    public void removeProductById(long id, String productFile);

    public boolean isProductExist(long id, String filename);
}

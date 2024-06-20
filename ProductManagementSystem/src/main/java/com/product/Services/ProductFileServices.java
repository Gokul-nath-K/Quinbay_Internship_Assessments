package com.product.Services;

import com.product.Entity.Product;

public interface ProductFileServices {

    public void addProduct(Product product, String filename);

    public Product viewProductById(long id, String fileName);

    public int viewAllProducts(String fileName);

    public boolean updateById(long id, String field, String value, int updateType, String filename);

    public boolean purchaseProduct(long id, int quantity, String productFile, String fileName);

    public void getPurchaseProductList(String fileName);

    public void removeProductById(long id, String productFile);

    public boolean isProductExist(long id, String filename);
}

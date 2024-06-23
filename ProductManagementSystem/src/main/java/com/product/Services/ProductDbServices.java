package com.product.Services;

import com.mongodb.client.MongoDatabase;
import com.product.Entity.Product;
import com.product.Utils.DatabaseConnection;
import org.bson.Document;

import java.util.List;

public interface ProductDbServices {

    public List<Product> getAllProducts();

    public Product getProductById(String id);

    public Double getProductPrice(String productId);

    public Long getProductQuantity(String productId);

    public void addProduct(Product product);

    public boolean updateById(String id, String field, String value, int updateType);

    public void removeProductById(String id);

    public int getSizeOfCollection();

    public Document isProductExist(String id);

    public void display(Product product);

}

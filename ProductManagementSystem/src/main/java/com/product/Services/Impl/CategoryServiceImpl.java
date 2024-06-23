package com.product.Services.Impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.product.Entity.Category;
import com.product.Services.CategoryServices;
import com.product.Utils.DatabaseConnection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;


public class CategoryServiceImpl implements CategoryServices {

    DatabaseConnection databaseConnection;
    MongoDatabase db;
    private MongoCollection<Document> collection;

    public CategoryServiceImpl() {

        databaseConnection = new DatabaseConnection();
        db = databaseConnection.getDatabase("store_db");
        collection = db.getCollection("category");
    }


    public void addCategory(Category category) {
        Document doc = new Document("id", category.getId())
                .append("name", category.getName());
        collection.insertOne(doc);
        System.out.println("Category added successfully");
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        for (Document doc : collection.find()) {
            Category category = Category.builder()
                    .id(doc.getLong("id"))
                    .name(doc.getString("name"))
                    .build();
            categories.add(category);
        }
        return categories;
    }

    public Category getCategoryById(long id) {
        Document doc = collection.find(eq("id", id)).first();
        if (doc != null) {
            return Category.builder()
                    .id(doc.getLong("id"))
                    .name(doc.getString("name"))
                    .build();
        } else {
                return null;
            }
        }

}

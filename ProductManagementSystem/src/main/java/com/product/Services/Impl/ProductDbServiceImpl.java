package com.product.Services.Impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.product.Entity.Category;
import com.product.Entity.Product;
import com.product.Main;
import com.product.Services.ProductDbServices;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.product.Utils.DatabaseConnection.getDatabase;

public class ProductDbServiceImpl implements ProductDbServices {

    static int id;

    MongoDatabase db;
    final private MongoCollection<Document> productCollection;

    public ProductDbServiceImpl() {

        db = getDatabase("store_db");
        productCollection = db.getCollection("products");
        id = getSizeOfCollection();
    }


    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();

        FindIterable<Document> iterable = productCollection.find();

        try (MongoCursor<Document> cursor = iterable.iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();

                Product product = documentToProduct(doc);
                products.add(product);
            }
        }

        return products;
    }


    public Product getProductById(String id) {

        Document query = new Document("product_id", id);

        Document productDoc = productCollection.find(query).first();

        if (productDoc != null) {
            return documentToProduct(productDoc);
        } else {
            System.out.println("Product not found with id: " + id);
            return null;
        }
    }


    public Double getProductPrice(String productId) {

        Document query = new Document("product_id", productId);

        Document productDoc = productCollection.find(query).first();

        if (productDoc != null) {
            return productDoc.getDouble("product_price");
        } else {
            return null;
        }
    }

    public Long getProductQuantity(String productId) {

        Document query = new Document("product_id", productId);

        Document productDoc = productCollection.find(query).first();

        if (productDoc != null) {
            return productDoc.getLong("product_stock");
        } else {
            return null;
        }
    }

    public void addProduct(Product product) {

        if (product ==null)
            return;

        Document categoryDoc = new Document()
                .append("id", product.getCategory().getId())
                .append("category_name", product.getCategory().getName());

        Document productDoc = new Document()
                .append("product_id", product.getProduct_id() + id++)
                .append("product_name", product.getProduct_name())
                .append("product_price", product.getProduct_price())
                .append("product_stock", product.getProduct_stock())
                .append("isDeleted", product.isDeleted())
                .append("category", categoryDoc);

        productCollection.insertOne(productDoc);

        long id = objectIdToLong(productDoc.getObjectId("_id"));
        product.setId(id);

        System.out.println("Product added with id: " + productDoc.getString("product_id") + "\n");
    }


    public boolean updateById(String id, String field, String value, int updateType) {
        Document query = new Document("product_id", id);

        long quantity = getProductQuantity(id);

        Document update = new Document();
        try {
            if (field.equalsIgnoreCase("stock")) {
                long stock = Long.parseLong(value);
                if (updateType == 1) {

                    System.out.println("Enter updation type: ");
                    System.out.println("1.Increment");
                    System.out.println("2.Decrement");
                    int type = Main.sc.nextInt();
                    if(type == 1) {

                        update = new Document("$inc", new Document("product_stock", stock));
                    }
                    else {

                        if(quantity < stock) {

                            System.out.println("Insufficient stock to reduce!");
                            return false;
                        }
                        update = new Document("$inc", new Document("product_stock", -stock));
                    }

                } else {
                    update = new Document("$set", new Document("product_stock", stock));
                }
            } else if (field.equalsIgnoreCase("price")) {
                double price = Double.parseDouble(value);
                if (updateType == 1) {

                    System.out.println("Enter updation type: ");
                    System.out.println("1.Increment");
                    System.out.println("2.Decrement");
                    int type = Main.sc.nextInt();

                    if(type == 1) {

                        update = new Document("$inc", new Document("product_price", price));
                    }
                    else {

                        if(quantity < price) {

                            System.out.println("Price cannot be updated beyond zero!");
                            return false;
                        }
                        update = new Document("$inc", new Document("product_price", -price));
                    }
                } else {
                    update = new Document("$set", new Document("product_price", price));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid " + field + " value!");
            return false;
        }

        productCollection.updateOne(query, update);


        return true;
    }


    public void removeProductById(String id) {

        Document query = new Document("product_id", id);
        Document productDoc = productCollection.find(query).first();

        if (productDoc == null) {
            System.out.println("Product not found with id: " + id);
            return;
        }

        boolean isDeleted = productDoc.getBoolean("isDeleted", false);
        if (isDeleted) {
            System.out.println("Product unavailable!");
            return;
        }
        try {

            Document update = new Document("$set", new Document("isDeleted", true));
            productCollection.updateOne(query, update);

            System.out.println("Product removed successfully");
        }
        catch (Exception exception) {

            System.out.println(exception.getMessage());
        }
    }


    private Product documentToProduct(Document doc) {

        Document cat_doc = doc.get("category", Document.class);

        Category category = Category.builder()
                .id(cat_doc.getLong("id"))
                .name(cat_doc.getString("name"))
                .build();

        Product product =  new Product(
                doc.getString("product_id"),
                doc.getString("product_name"),
                doc.getDouble("product_price"),
                doc.getLong("product_stock"),
                doc.getBoolean("isDeleted"),
                category
        );
        product.setId(objectIdToLong(doc.getObjectId("_id")));

        return product;
    }

    public static long objectIdToLong(ObjectId objectId) {
        byte[] bytes = objectId.toByteArray();

        return ((long) bytes[0] & 0xffL) << 24 |
                ((long) bytes[1] & 0xffL) << 16 |
                ((long) bytes[2] & 0xffL) << 8  |
                ((long) bytes[3] & 0xffL);
    }

    public int getSizeOfCollection() {
        long count = productCollection.countDocuments();
        if (count > Integer.MAX_VALUE) {
            throw new IllegalStateException("Collection size exceeds the range of int");
        }
        return (int) count;
    }


    public Document isProductExist(String id) {

        Document query = new Document("product_id", id);
        return productCollection.find(query).first();
    }

    @SuppressWarnings("MalformedFormatString")
    public void display(Product product) {

        System.out.printf("Product Details:%nID: %d%nPROD_ID: %s%nName: %s%nPrice: %.2f%nQuantity: %d%nIsDeleted: %b%n",
                product.getId(),
                product.getProduct_id(),
                product.getProduct_name(),
                product.getProduct_price(),
                product.getProduct_stock(),
                product.isDeleted(),
                product.getCategory().getName());
        System.out.println();
    }
}

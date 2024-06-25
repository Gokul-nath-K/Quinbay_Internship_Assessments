package com.quinbay.orders.service;

import com.mongodb.client.*;
import com.quinbay.orders.dto.CategoryDTO;
import com.quinbay.orders.dto.ProductDTO;
import com.quinbay.orders.model.Cart;
import com.quinbay.orders.model.Order;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {

    private final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private final MongoDatabase database = mongoClient.getDatabase("e_commerce");
    private final MongoCollection<Document> orderCollection = database.getCollection("orders");

    CartService cartService = new CartService();

    @Autowired
    RestTemplate restTemplate;

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        FindIterable<Document> documents = orderCollection.find();

        try (MongoCursor<Document> cursor = documents.iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Order order = convertDocumentToOrder(doc);
                orders.add(order);
            }
        }

        return orders;
    }

    public Order getOrderById(String id) {

        Document query = new Document("orderId", id);
        Document doc = orderCollection.find(query).first();

        if (doc != null) {
            return convertDocumentToOrder(doc);
        } else {
            return null;
        }
    }


    public void placeOrder(String cartId) {
        Cart cart = cartService.getCartById(cartId);
        if (cart == null) {
            throw new RuntimeException("Cart not found with ID: " + cartId);
        }

        Order order = new Order();

        long totalQuantity = cart.getProducts().stream().mapToLong(ProductDTO::getProductQuantity).sum();
        long numberOfItems = cart.getProducts().size();
        double totalPrice = cart.getProducts().stream().mapToDouble(p -> p.getProductPrice() * p.getProductQuantity()).sum();

        order.setTotalQuantity(totalQuantity);
        order.setNumberOfItems(numberOfItems);
        order.setTotalPrice(totalPrice);

        order.setOrderId("ORD_" + new Random().nextInt(1, 100));
        order.setOrderedOn(new Date());
        order.setProducts(cart.getProducts());

        Document orderDoc = new Document();
        orderDoc.append("orderId", order.getOrderId())
                .append("totalPrice", order.getTotalPrice())
                .append("numberOfItems", order.getNumberOfItems())
                .append("totalQuantity", order.getTotalQuantity())
                .append("orderedOn", order.getOrderedOn());

        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            orderDoc.append("products", convertProductsToDocuments(order.getProducts()));
        }

        updateProductQuantities(order.getProducts());
        cartService.clearCart(cartId);
        orderCollection.insertOne(orderDoc);
    }

    private void updateProductQuantities(List<ProductDTO> products) {

        for (ProductDTO product : products) {
            ResponseEntity<ProductDTO> response = restTemplate.getForEntity(
                    "http://localhost:8083/product/getProductById/" + product.getProductId(),
                    ProductDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ProductDTO fetchedProduct = response.getBody();
                long newQuantity = fetchedProduct.getProductQuantity() - product.getProductQuantity();
                fetchedProduct.setProductQuantity(newQuantity);

                restTemplate.put("http://localhost:8083/product/updateProductQuantity/", fetchedProduct);
            } else {
                throw new RuntimeException("Failed to fetch product details for ID: " + product.getProductId());
            }
        }
    }

    private List<Document> convertProductsToDocuments(List<ProductDTO> products) {
        List<Document> productDocs = new ArrayList<>();
        for (ProductDTO product : products) {
            Document productDoc = new Document()
                    .append("id", product.getId())
                    .append("productId", product.getProductId())
                    .append("productName", product.getProductName())
                    .append("productPrice", product.getProductPrice())
                    .append("productQuantity", product.getProductQuantity());

            if (product.getCategory() != null) {
                Document categoryDoc = new Document()
                        .append("id", product.getCategory().getId())
                        .append("categoryId", product.getCategory().getCategoryId())
                        .append("categoryName", product.getCategory().getCategoryName());
                productDoc.append("category", categoryDoc);
            }

            productDocs.add(productDoc);
        }
        return productDocs;
    }

    private Order convertDocumentToOrder(Document doc) {

        Order order = new Order();
        order.setOrderId(doc.getString("orderId"));
        order.setTotalPrice(doc.getDouble("totalPrice"));
        order.setNumberOfItems(doc.getLong("numberOfItems"));
        order.setTotalQuantity(doc.getLong("totalQuantity"));
        order.setOrderedOn(doc.getDate("orderedOn"));

        List<ProductDTO> products = new ArrayList<>();
        List<Document> productDocs = (List<Document>) doc.get("products");
        if (productDocs != null) {
            for (Document productDoc : productDocs) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(productDoc.getLong("id"));
                productDTO.setProductId(productDoc.getString("productId"));
                productDTO.setProductName(productDoc.getString("productName"));
                productDTO.setProductPrice(productDoc.getDouble("productPrice"));
                productDTO.setProductQuantity(productDoc.getLong("productQuantity"));

                Document categoryDoc = (Document) productDoc.get("category");
                if (categoryDoc != null) {
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setId(categoryDoc.getLong("id"));
                    categoryDTO.setCategoryId(categoryDoc.getString("categoryId"));
                    categoryDTO.setCategoryName(categoryDoc.getString("categoryName"));
                    productDTO.setCategory(categoryDTO);
                }

                products.add(productDTO);
            }
        }

        order.setProducts(products);
        return order;
    }
}

package com.quinbay.orders.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.quinbay.orders.dto.ProductRequest;
import com.quinbay.orders.dto.CategoryDTO;
import com.quinbay.orders.dto.ProductDTO;
import com.quinbay.orders.model.Cart;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CartService {

    private final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private final MongoDatabase database = mongoClient.getDatabase("e_commerce");
    private final MongoCollection<Document> cartCollection = database.getCollection("carts");

    @Autowired
    RestTemplate restTemplate;

    public List<Cart> getAllCarts() {
        List<Cart> carts = new ArrayList<>();
        for (Document doc : cartCollection.find()) {
            carts.add(convertDocumentToCart(doc));
        }
        return carts;
    }

    public Cart getCartById(String cartId) {
        Document doc = cartCollection.find(Filters.eq("cartId", cartId)).first();
        return convertDocumentToCart(doc);
    }


    public String createCart() {

        Document cartDoc = new Document()
                .append("cartId", "CART_" + new Random().nextInt(1, 100))
                .append("products", new ArrayList<>());

        cartCollection.insertOne(cartDoc);

        return "Cart created with id: " + cartDoc.getString("cartId");

    }


    public String addProductToCart(String cartId, ProductRequest productRequest) {
        if (productRequest.getProductQuantity() <= 0) {
            return "Quantity cannot be zero or negative";
        }

        ResponseEntity<ProductDTO> response = restTemplate.getForEntity(
                "http://localhost:8083/product/getProductById/" + productRequest.getProductId(),
                ProductDTO.class
        );

        ProductDTO fetchedProduct;

        if (response.getStatusCode() == HttpStatus.OK) {
            fetchedProduct = response.getBody();
            if (fetchedProduct.getProductQuantity() < productRequest.getProductQuantity()) {
                return "Not enough quantity available for product " + fetchedProduct.getProductName();
            } else {
                Cart cart = getCartById(cartId);
                if (cart == null) {
                    return "Cart not found with ID: " + cartId;
                }

                List<ProductDTO> productList = cart.getProducts();
                boolean productExists = false;

                for (ProductDTO existingProduct : productList) {
                    if (existingProduct.getProductId().equals(productRequest.getProductId())) {
                        long totalQuantity = existingProduct.getProductQuantity() + productRequest.getProductQuantity();
                        if (totalQuantity > fetchedProduct.getProductQuantity()) {
                            return "Total quantity exceeds available quantity for product " + fetchedProduct.getProductName();
                        }
                        existingProduct.setProductQuantity(totalQuantity);
                        productExists = true;
                        break;
                    }
                }

                if (!productExists) {
                    fetchedProduct.setProductQuantity(productRequest.getProductQuantity());
                    productList.add(fetchedProduct);
                }

                cart.setProducts(productList);
                updateCart(cart);

                return "Product added to cart successfully";
            }
        } else {
            return "Product not found with ID: " + productRequest.getProductId();
        }
    }


    public String updateCart(Cart cart) {

        Document doc = convertCartToDocument(cart);
        return cartCollection.replaceOne(Filters.eq("cartId", cart.getCode()), doc).getModifiedCount() > 0 ?
                "Cart updated successfully" : "Cart not found";
    }


    public void clearCart(String cartId) {

        Document query = new Document("cartId", cartId);
        Document update = new Document("$set", new Document("products", new ArrayList<>())); // Clear products

        cartCollection.updateOne(query, update);
    }

    private Document convertCartToDocument(Cart cart) {
        List<Document> productDocs = new ArrayList<>();
        for (ProductDTO productDTO : cart.getProducts()) {
            Document productDoc = new Document("id", productDTO.getId())
                    .append("productId", productDTO.getProductId())
                    .append("productName", productDTO.getProductName())
                    .append("productPrice", productDTO.getProductPrice())
                    .append("productQuantity", productDTO.getProductQuantity())
                    .append("category", new Document("id", productDTO.getCategory().getId())
                            .append("categoryId", productDTO.getCategory().getCategoryId())
                            .append("categoryName", productDTO.getCategory().getCategoryName()));
            productDocs.add(productDoc);
        }

        return new Document()
                .append("cartId", cart.getCode())
                .append("products", productDocs);
    }

    private Cart convertDocumentToCart(Document doc) {
        if (doc == null) {
            return null;
        }

        List<ProductDTO> productDTOS = new ArrayList<>();
        List<Document> productDocs = (List<Document>) doc.get("products");

        if (productDocs == null) {
            return Cart.builder()
                    .code(doc.getString("cartId"))
                    .products(new ArrayList<>())
                    .build();
        }

        for (Document productDoc : productDocs) {
            ProductDTO productDTO = ProductDTO.builder()
                    .id(productDoc.getLong("id"))
                    .productId(productDoc.getString("productId"))
                    .productName(productDoc.getString("productName"))
                    .productPrice(productDoc.getDouble("productPrice"))
                    .productQuantity(productDoc.getLong("productQuantity"))
                    .category(CategoryDTO.builder()
                            .id(productDoc.getEmbedded(List.of("category"), Document.class).getLong("id"))
                            .categoryId(productDoc.getEmbedded(List.of("category"), Document.class).getString("categoryId"))
                            .categoryName(productDoc.getEmbedded(List.of("category"), Document.class).getString("categoryName"))
                            .build())
                    .build();
            productDTOS.add(productDTO);
        }

        return Cart.builder()
                .id(doc.getLong("id") != null ? doc.getLong("id") : 0L)
                .code(doc.getString("cartId"))
                .products(productDTOS)
                .build();
    }
}
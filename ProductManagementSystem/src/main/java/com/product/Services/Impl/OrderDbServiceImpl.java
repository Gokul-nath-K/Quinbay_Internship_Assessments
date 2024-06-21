package com.product.Services.Impl;

import com.product.Dto.OrderItemDTO;
import com.product.Dto.OrdersDTO;
import com.product.Entity.Orders;
import com.product.Entity.Product;
import com.product.Entity.PurchasedProduct;
import com.product.Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDbServiceImpl {



    ProductDbServiceImpl productDbService = new ProductDbServiceImpl();

    DatabaseConnection databaseConnection;

    Connection conn;

    public OrderDbServiceImpl() {

        databaseConnection = new DatabaseConnection();
        conn = databaseConnection.connectPostgresDb();
    }


    public void purchaseProducts(List<OrderItemDTO> products) {
        PreparedStatement orderStmt = null;
        PreparedStatement orderItemStmt = null;

        try {

            conn.setAutoCommit(false);
            // Insert into Orders table
            String orderSQL = "INSERT INTO Orders(total_price, no_of_items, order_status, ordered_on, updated_on) VALUES (?, ?, ?, ?, ?) RETURNING id";
            orderStmt = conn.prepareStatement(orderSQL);
            double totalPrice = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
            long totalItems = products.stream().mapToLong(OrderItemDTO::getQuantity).sum();

            orderStmt.setDouble(1, totalPrice);
            orderStmt.setLong(2, totalItems);
            orderStmt.setString(3, "PENDING");
            orderStmt.setDate(4, new Date(System.currentTimeMillis()));
            orderStmt.setDate(5, new Date(System.currentTimeMillis()));

            ResultSet rs = orderStmt.executeQuery();
            rs.next();
            long orderId = rs.getLong(1);

            // Insert into Ordered_items table
            String orderItemSQL = "INSERT INTO Ordered_items(quantity, price, product_id, order_id) VALUES (?, ?, ?, ?)";
            orderItemStmt = conn.prepareStatement(orderItemSQL);

            for (OrderItemDTO product : products) {
                orderItemStmt.setLong(1, product.getQuantity());
                orderItemStmt.setDouble(2, product.getPrice() * product.getQuantity());
                orderItemStmt.setString(3, product.getId());
                orderItemStmt.setLong(4, orderId);
                orderItemStmt.addBatch();
            }

            orderItemStmt.executeBatch();

            conn.commit(); // Commit transaction
            System.out.println("Order placed successfully!");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(e.getMessage());
        }
    }



    private static final String SELECT_ALL_ORDERS_SQL = "SELECT id, total_price, no_of_items, order_status, ordered_on, updated_on FROM orders";
    private static final String SELECT_PURCHASED_PRODUCTS_BY_ORDER_ID_SQL = "SELECT id, product_id, quantity, price, order_id FROM ordered_items WHERE order_id = ?";

    public List<OrdersDTO> getAllOrdersWithPurchasedProducts() {
        List<OrdersDTO> ordersList = new ArrayList<>();
        try (PreparedStatement orderStmt = conn.prepareStatement(SELECT_ALL_ORDERS_SQL);
             ResultSet orderRs = orderStmt.executeQuery()) {

            while (orderRs.next()) {
                OrdersDTO order = OrdersDTO.builder()
                        .id(orderRs.getLong("id"))
                        .totalPrice(orderRs.getDouble("total_price"))
                        .numberOfItems(orderRs.getLong("no_of_items"))
                        .orderStatus(orderRs.getString("order_status"))
                        .orderedOn(orderRs.getDate("ordered_on"))
                        .updatedOn(orderRs.getDate("updated_on"))
                        .purchasedProducts(getPurchasedProductsByOrderId(orderRs.getLong("id")))
                        .build();

                ordersList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersList;
    }

    private List<PurchasedProduct> getPurchasedProductsByOrderId(long orderId) {
        List<PurchasedProduct> purchasedProducts = new ArrayList<>();
        try (PreparedStatement purchasedProductStmt = conn.prepareStatement(SELECT_PURCHASED_PRODUCTS_BY_ORDER_ID_SQL)) {

            purchasedProductStmt.setLong(1, orderId);
            try (ResultSet purchasedProductRs = purchasedProductStmt.executeQuery()) {
                while (purchasedProductRs.next()) {
                    PurchasedProduct purchasedProduct = PurchasedProduct.builder()
                            .purchaseId(purchasedProductRs.getLong("id"))
                            .productId(purchasedProductRs.getString("product_id"))
                            .productName(productDbService.getProductById(purchasedProductRs.getString("product_id")).getProduct_name())
                            .quantity(purchasedProductRs.getLong("quantity"))
                            .productPrice(purchasedProductRs.getDouble("price"))
                            .totalPrice(purchasedProductRs.getDouble("price") * purchasedProductRs.getLong("quantity"))
                            .build();

                    purchasedProducts.add(purchasedProduct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchasedProducts;
    }


    public void displayAllOrdersWithPurchasedProducts() {
        List<OrdersDTO> ordersList = getAllOrdersWithPurchasedProducts();

        for (OrdersDTO order : ordersList) {
            System.out.println("ID: " + order.getId());
            System.out.println("Total Price: " + order.getTotalPrice());
            System.out.println("Number of Items: " + order.getNumberOfItems());
            System.out.println("Order Status: " + order.getOrderStatus());
            System.out.println("Ordered On: " + order.getOrderedOn());
            System.out.println("Updated On: " + order.getUpdatedOn());

            System.out.println("Purchased Products:");
            for (PurchasedProduct product : order.getPurchasedProducts()) {
                System.out.println("  - Purchase ID: " + product.getPurchaseId());
                System.out.println("    Product ID: " + product.getProductId());
                System.out.println("    Product Name: " + product.getProductName());
                System.out.println("    Quantity: " + product.getQuantity());
                System.out.println("    Product Price: " + product.getProductPrice());
                System.out.println("    Total Price: " + product.getTotalPrice());
                System.out.println();
            }
            System.out.println();
        }
    }


}
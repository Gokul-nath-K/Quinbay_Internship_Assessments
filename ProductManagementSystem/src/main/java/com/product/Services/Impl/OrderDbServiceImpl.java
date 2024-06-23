package com.product.Services.Impl;

import com.product.Dto.OrderItemDTO;
import com.product.Dto.OrdersDTO;
import com.product.Entity.Orders;
import com.product.Entity.Product;
import com.product.Entity.PurchasedProduct;
import com.product.Services.OrderDbServices;
import com.product.Utils.DatabaseConnection;

import javax.swing.text.Document;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDbServiceImpl implements OrderDbServices {



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
            for(OrderItemDTO orderItem : products) {

                productDbService.updateById(orderItem.getId(), "stock",
                        new String(String.valueOf(productDbService.getProductQuantity(orderItem.getId()) - orderItem.getQuantity())), 0);
            }


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

    public List<Orders> getOrderList() {

        List<Orders> ordersList = new ArrayList<>();
        try (PreparedStatement orderStmt = conn.prepareStatement(SELECT_ALL_ORDERS_SQL);
             ResultSet orderRs = orderStmt.executeQuery()) {

            while (orderRs.next()) {
                Orders order = Orders.builder()
                        .id(orderRs.getLong("id"))
                        .totalPrice(orderRs.getDouble("total_price"))
                        .numberOfItems(orderRs.getLong("no_of_items"))
                        .orderStatus(orderRs.getString("order_status"))
                        .orderedOn(orderRs.getDate("ordered_on"))
                        .updatedOn(orderRs.getDate("updated_on"))
                        .build();

                ordersList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersList;
    }
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

    public List<PurchasedProduct> getPurchasedProductsByOrderId(long orderId) {
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

            System.out.println();
            System.out.println("Purchased Products:");

            System.out.printf("%-12s %-10s %-20s %-10s %-15s %-12s%n",
                    "Purchase ID", "Product ID", "Product Name", "Quantity", "Product Price", "Total Price");

            for (PurchasedProduct product : order.getPurchasedProducts()) {
                System.out.printf("%-12s %-10s %-20s %-10d %-15.2f %-12.2f%n",
                        product.getPurchaseId(), product.getProductId(), product.getProductName(),
                        product.getQuantity(), product.getProductPrice(), product.getTotalPrice());
            }
            System.out.println();
        }
    }

    @Override
    public OrdersDTO getOrderById(long orderId) {

        OrdersDTO ordersDTO = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establishing a connection
            // SQL query to fetch data of order by ID
            String sql = "SELECT id, total_price, no_of_items, order_status, ordered_on, updated_on " +
                    "FROM orders " +
                    "WHERE id = ?";

            // Creating a prepared statement
            preparedStatement  = conn.prepareStatement(sql);
            preparedStatement.setLong(1, orderId);

            // Executing the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Processing the ResultSet
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                double totalPrice = resultSet.getDouble("total_price");
                long numberOfItems = resultSet.getLong("no_of_items");
                String orderStatus = resultSet.getString("order_status");
                Date orderedOn = resultSet.getDate("ordered_on");
                Date updatedOn = resultSet.getDate("updated_on");

                // Fetching purchased products associated with the order
                List<PurchasedProduct> purchasedProducts = getPurchasedProductsByOrderId(id);

                // Building OrdersDTO object
                ordersDTO = OrdersDTO.builder()
                        .id(id)
                        .totalPrice(totalPrice)
                        .numberOfItems(numberOfItems)
                        .orderStatus(orderStatus)
                        .orderedOn(orderedOn)
                        .updatedOn(updatedOn)
                        .purchasedProducts(purchasedProducts)
                        .build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Closing PreparedStatement and Connection
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ordersDTO;
    }


    public void displayOrdersWithPurchasedProducts(OrdersDTO order) {

            System.out.println("ID: " + order.getId());
            System.out.println("Total Price: " + order.getTotalPrice());
            System.out.println("Number of Items: " + order.getNumberOfItems());
            System.out.println("Order Status: " + order.getOrderStatus());
            System.out.println("Ordered On: " + order.getOrderedOn());
            System.out.println("Updated On: " + order.getUpdatedOn());

            System.out.println();
            System.out.println("Purchased Products:");

            System.out.printf("%-12s %-10s %-20s %-10s %-15s %-12s%n",
                    "Purchase ID", "Product ID", "Product Name", "Quantity", "Product Price", "Total Price");

            for (PurchasedProduct product : order.getPurchasedProducts()) {
                System.out.printf("%-12s %-10s %-20s %-10d %-15.2f %-12.2f%n",
                        product.getPurchaseId(), product.getProductId(), product.getProductName(),
                        product.getQuantity(), product.getProductPrice(), product.getTotalPrice());
            }
            System.out.println();
    }


}
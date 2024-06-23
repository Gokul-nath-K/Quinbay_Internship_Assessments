package com.product.Services;

import com.product.Dto.OrderItemDTO;
import com.product.Dto.OrdersDTO;
import com.product.Entity.Orders;
import com.product.Entity.Product;
import com.product.Entity.PurchasedProduct;
import com.product.Utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface OrderDbServices {


    public void purchaseProducts(List<OrderItemDTO> products);

    public List<Orders> getOrderList();

    public List<OrdersDTO> getAllOrdersWithPurchasedProducts();

    public List<PurchasedProduct> getPurchasedProductsByOrderId(long orderId);

    public void displayAllOrdersWithPurchasedProducts();

    public void displayOrdersWithPurchasedProducts(OrdersDTO order);

    public OrdersDTO getOrderById(long orderId);
}



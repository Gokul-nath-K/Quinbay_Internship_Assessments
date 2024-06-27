package com.quinbay.orders.service;

import com.quinbay.orders.dto.RedisDTO;
import com.quinbay.orders.dao.entity.Order;

import java.util.List;

public interface OrderService {

    String redisCache(String key, String value);

    String addKey(RedisDTO redisObj);

    List<Order> getAllOrders();

    Order getOrderById(String id);

    void placeOrder(String cartId);
}

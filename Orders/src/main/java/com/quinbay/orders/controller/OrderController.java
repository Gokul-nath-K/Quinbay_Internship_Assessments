package com.quinbay.orders.controller;

import com.quinbay.orders.dto.RedisDTO;
import com.quinbay.orders.dao.entity.Order;
import com.quinbay.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/redis")
    public String addKey(@RequestBody RedisDTO redisObj) {

        return orderService.addKey(redisObj);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/placeOrder/{cartId}")
    public ResponseEntity<String> placeOrder(@PathVariable String cartId) {
        orderService.placeOrder(cartId);
        return ResponseEntity.ok("Order placed successfully");
    }
}

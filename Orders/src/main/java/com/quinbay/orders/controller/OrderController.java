package com.quinbay.orders.controller;

import com.quinbay.orders.model.Order;
import com.quinbay.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/placeOrder/{cartId}")
    public ResponseEntity<String> placeOrder(@PathVariable("cartId") String cartId) {
        try {
            orderService.placeOrder(cartId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to place order: " + e.getMessage());
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable("id") String id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}

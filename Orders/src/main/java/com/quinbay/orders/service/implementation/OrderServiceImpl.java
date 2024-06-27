package com.quinbay.orders.service.implementation;

import com.quinbay.orders.dto.ProductDTO;
import com.quinbay.orders.dto.RedisDTO;
import com.quinbay.orders.exception.OrderException;
import com.quinbay.orders.dao.entity.Cart;
import com.quinbay.orders.dao.entity.Order;
import com.quinbay.orders.dao.repository.OrderRepository;
import com.quinbay.orders.service.CartService;
import com.quinbay.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private RestTemplate restTemplate;

    @Lazy
    @Autowired
    OrderService orderService;

    @Override
    @Cacheable(value = "Orders", key = "#key")
    public String redisCache(String key, String value) { return value; }

    @Override
    public String addKey(RedisDTO redisObj) {

        return orderService.redisCache(redisObj.getKey(), redisObj.getValue());
    }
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(String id) {
        Optional<Order> order = Optional.ofNullable(orderRepository.findByCode(id));
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new OrderException("Order not found with ID: " + id);
        }
    }

    @Override
    public void placeOrder(String cartId) {
        Cart cart = cartService.getCartById(cartId);
        if (cart == null) {
            throw new OrderException("Cart not found with ID: " + cartId);
        }

        Order order = new Order();
        long totalQuantity = cart.getProducts().stream().mapToLong(ProductDTO::getQuantity).sum();
        long numberOfItems = cart.getProducts().size();
        double totalPrice = cart.getProducts().stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();

        order.setTotalQuantity(totalQuantity);
        order.setNumberOfItems(numberOfItems);
        order.setTotalPrice(totalPrice);
        order.setCode("ORD_" + new Random().nextInt(1, 100));
        order.setOrderedOn(new Date());
        order.setProducts(cart.getProducts());

        updateProductQuantities(order.getProducts());
        cartService.clearCart(cartId);

        orderRepository.save(order);
    }

    private void updateProductQuantities(List<ProductDTO> products) {
        for (ProductDTO product : products) {
            ResponseEntity<ProductDTO> response = restTemplate.getForEntity(
                    "http://localhost:8083/product/getByCode?id=" + product.getCode(),
                    ProductDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ProductDTO fetchedProduct = response.getBody();
                long newQuantity = fetchedProduct.getQuantity() - product.getQuantity();
                fetchedProduct.setQuantity(newQuantity);

                restTemplate.put("http://localhost:8083/product/update", fetchedProduct);
            } else {
                throw new OrderException("Failed to fetch product details for ID: " + product.getCode());
            }
        }
    }
}

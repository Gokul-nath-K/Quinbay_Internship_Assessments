package com.quinbay.orders.controller;

import com.quinbay.orders.model.Cart;
import com.quinbay.orders.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/getAll")
    public List<Cart> getAllCarts() {
        return cartService.getAllCarts();
    }

    @GetMapping("/getCartById/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable("cartId") String cartId) {
        Cart cart = cartService.getCartById(cartId);
        return cart != null ? ResponseEntity.ok(cart) : ResponseEntity.notFound().build();
    }

    @PostMapping("/createCart")
    public ResponseEntity<String> createCart() {
        String result = cartService.createCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/addProductToCart/{cartId}")
    public ResponseEntity<String> addProductToCart(@PathVariable("cartId") String cartId,
                                                   @RequestBody ProductRequest productRequest) {
        String result = cartService.addProductToCart(cartId, productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/updateCart")
    public ResponseEntity<String> updateCart(@RequestBody Cart cart) {
        String result = cartService.updateCart(cart);
        return result.equals("Cart updated successfully") ?
                ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/clear/{cartId}")
    public String clearCart(@PathVariable String cartId) {
        try {
            cartService.clearCart(cartId);
            return "Cart cleared successfully";
        } catch (Exception e) {
            return "Failed to clear cart: " + e.getMessage();
        }
    }

}

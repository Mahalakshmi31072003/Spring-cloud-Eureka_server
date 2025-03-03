package com.maha.orderserver.controller;

import com.maha.orderserver.dto.OrderResponseDTO;
import com.maha.orderserver.dto.ProductDTO;
import com.maha.orderserver.entity.Order;
import com.maha.orderserver.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    //create a method to place order
    @PostMapping("/placeOrder")
    public Mono<ResponseEntity<OrderResponseDTO>> placeOrder(@RequestBody Order order) {
        return webClientBuilder.build().get()
                .uri("http://localhost:8081/products/" + order.getProductId())
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .map(productDTO -> {
                    // Debugging log
                    System.out.println("Fetched Product: " + productDTO.getName() + ", Price: " + productDTO.getPrice());

                    // Set product details in response DTO
                    OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
                    orderResponseDTO.setProductId(order.getProductId());
                    orderResponseDTO.setQuantity(order.getQuantity());
                    orderResponseDTO.setProductName(productDTO.getName());
                    orderResponseDTO.setProductPrice(productDTO.getPrice());
                    orderResponseDTO.setTotalPrice(order.getQuantity() * productDTO.getPrice());

                    // Update order entity before saving
                    order.setTotalPrice(orderResponseDTO.getTotalPrice());

                    // Save updated order
                    orderRepository.save(order);

                    // Set the order ID after saving
                    orderResponseDTO.setOrderId(order.getId());

                    return ResponseEntity.ok(orderResponseDTO);
                })
                .onErrorResume(error -> {
                    System.err.println("Error fetching product: " + error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(new OrderResponseDTO()));
                });
    }
    //get all orders

    @GetMapping
    public List<Order> getAllOrders()
    {
        return orderRepository.findAll();
    }

}


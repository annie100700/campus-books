package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.CreateOrderRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.model.UserOrder;
import com.annie.campus_books.service.BookService;
import com.annie.campus_books.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RequestMapping("/api.campus-books.com/orders")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @PostMapping("/")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal User buyer,
                                         @RequestBody CreateOrderRequest createOrderRequest) {
        try {
            UUID bookId = UUID.fromString(createOrderRequest.bookId());
            Book book = bookService.getBookById(bookId);
            if (book != null) {
                String sellerAccount = book.getSeller().getAccount();
                if (buyer.getAccount().equals(sellerAccount)) {
                    return new ResponseEntity<>("Can not buy your own book", HttpStatus.BAD_REQUEST);
                } else if (book.getStatus().equals(Book.Status.SOLD_OUT)) {
                    return new ResponseEntity<>("This book already sold out", HttpStatus.BAD_REQUEST);
                }
                UserOrder userOrder = orderService.createOrder(book, buyer);
                return new ResponseEntity<>(userOrder, HttpStatus.CREATED);
            }
            return new ResponseEntity<>("Book id doesn't exist ", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Book id is not legal", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<UserOrder>> getAllOrders() {
        List<UserOrder> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        try {
            UUID orderId = UUID.fromString(id);
            UserOrder order = orderService.getOrderById(orderId);
            if (order != null) {
                return new ResponseEntity<>(order, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("id is not legal", HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status/complete")
    public ResponseEntity<String> completeOrderById(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            UUID orderId = UUID.fromString(id);
            UserOrder order = orderService.getOrderById(orderId);
            if (order != null) {
                if (user.getAccount().equals(order.getBuyer().getAccount()) || user.isAdmin()) {
                    orderService.completeOrder(order);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("id is not legal", HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status/cancel")
    public ResponseEntity<String> cancelOrderById(@AuthenticationPrincipal User user, @PathVariable String id) {
        try {
            UUID orderId = UUID.fromString(id);
            UserOrder order = orderService.getOrderById(orderId);
            if (order != null) {
                if (user.getAccount().equals(order.getBuyer().getAccount()) ||
                        user.getAccount().equals(order.getSeller().getAccount()) ||
                        user.isAdmin()) {
                    orderService.cancelOrder(order);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("id is not legal", HttpStatus.BAD_REQUEST);
        }
    }

}

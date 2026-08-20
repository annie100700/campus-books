package com.annie.campus_books.controller;

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
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal User buyer, @RequestPart Integer id) {
        Book book = bookService.getBookById(id);
        String sellerAccount = book.getSeller().getAccount();
        if (buyer.getAccount().equals(sellerAccount)) {
            return new ResponseEntity<>("Can not buy your own book", HttpStatus.BAD_REQUEST);
        } else if (book.getStatus().equals(Book.Status.SOLD_OUT)) {
            return new ResponseEntity<>("This book already sold out", HttpStatus.BAD_REQUEST);
        }
        UserOrder userOrder = orderService.createOrder(book, buyer);
        return new ResponseEntity<>(userOrder, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserOrder>> getAllOrders() {
        List<UserOrder> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOrder> getOrderById(@PathVariable UUID id) {
        UserOrder order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/complete")
    public ResponseEntity<String> completeOrderById(@PathVariable UUID id) {
        UserOrder order = orderService.getOrderById(id);
        if (order != null) {
            orderService.completeOrder(order);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}/status/cancel")
    public ResponseEntity<String> cancelOrderById(@PathVariable UUID id) {
        UserOrder order = orderService.getOrderById(id);
        if (order != null) {
            orderService.cancelOrder(order);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable UUID id) {
        UserOrder order = orderService.getOrderById(id);
        if (order != null) {
            orderService.deleteOrderById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

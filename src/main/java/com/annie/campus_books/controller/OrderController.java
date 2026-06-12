package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.OrderRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.model.UserOrder;
import com.annie.campus_books.service.BookService;
import com.annie.campus_books.service.OrderService;
import com.annie.campus_books.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@CrossOrigin
@RequestMapping("/campus-books")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestPart UserOrder order,
                                         @RequestPart OrderRequest orderRequest) {
        Book book = bookService.getBookById(orderRequest.getBookId());
        User buyer=userService.getUserByAccount(orderRequest.getBuyerAccount());
        if(Objects.equals(buyer.getAccount(), book.getSeller().getAccount())){
            return new ResponseEntity<>("Can not buy your own book",HttpStatus.BAD_REQUEST);
        } else if (book.getStatus().contains(Book.Status.SOLD_OUT)) {
            return  new ResponseEntity<>("This book already sold out",HttpStatus.BAD_REQUEST);
        }else
            orderService.createOrder(order,book,buyer);
            return new ResponseEntity<>("Order created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<UserOrder>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(),HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<UserOrder> getOrderById(@PathVariable UUID orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId),HttpStatus.OK);
    }

    @GetMapping("/{account}/order/purchases")
    public ResponseEntity<List<UserOrder>> getUserPurchases(@PathVariable String account) {
        return new ResponseEntity<>(orderService.getUserPurchases(account),HttpStatus.OK);
    }

    @GetMapping("/{account}/order/sales")
    public ResponseEntity<List<UserOrder>> getUserSales(@PathVariable String account) {
        return new ResponseEntity<>(orderService.getUserSales(account),HttpStatus.OK);
    }

    @PatchMapping("/order/{orderId}/completed")
    public ResponseEntity<String> completeOrderById(@PathVariable UUID orderId) {
        UserOrder order= orderService.getOrderById(orderId);
        if (order!=null){
            orderService.completeOrder(order);
            return new ResponseEntity<>("Order completed successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Order not found",HttpStatus.NOT_FOUND);
    }

//    @PutMapping("/order/{orderId}")
//    public ResponseEntity<String> updateOrder(@PathVariable UUID orderId,
//                                              @RequestPart UserOrder order,
//                                              @RequestPart Book book,
//                                              @RequestPart User buyer){
//        return new ResponseEntity<>("Order updated successfully", HttpStatus.OK);
//    }

    @PatchMapping("/order/{orderId}/cancelled")
    public ResponseEntity<String> cancelOrderById(@PathVariable UUID orderId) {
        UserOrder order = orderService.getOrderById(orderId);
        if(order!=null){
            orderService.cancelOrder(order);
            return new ResponseEntity<>("Order cancelled successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Order not found",HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable UUID orderId) {
        UserOrder order = orderService.getOrderById(orderId);
        if(order!=null){
            orderService.deleteOrderById(orderId);
            return new ResponseEntity<>("Order deleted successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Order not found",HttpStatus.NOT_FOUND);
    }

}

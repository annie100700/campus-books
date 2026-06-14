package com.annie.campus_books.service;

import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.model.UserOrder;
import com.annie.campus_books.repository.BookRepo;
import com.annie.campus_books.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private BookRepo bookRepo;

    @Transactional
    public UserOrder createOrder(UserOrder order, Book book, User buyer) {
        order.setBook(book);
        order.setBookName(book.getName());
        order.setPrice(book.getSellingPrice());

        order.setSeller(book.getSeller());
        order.setBuyer(buyer);

        order.setCreatedDate(LocalDateTime.now());
        List<UserOrder.Status> orderStatus = new ArrayList<>();
        orderStatus.add(UserOrder.Status.IN_PROGRESS);
        order.setStatus(orderStatus);

        List<Book.Status> bookStatus = new ArrayList<>();
        bookStatus.add(Book.Status.SOLD_OUT);
        book.setStatus(bookStatus);
        bookRepo.save(book);
        return orderRepo.save(order);
    }

    public List<UserOrder> getAllOrders() {
        return orderRepo.findAll();
    }

    public UserOrder getOrderById(UUID orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    public List<UserOrder> getUserPurchases(String account) {
        return orderRepo.findAllByBuyer(account);
    }

    public List<UserOrder> getUserSales(String account) {
        return orderRepo.findAllBySeller(account);
    }

    public void completeOrder(UserOrder order) {
        List<UserOrder.Status> status=new ArrayList<>();
        status.add(UserOrder.Status.COMPLETED);
        order.setStatus(status);
        orderRepo.save(order);
    }

    @Transactional
    public void cancelOrder(UserOrder order,Book book) {
        List<UserOrder.Status> orderStatus=new ArrayList<>();
        orderStatus.add(UserOrder.Status.CANCELED);
        order.setStatus(orderStatus);
        orderRepo.save(order);

        List<Book.Status> bookStatus=new ArrayList<>();
        bookStatus.add(Book.Status.FOR_SALE);
        book.setStatus(bookStatus);
        bookRepo.save(book);

    }

    public void deleteOrderById(UUID orderId) {
        orderRepo.deleteById(orderId);
    }

}

package com.annie.campus_books.service;

import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.model.UserOrder;
import com.annie.campus_books.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepo repo;


    public void createOrder(UserOrder order, Book book, User buyer) {
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
        repo.save(order);
    }

    public List<UserOrder> getAllOrders() {
        return repo.findAll();
    }

    public UserOrder getOrderById(UUID orderId) {
        return repo.findById(orderId).orElse(null);
    }

    public List<UserOrder> getUserPurchases(String account) {
        return repo.findAllByBuyer(account);
    }

    public List<UserOrder> getUserSales(String account) {
        return repo.findAllBySeller(account);
    }

    public void completeOrder(UserOrder order) {
        List<UserOrder.Status> status=new ArrayList<>();
        status.add(UserOrder.Status.COMPLETED);
        order.setStatus(status);
        repo.save(order);
    }

    public void cancelOrder(UserOrder order) {
        List<UserOrder.Status> status=new ArrayList<>();
        status.add(UserOrder.Status.CANCELED);
        order.setStatus(status);
        repo.save(order);
    }

    public void deleteOrderById(UUID orderId) {
        repo.deleteById(orderId);
    }

}

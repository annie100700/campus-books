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
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private BookRepo bookRepo;

    @Transactional
    public UserOrder createOrder(Book book, User buyer) {
        UserOrder order = new UserOrder();
        order.setBook(book);
        order.setBookName(book.getName());
        order.setPrice(book.getSellingPrice());

        order.setSeller(book.getSeller());
        order.setBuyer(buyer);

        order.setCreatedDate(LocalDateTime.now());
        order.setStatus(UserOrder.Status.IN_PROGRESS);

        book.setStatus(Book.Status.SOLD_OUT);
        bookRepo.save(book);
        return orderRepo.save(order);
    }

    public List<UserOrder> getAllOrders() {
        return orderRepo.findAll();
    }

    public UserOrder getOrderById(UUID orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    public List<UserOrder> getPurchasesByUser(String account) {
        return orderRepo.findAllByBuyer(account);
    }

    public List<UserOrder> getUserSales(String account) {
    public List<UserOrder> getPurchasesByUserAndStatus(String account, UserOrder.Status status) {
        return orderRepo.findAllByBuyerAndStatus(account,status);
    }

        return orderRepo.findAllBySeller(account);
    }

    public void completeOrder(UserOrder order) {
        order.setStatus(UserOrder.Status.COMPLETED);
        orderRepo.save(order);
    }

    @Transactional
    public void cancelOrder(UserOrder order) {
        order.setStatus(UserOrder.Status.CANCELED);

        Book book=order.getBook();
        book.setStatus(Book.Status.FOR_SALE);

        orderRepo.save(order);
        bookRepo.save(book);
    }

    public void deleteOrderById(UUID orderId) {
        orderRepo.deleteById(orderId);
    }

}

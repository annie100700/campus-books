package com.annie.campus_books.repository;

import com.annie.campus_books.model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<UserOrder, UUID> {

    @Query("SELECT o FROM UserOrder o " +
            "WHERE o.buyer.account=:account")
    List<UserOrder> findAllByBuyer(String account);

    @Query("SELECT o FROM UserOrder o " +
            "WHERE o.seller.account=:account")
    List<UserOrder> findAllBySeller(String account);

}

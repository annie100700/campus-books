package com.annie.campus_books.repository;

import com.annie.campus_books.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);

}

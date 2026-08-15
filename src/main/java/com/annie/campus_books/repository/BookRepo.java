package com.annie.campus_books.repository;

import com.annie.campus_books.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b WHERE "
            + "b.seller.account=:account")
    List<Book> findAllByUser(String account);

    @Query("SELECT b FROM Book b WHERE "
            + "b.seller.account=:account AND "
            + "b.status=:status")
    List<Book> findAllByUserAndStatus(String account, Book.Status status);

    @Query("SELECT b FROM Book b WHERE "
            + "LOWER(b.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "
            + "LOWER(b.author) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "
            + "LOWER(b.publisher) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    List<Book> searchBooks(String keyword);

    @SuppressWarnings("NullableProblems")
    @EntityGraph(attributePaths = "bookCondition")
    List<Book> findAll();

    @EntityGraph(attributePaths = "bookCondition")
    Optional<Book> findById(int bookId);

}

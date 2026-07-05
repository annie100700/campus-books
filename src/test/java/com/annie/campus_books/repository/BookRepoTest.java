package com.annie.campus_books.repository;

import com.annie.campus_books.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class BookRepoTest {

    @Autowired
    private BookRepo bookRepo;

    @BeforeEach
    public void setup(){
        bookRepo.deleteAll();

        Book book=new Book();
        book.setName("test-name");
        book.setAuthor("test-author");
        book.setPublisher("test-publisher");
        bookRepo.save(book);

    }

    @Test
    public void searchBooks_shouldIgnoreUpperCase(){
        String keyword="Test";

        List<Book> books=bookRepo.searchBooks(keyword);

        assertEquals(1,books.size());
        assertEquals("test-name",books.getFirst().getName());
        assertEquals("test-author",books.getFirst().getAuthor());
        assertEquals("test-publisher",books.getFirst().getPublisher());
    }

    @Test
    public void searchBooks_shouldReturnEmptyList(){
        String keyword="toast";

        List<Book> books=bookRepo.searchBooks(keyword);

        assertEquals(0,books.size());
    }

}

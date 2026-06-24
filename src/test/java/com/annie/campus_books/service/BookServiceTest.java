package com.annie.campus_books.service;

import com.annie.campus_books.model.Book;
import com.annie.campus_books.repository.BookRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    @Test
    void getBookById_whenBookExists_shouldReturnBook() {

        Book book = new Book();
        int id=1;
        book.setId(id);
        book.setName("書名");
        when(bookRepo.findById(id)).thenReturn(Optional.of(book));

        Book foundBook= bookService.getBookById(1);

        assertNotNull(foundBook);
        //assertTrue(foundBook.isPresent());
        assertEquals("書名",foundBook.getName());
        verify(bookRepo,times(1)).findById(id);

    }

}

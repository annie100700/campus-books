package com.annie.campus_books.service;

import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.BookRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBookById_whenBookExist_shouldReturnBook() {
        Book book = new Book();
        int id=1;
        book.setId(id);
        when(bookRepo.findById(id)).thenReturn(Optional.of(book));

        Book foundBook= bookService.getBookById(id);

        assertNotNull(foundBook);
        verify(bookRepo).findById(id);
    }

    @Test
    void getBookById_whenBookNotExist_shouldReturnNull() {
        int id=1;
        when(bookRepo.findById(id)).thenReturn(Optional.empty());

        Book foundBook= bookService.getBookById(id);

        assertNull(foundBook);
    }

    @Test
    void addBook_shouldSetBookAndSave() throws IOException {
        Book book = new Book();
        String imageName = "book.jpg";
        String imageType = "image/jpeg";
        byte[] imageData = "test-image".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile(imageName,imageName,imageType,imageData);
        User seller = new User();
        when(bookRepo.save(any(Book.class))).then(returnsFirstArg());

        Book newBook=bookService.addBook(book,imageFile,seller);

        assertEquals(seller,newBook.getSeller());
        assertEquals(imageName,newBook.getImageName());
        assertEquals(imageType,newBook.getImageType());
        assertEquals(imageData,newBook.getImageData());
        assertTrue(newBook.getStatus().contains(Book.Status.FOR_SALE));
        verify(bookRepo).save(book);
    }

}

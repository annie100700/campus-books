package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.BookInfoRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.BookCondition;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.BookRepo;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBookById_whenBookExist_shouldReturnBook() {
        Book book = new Book();
        UUID id = UUID.randomUUID();
        book.setId(id);
        when(bookRepo.findByUuid(id)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(id);

        assertNotNull(foundBook);
        verify(bookRepo).findByUuid(id);
    }

    @Test
    void getBookById_whenBookNotExist_shouldReturnNull() {
        UUID id = UUID.randomUUID();
        when(bookRepo.findByUuid(id)).thenReturn(Optional.empty());

        Book foundBook = bookService.getBookById(id);

        assertNull(foundBook);
    }

    @Test
    void addBook_shouldSetBookAndSave() throws IOException {
        Book book = new Book();
        String imageName = "book.jpg";
        String imageType = "image/jpeg";
        byte[] imageData = "test-image".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile(imageName, imageName, imageType, imageData);
        User seller = new User();
        when(bookRepo.save(any(Book.class))).then(returnsFirstArg());

        Book newBook = bookService.addBook(book, imageFile, seller);

        assertEquals(seller, newBook.getSeller());
        assertEquals(imageName, newBook.getImageName());
        assertEquals(imageType, newBook.getImageType());
        assertEquals(imageData, newBook.getImageData());
        assertTrue(newBook.getStatus().contains(Book.Status.FOR_SALE));
        verify(bookRepo).save(book);
    }

    @Test
    void updateBook_withImage_shouldUpdateBookInfo() throws IOException {
        Book book = new Book();
        String newName = "newName";
        String newAuthor = "newAuthor";
        String newPublisher = "newPublisher";
        BookCondition bookCondition = new BookCondition();
        BigDecimal newListPrice = new BigDecimal("100");
        BigDecimal newSellingPrice = new BigDecimal("120");
        BookInfoRequest bookInfoRequest = getBookInfoRequest(newName, newAuthor, newPublisher, bookCondition, newListPrice, newSellingPrice);
        String imageName = "book.jpg";
        String imageType = "image/jpeg";
        byte[] imageData = "test-image".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile(imageName, imageName, imageType, imageData);

        bookService.updateBook(book, bookInfoRequest, imageFile);

        assertEquals(newName, book.getName());
        assertEquals(newAuthor, book.getAuthor());
        assertEquals(newPublisher, book.getPublisher());
        assertEquals(bookCondition, book.getBookCondition());
        assertEquals(newListPrice, book.getListPrice());
        assertEquals(newSellingPrice, book.getSellingPrice());
        assertEquals(imageName, book.getImageName());
        assertEquals(imageType, book.getImageType());
        assertEquals(imageData, book.getImageData());
        verify(bookRepo).save(book);
    }

    private static @NonNull BookInfoRequest getBookInfoRequest(String newName, String newAuthor, String newPublisher, BookCondition bookCondition, BigDecimal newListPrice, BigDecimal newSellingPrice) {
        BookInfoRequest bookInfoRequest = new BookInfoRequest();
        bookInfoRequest.setName(newName);
        bookInfoRequest.setAuthor(newAuthor);
        bookInfoRequest.setPublisher(newPublisher);
        bookInfoRequest.setBookCondition(bookCondition);
        bookInfoRequest.setListPrice(newListPrice);
        bookInfoRequest.setSellingPrice(newSellingPrice);
        return bookInfoRequest;
    }

    @Test
    void updateBook_withOutImage_shouldKeepOldImage() throws IOException {
        Book book = new Book();
        String newName = "newName";
        String newAuthor = "newAuthor";
        String newPublisher = "newPublisher";
        BookCondition bookCondition = new BookCondition();
        BigDecimal newListPrice = new BigDecimal("100");
        BigDecimal newSellingPrice = new BigDecimal("120");
        BookInfoRequest bookInfoRequest = getBookInfoRequest(newName, newAuthor, newPublisher, bookCondition, newListPrice, newSellingPrice);

        bookService.updateBook(book, bookInfoRequest, null);

        assertEquals(newName, book.getName());
        assertEquals(newAuthor, book.getAuthor());
        assertEquals(newPublisher, book.getPublisher());
        assertEquals(bookCondition, book.getBookCondition());
        assertEquals(newListPrice, book.getListPrice());
        assertEquals(newSellingPrice, book.getSellingPrice());
        verify(bookRepo).save(book);
    }

    @Test
    void searchBooks_shouldReturnMatchedBooks() {
        List<Book> books = List.of(new Book());
        String keyword = "keyword";
        when(bookRepo.searchBooks(keyword)).thenReturn(books);

        List<Book> matchedBooks = bookService.searchBooks(keyword);

        assertEquals(books.size(), matchedBooks.size());

        verify(bookRepo).searchBooks(keyword);
    }
}

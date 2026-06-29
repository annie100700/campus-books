package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.BookInfoRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.service.BookService;
import com.annie.campus_books.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserService userService;

    @Test
    void addBook_shouldReturnAddedBookWith201() throws Exception {
        Book book = new Book();
        MockMultipartFile bookPart=new MockMultipartFile("book","","application/json",objectMapper.writeValueAsBytes(book));
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "book.jpg", "image/jpeg", "test-image".getBytes());
        String sellerAccount="sellerAccount";
        MockMultipartFile sellerPart=new MockMultipartFile("sellerAccount","","text/plain",sellerAccount.getBytes());
        User seller = new User();
        when(userService.getUserByAccount(sellerAccount)).thenReturn(seller);
        when(bookService.addBook(any(Book.class),any(MultipartFile.class),any(User.class))).thenReturn(book);

        mockMvc.perform(multipart("/api.campus-books.com/books/")
                        .file(imageFile).file(bookPart).file(sellerPart))
                .andExpect(status().isCreated());

        verify(bookService).addBook(any(Book.class),eq(imageFile),eq(seller));
    }

    @Test
    void getAllBooks_shouldReturnAllBooksWith200() throws Exception {
        List<Book> books = List.of(new Book());
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api.campus-books.com/books/"))
        .andExpect(status().isOk());

        verify(bookService).getAllBooks();
    }

    @Test
    void getBookById_bookFound_shouldReturnBookWith200() throws Exception {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        when(bookService.getBookById(bookId)).thenReturn(book);

        mockMvc.perform(get("/api.campus-books.com/books/"+bookId))
        .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId));

        verify(bookService).getBookById(bookId);
    }

    @Test
    void getBookById_bookNotFound_shouldReturn404() throws Exception {
        int bookId = 1;
        when(bookService.getBookById(bookId)).thenReturn(null);

        mockMvc.perform(get("/api.campus-books.com/books/"+bookId))
                .andExpect(status().isNotFound());

        verify(bookService).getBookById(bookId);
    }

    @Test
    void updateBook_shouldReturn200() throws Exception {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        BookInfoRequest bookInfoRequest = new BookInfoRequest();
        MockMultipartFile idPart=new MockMultipartFile("bookId", "", "text/plain", String.valueOf(bookId).getBytes());
        MockMultipartFile bookInfoPart=new MockMultipartFile("bookInfoRequest", "", "application/json", objectMapper.writeValueAsBytes(bookInfoRequest));
        String originalFileName = "book.jpg";
        byte[] imageData = "test-image".getBytes();
        MockMultipartFile imagePart=new MockMultipartFile("imageFile", originalFileName, "image/jpeg", imageData);
        when(bookService.getBookById(bookId)).thenReturn(book);

        mockMvc.perform(multipart("/api.campus-books.com/books/"+bookId)
                .file(idPart).file(bookInfoPart).file(imagePart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;}))
                .andExpect(status().isOk());

        verify(bookService).updateBook(book,bookInfoRequest,imagePart);
    }

    @Test
    void deleteBook_bookFound_shouldReturn200() throws Exception {
        int bookId = 1;
        Book book = new Book();
        book.setId(bookId);
        when(bookService.getBookById(bookId)).thenReturn(book);

        mockMvc.perform(delete("/api.campus-books.com/books/"+bookId))
                .andExpect(status().isOk());

        verify(bookService).deleteBookById(bookId);
    }

}

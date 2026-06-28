package com.annie.campus_books.controller;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    void addBook_shouldReturnAddedBookAnd201() throws Exception {
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

}

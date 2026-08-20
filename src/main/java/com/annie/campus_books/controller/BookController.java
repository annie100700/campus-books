package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.AddBookRequest;
import com.annie.campus_books.dto.request.UpdateBookRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.User;
import com.annie.campus_books.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RequestMapping("/api.campus-books.com/books")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/")
    public ResponseEntity<?> addBook(@AuthenticationPrincipal User user,
                                     @RequestPart AddBookRequest addBookRequest, @RequestPart MultipartFile imageFile) {
        try {
            Book book = bookService.addBook(addBookRequest, imageFile, user);
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        Book book = bookService.getBookById(id);
        if (book != null)
            return new ResponseEntity<>(book, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getBookImageById(@PathVariable int id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            byte[] imageFile = book.getImageData();
            return new ResponseEntity<>(imageFile, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBook(@AuthenticationPrincipal User user, @PathVariable int id,
                                        @RequestPart UpdateBookRequest updateBookRequest,
                                        @RequestPart(required = false) MultipartFile imageFile) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            if (user.equals(book.getSeller()) || user.getRole().equals(User.Role.ADMIN)) {
                try {
                    Book updatedBook = bookService.updateBook(book, updateBookRequest, imageFile);
                    return new ResponseEntity<>(updatedBook, HttpStatus.OK);
                } catch (IOException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@AuthenticationPrincipal User user, @PathVariable int id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            if (user.equals(book.getSeller()) || user.getRole().equals(User.Role.ADMIN)) {
                bookService.deleteBook(book);
                return new ResponseEntity<>("deleted", HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("Deleted book doesn't exist", HttpStatus.NOT_FOUND);
        }
    }

}

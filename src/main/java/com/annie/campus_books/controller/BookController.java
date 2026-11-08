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
import java.util.UUID;

@CrossOrigin
@RequestMapping("/books")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/")
    public ResponseEntity<?> addBook(@AuthenticationPrincipal User user,
                                     @RequestPart AddBookRequest addBookRequest, @RequestPart(required = false) MultipartFile imageFile) {
        if (addBookRequest.isNullOrBlank()) {
            return new ResponseEntity<>("Name, author and publisher is required field", HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<Book> getBookById(@PathVariable UUID id) {
        Book book = bookService.getBookById(id);
        if (book != null)
            return new ResponseEntity<>(book, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getBookImageById(@PathVariable UUID id) {
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
    public ResponseEntity<?> updateBook(@AuthenticationPrincipal User user, @PathVariable UUID id,
                                        @RequestPart(required = false) UpdateBookRequest updateBookRequest,
                                        @RequestPart(required = false) MultipartFile imageFile) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            String sellerAccount = book.getSeller().getAccount();
            if (user.getAccount().equals(sellerAccount) || user.isAdmin()) {
                if (updateBookRequest == null && imageFile == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
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
    public ResponseEntity<String> deleteBookById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            String sellerAccount = book.getSeller().getAccount();
            if (user.getAccount().equals(sellerAccount) || user.isAdmin()) {
                bookService.deleteBook(book);
                return new ResponseEntity<>("deleted", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteBookImageById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            String sellerAccount = book.getSeller().getAccount();
            if (user.getAccount().equals(sellerAccount) || user.isAdmin()) {
                bookService.deleteBookImage(book);
                return new ResponseEntity<>("deleted", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

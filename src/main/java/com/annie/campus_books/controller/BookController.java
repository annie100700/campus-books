package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.BookInfoRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RequestMapping("/api.campus-books.com/books")
@RestController
public class BookController {

    @Autowired
    private BookService service;

    @GetMapping("/")
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(service.getAllBooks(),HttpStatus.OK);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable int bookId) {
        Book book=service.getBookById(bookId);
        if(book!=null)
            return new ResponseEntity<>(book, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<?> addBook(@RequestPart Book book,@RequestPart MultipartFile imageFile) {
        try {
            Book addedBook = service.addBook(book,imageFile);
            return  new ResponseEntity<>(addedBook, HttpStatus.CREATED);
        } catch (Exception e) {
            return  new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{bookId}/image")
    public ResponseEntity<byte[]> getBookImageById(@PathVariable int bookId) {
        Book book=service.getBookById(bookId);
        byte[] imageFile=book.getImageData();
        return ResponseEntity.ok().body(imageFile);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable int bookId,
                                             @RequestPart BookInfoRequest bookInfoRequest,
                                             @RequestPart MultipartFile imageFile) {
        Book book= service.getBookById(bookId);
        try {
            service.updateBook(book,bookInfoRequest,imageFile);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Updated", HttpStatus.OK);

    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBookById(@PathVariable int bookId) {
        Book book=service.getBookById(bookId);
        if(book!=null) {
            service.deleteBookById(bookId);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }else
            return new ResponseEntity<>("Deleted book doesn't exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> books=service.searchBooks(keyword);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

}

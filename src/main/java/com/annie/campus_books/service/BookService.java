package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.AddBookRequest;
import com.annie.campus_books.dto.request.UpdateBookRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.BookCondition;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.BookRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepo repo;

    public Book addBook(AddBookRequest addBookRequest, MultipartFile imageFile, User seller) throws IOException {
        Book book = new Book();
        book.setName(addBookRequest.name());
        book.setAuthor(addBookRequest.author());
        book.setPublisher(addBookRequest.publisher());

        BookCondition bookCondition = getBookCondition(addBookRequest);
        book.setBookCondition(bookCondition);

        book.setSeller(seller);
        book.setListPrice(addBookRequest.listPrice());
        book.setSellingPrice(addBookRequest.sellingPrice());
        book.setStatus(Book.Status.FOR_SALE);
        if (imageFile != null) {
            book.setImageName(imageFile.getOriginalFilename());
            book.setImageType(imageFile.getContentType());
            book.setImageData(imageFile.getBytes());
        }
        return repo.save(book);
    }

    private BookCondition getBookCondition(AddBookRequest addBookRequest) {
        BookCondition bookCondition = new BookCondition();
        bookCondition.setHighlight(addBookRequest.highlight());
        bookCondition.setNotes(addBookRequest.notes());
        bookCondition.setStain(addBookRequest.stain());
        bookCondition.setFrontCoverMissing(addBookRequest.frontCoverMissing());
        bookCondition.setBackCoverMissing(addBookRequest.backCoverMissing());
        bookCondition.setSplitVolume(addBookRequest.splitVolume());
        bookCondition.setOther(addBookRequest.other());
        return bookCondition;
    }

    public List<Book> getAllBooks() {
        return repo.findAll();
    }

    public Book getBookById(UUID id) {
        return repo.findByUuid(id).orElse(null);
    }

    public List<Book> getBooksByUser(String account) {
        return repo.findAllByUser(account);
    }

    public List<Book> getBooksByUserAndStatus(String account, Book.Status status) {
        return repo.findAllByUserAndStatus(account, status);
    }

    public List<Book> searchBooks(String keyword) {
        return repo.searchBooks(keyword);
    }

    public Book updateBook(Book book, UpdateBookRequest updateBookRequest,
                           MultipartFile imageFile) throws IOException {
        if (updateBookRequest != null) {
            if (updateBookRequest.name() != null) {
                book.setName(updateBookRequest.name());
            }
            if (updateBookRequest.author() != null) {
                book.setAuthor(updateBookRequest.author());
            }
            if (updateBookRequest.publisher() != null) {
                book.setPublisher(updateBookRequest.publisher());
            }
            if (updateBookRequest.version() != null) {
                book.setVersion(updateBookRequest.version());
            }

            BookCondition bookCondition = updateBookCondition(book, updateBookRequest);
            book.setBookCondition(bookCondition);

            if (updateBookRequest.listPrice() != null) {
                book.setListPrice(updateBookRequest.listPrice());
            }
            if (updateBookRequest.sellingPrice() != null) {
                book.setSellingPrice(updateBookRequest.sellingPrice());
            }
        }

        if (imageFile != null) {
            book.setImageName(imageFile.getOriginalFilename());
            book.setImageType(imageFile.getContentType());
            book.setImageData(imageFile.getBytes());
        }
        repo.save(book);
        return book;
    }

    private BookCondition updateBookCondition(Book book, UpdateBookRequest updateBookRequest) {
        BookCondition bookCondition = book.getBookCondition();
        if (updateBookRequest.highlight() != null) {
            bookCondition.setHighlight(updateBookRequest.highlight());
        }
        if (updateBookRequest.notes() != null) {
            bookCondition.setNotes(updateBookRequest.notes());
        }
        if (updateBookRequest.stain() != null) {
            bookCondition.setStain(updateBookRequest.stain());
        }
        if (updateBookRequest.frontCoverMissing() != null) {
            bookCondition.setFrontCoverMissing(updateBookRequest.frontCoverMissing());
        }
        if (updateBookRequest.backCoverMissing() != null) {
            bookCondition.setBackCoverMissing(updateBookRequest.backCoverMissing());
        }
        if (updateBookRequest.splitVolume() != null) {
            bookCondition.setSplitVolume(updateBookRequest.splitVolume());
        }
        if (updateBookRequest.other() != null) {
            bookCondition.setOther(updateBookRequest.other());
        }
        return bookCondition;
    }

    public void deleteBook(Book book) {
        book.setStatus(Book.Status.REMOVED);
        repo.save(book);
    }

}

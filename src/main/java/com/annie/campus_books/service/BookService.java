package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.BookInfoRequest;
import com.annie.campus_books.dto.request.AddBookRequest;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.BookCondition;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.BookRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@Service
public class BookService {

    @Autowired
    private BookRepo repo;


    public List<Book> getAllBooks() {
        return repo.findAll();
    }

    public Book getBookById(int bookId) {
        return repo.findById(bookId).orElse(null);
    }

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
        book.setImageName(imageFile.getOriginalFilename());
        book.setImageType(imageFile.getContentType());
        book.setImageData(imageFile.getBytes());
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


    public void updateBook(Book book, BookInfoRequest bookInfoRequest,
                           MultipartFile imageFile) throws IOException {
        book.setName(bookInfoRequest.getName());
        book.setAuthor(bookInfoRequest.getAuthor());
        book.setPublisher(bookInfoRequest.getPublisher());
        book.setBookCondition(bookInfoRequest.getBookCondition());
        book.setListPrice(bookInfoRequest.getListPrice());
        book.setSellingPrice(bookInfoRequest.getSellingPrice());

        if(imageFile!=null&& !imageFile.isEmpty()){
            book.setImageData(imageFile.getBytes());
            book.setImageName(imageFile.getOriginalFilename());
            book.setImageType(imageFile.getContentType());
        }
        repo.save(book);
    }

    public void deleteBookById(int bookId) {
        repo.deleteById(bookId);
    }

    public List<Book> searchBooks(String keyword) {
        return repo.searchBooks(keyword);
    }
}

package com.annie.campus_books.dto.request;

import com.annie.campus_books.model.BookCondition;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookInfoRequest {

    private String name;
    private String author;
    private String publisher;

    private BookCondition bookCondition;

    private BigDecimal listPrice;
    private BigDecimal sellingPrice;

}

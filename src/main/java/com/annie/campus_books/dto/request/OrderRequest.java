package com.annie.campus_books.dto.request;

import lombok.Data;

@Data
public class OrderRequest {
    private int bookId;
    private String buyerAccount;
}

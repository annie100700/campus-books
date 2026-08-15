package com.annie.campus_books.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID orderId;

    @OneToOne
    @JoinColumn(name = "bookId",referencedColumnName = "id")
    private Book book;
    private String bookName;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "sellerAccount",referencedColumnName = "account")
    private User seller;
    @ManyToOne
    @JoinColumn(name = "buyerAccount",referencedColumnName = "account")
    private User buyer;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @JdbcTypeCode(SqlTypes.JSON)
    private Status status;
    public enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

}

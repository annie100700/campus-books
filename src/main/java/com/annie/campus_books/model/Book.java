package com.annie.campus_books.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
//@Getter
//@Setter
@Data
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String author;
    private String publisher;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "conditionId",referencedColumnName = "conditionId")
    private BookCondition bookCondition;

    @ManyToOne
    @JoinColumn(name = "sellerAccount",referencedColumnName = "account")
    private User seller;

    private BigDecimal listPrice;
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    public enum Status {
        FOR_SALE,
        SOLD_OUT,
        REMOVED
    }

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

}

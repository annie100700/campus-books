package com.annie.campus_books.model;

import jakarta.persistence.*;

import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "binary(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    private String name;
    private String version;
    private String author;
    private String publisher;

    @Embedded
    @AttributeOverride(name = "highlight", column = @Column(name = "highlight"))
    @AttributeOverride(name = "notes", column = @Column(name = "notes"))
    @AttributeOverride(name = "stain", column = @Column(name = "stain"))
    @AttributeOverride(name = "frontCoverMissing", column = @Column(name = "front_cover_missing"))
    @AttributeOverride(name = "backCoverMissing", column = @Column(name = "back_cover_missing"))
    @AttributeOverride(name = "splitVolume", column = @Column(name = "split_volume"))
    @AttributeOverride(name = "other", column = @Column(name = "other"))
    private BookCondition bookCondition;

    @ManyToOne
    @JoinColumn(name = "sellerAccount", referencedColumnName = "account")
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

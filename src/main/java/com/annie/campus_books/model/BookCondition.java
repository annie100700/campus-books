package com.annie.campus_books.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
//@Setter
//@Getter
@Data
@Entity
public class BookCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID conditionId;

    private Boolean highlight;
    private Boolean notes;
    private Boolean stain;
    private Boolean frontCoverMissing;
    private Boolean backCoverMissing;
    private Boolean splitVolume;

    private String other;

}

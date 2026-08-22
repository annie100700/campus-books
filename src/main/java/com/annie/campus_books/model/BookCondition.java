package com.annie.campus_books.model;

import jakarta.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class BookCondition {

    private Boolean highlight;
    private Boolean notes;
    private Boolean stain;
    private Boolean frontCoverMissing;
    private Boolean backCoverMissing;
    private Boolean splitVolume;

    private String other;

}

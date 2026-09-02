package com.annie.campus_books.dto.request;

import java.math.BigDecimal;

public record AddBookRequest(String name, String author, String publisher,
                             boolean highlight,
                             boolean notes,
                             boolean stain,
                             boolean frontCoverMissing,
                             boolean backCoverMissing,
                             boolean splitVolume,
                             String other,
                             BigDecimal listPrice, BigDecimal sellingPrice) {

    public AddBookRequest() {
        this("test-name", "test-author", "test-publisher",
                false, false, false, false, false, false,
                "test-other",
                new BigDecimal(100), new BigDecimal(10)
        );
    }

    public boolean isNullOrBlank() {
        return name == null || author == null || publisher == null || sellingPrice == null ||
                name.isBlank() || author.isBlank() || publisher.isBlank();
    }
}

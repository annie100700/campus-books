package com.annie.campus_books.dto.request;

import java.math.BigDecimal;

public record UpdateBookRequest(String name, String author, String publisher,
                                Boolean highlight,
                                Boolean notes,
                                Boolean stain,
                                Boolean frontCoverMissing,
                                Boolean backCoverMissing,
                                Boolean splitVolume,
                                String other,
                                BigDecimal listPrice, BigDecimal sellingPrice) {
    public UpdateBookRequest() {
        this("test-name", "test-author", "test-publisher",
                false, false, false, false, false, false,
                "test-other",
                new BigDecimal(100), new BigDecimal(10)
        );
    }

}

package com.annie.campus_books.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
//@Setter
//@Getter
@Data
@Entity
public class User {

    @Id
    private String account;
    @Column(nullable = false)
    private String password;
    private String email;
    private String address;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Role> role=new ArrayList<>();
    public enum Role {
        ADMIN,USER
    }

}

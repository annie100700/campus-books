package com.annie.campus_books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CampusBooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusBooksApplication.class, args);

//		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//		encryptor.setAlgorithm("PBEWithMD5AndDES");
//
//		encryptor.setPassword("my_jasypt_key");
//		String encrypted = encryptor.encrypt("abcd");
//		System.out.println(encrypted);
	}

}

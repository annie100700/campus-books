package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.UserInfoRequest;
import com.annie.campus_books.model.User;
import com.annie.campus_books.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/api.campus-books.com/users")
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user,String reEnterPassword) {
        if (service.accountBeenUsed(user.getAccount())) {
            return new ResponseEntity<>("Account already exist", HttpStatus.BAD_REQUEST);
        } else if (service.emailBeenUsed(user.getEmail())) {
            return new ResponseEntity<>("Email already been used", HttpStatus.BAD_REQUEST);
        } else if (!(reEnterPassword.equals(user.getPassword()))) {
            return new ResponseEntity<>("Password doesn't match", HttpStatus.BAD_REQUEST);
        } else
            service.register(user.getAccount(), user.getPassword(), user.getEmail(), user.getAddress());
        return new ResponseEntity<>("User added", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userAccount}")
    public ResponseEntity<User> getUserByAccount(@PathVariable String userAccount) {
        User user = service.getUserByAccount(userAccount);
        if (user != null)
            return new ResponseEntity<>(user, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{account}")
    public ResponseEntity<String> updateUserInfo(@PathVariable String account, @RequestBody UserInfoRequest userInfoRequest) {
        User user = service.getUserByAccount(account);
        if (user != null) {
            service.updateUserInfo(user, userInfoRequest);
            return new ResponseEntity<>("Updated", HttpStatus.OK);
        }

        return new ResponseEntity<>("Updated user doesn't exist", HttpStatus.BAD_REQUEST);

    }

    @PatchMapping("/{account}/password")
    public ResponseEntity<String> changePassword(@PathVariable String account,
                                                 @RequestPart String oldPassword, @RequestPart String newPassword) {
        User user = service.getUserByAccount(account);
        if (!(new BCryptPasswordEncoder().matches(oldPassword,user.getPassword()))) {
            return new ResponseEntity<>("Password uncorrected", HttpStatus.BAD_REQUEST);
        }else if(new BCryptPasswordEncoder().matches(oldPassword,newPassword))
            return new ResponseEntity<>("Password could not be same", HttpStatus.BAD_REQUEST);
        service.changePassword(user,newPassword);
        return new ResponseEntity<>("Password changed", HttpStatus.OK);
    }

    @DeleteMapping("/{account}")
    public ResponseEntity<String> deleteUserByAccount(@PathVariable String account) {
        User user = service.getUserByAccount(account);
        if (user != null) {
            service.deleteUserByAccount(account);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } else
            return new ResponseEntity<>("Deleted user doesn't exist", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        boolean login = service.login(user.getAccount(), user.getPassword());
        if (login)
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        else
            return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
    }

}

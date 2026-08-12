package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.ChangePasswordRequest;
import com.annie.campus_books.dto.request.LoginRequest;
import com.annie.campus_books.dto.request.UserInfoRequest;
import com.annie.campus_books.dto.request.UpdateUserRequest;
import com.annie.campus_books.dto.response.LoginResponse;
import com.annie.campus_books.model.User;
import com.annie.campus_books.service.JwtService;
import com.annie.campus_books.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RequestMapping("/api.campus-books.com/users")
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.accountBeenUsed(registerRequest.account())) {
            return new ResponseEntity<>("Account already exist", HttpStatus.BAD_REQUEST);
        } else if (userService.emailBeenUsed(registerRequest.email())) {
            return new ResponseEntity<>("Email already been used", HttpStatus.BAD_REQUEST);
        } else if (userService.isPasswordMatch(registerRequest.password(), registerRequest.confirmPassword())) {
            return new ResponseEntity<>("Password doesn't match", HttpStatus.BAD_REQUEST);
        }
        User user = userService.register(registerRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean login;
        if (user != null) {
        } else {
            return new ResponseEntity<>("account doesn't exist", HttpStatus.NOT_FOUND);
        }
        if (login) {
            String jwt = jwtService.generateLoginAccessToken(user);
            Date expiration = jwtService.parseToken(jwt).getExpiration();
            return new ResponseEntity<>(LoginResponse.getResponseOf(user, jwt, expiration), HttpStatus.OK);
        }
        return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
    }

    //logout

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userAccount}")
    public ResponseEntity<User> getUserByAccount(@PathVariable String userAccount) {
        User user = service.getUserByAccount(userAccount);
        if (user != null)
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/current-user")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/current-user")
    public ResponseEntity<?> updateCurrentUser(@AuthenticationPrincipal User user,
                                               @RequestBody UpdateUserRequest updateUserRequest) {
        User updatedUser = userService.updateUserByAccount(user, updateUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    //admin用
    @PatchMapping("/{account}")
    public ResponseEntity<?> updateUserByAccount(@PathVariable String account,
                                                 @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            User updatedUser = userService.updateUserByAccount(user, updateUserRequest);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>("updated user doesn't exist", HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{account}/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user,
                                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (userService.isPasswordMatch(user.getPassword(), changePasswordRequest.oldPassword())) {
            return new ResponseEntity<>("Password incorrect", HttpStatus.BAD_REQUEST);
        } else if (userService.isPasswordMatch(changePasswordRequest.newPassword(), changePasswordRequest.confirmNewPassword())) {
            return new ResponseEntity<>("New password doesn't match", HttpStatus.BAD_REQUEST);
        }
        if(userService.isPasswordMatch(changePasswordRequest.oldPassword(),  changePasswordRequest.newPassword())) {
            return new ResponseEntity<>("Password couldn't be same", HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(user, changePasswordRequest);
        return new ResponseEntity<>("Password changed", HttpStatus.OK);
    }

    @DeleteMapping("/current-user")
    public ResponseEntity<String> deleteCurrentUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
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

}

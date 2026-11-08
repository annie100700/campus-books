package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.*;
import com.annie.campus_books.dto.response.GenerateTokenResponse;
import com.annie.campus_books.dto.response.LoginResponse;
import com.annie.campus_books.model.*;
import com.annie.campus_books.service.BookService;
import com.annie.campus_books.service.JwtService;
import com.annie.campus_books.service.OrderService;
import com.annie.campus_books.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BookService bookService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.isNullOrBlank()) {
            return new ResponseEntity<>("Account and password is required field", HttpStatus.BAD_REQUEST);
        } else if (userService.accountBeenUsed(registerRequest.account())) {
            return new ResponseEntity<>("Account already exist", HttpStatus.BAD_REQUEST);
        } else if (userService.emailBeenUsed(registerRequest.email())) {
            return new ResponseEntity<>("Email already been used", HttpStatus.BAD_REQUEST);
        } else if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            return new ResponseEntity<>("Password doesn't match", HttpStatus.BAD_REQUEST);
        }
        User user = userService.register(registerRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserByAccount(loginRequest.account());
        boolean login;
        if (user != null) {
            if (user.isDeleted()) {
                return new ResponseEntity<>("Account has been deleted", HttpStatus.BAD_REQUEST);
            }
            login = userService.login(user, loginRequest);
            if (login) {
                GenerateTokenResponse jwt = jwtService.generateToken(user);
                return new ResponseEntity<>(new LoginResponse(jwt, user), HttpStatus.OK);
            }
            return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PostMapping("/refresh")
    public ResponseEntity<GenerateTokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = jwtService.getRefreshToken(refreshTokenRequest.refreshToken());
        if (refreshToken != null) {
            if (refreshToken.isExpired()) {
                jwtService.deleteRefreshToken(refreshToken);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            GenerateTokenResponse generateTokenResponse = jwtService.refreshToken(refreshToken);
            return new ResponseEntity<>(generateTokenResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(@AuthenticationPrincipal User user, @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = jwtService.getRefreshToken(refreshTokenRequest.refreshToken());
        if (refreshToken != null) {
            String tokenOwner = refreshToken.getUser().getAccount();
            if (user.getAccount().equals(tokenOwner) || user.isAdmin()) {
                jwtService.deleteRefreshToken(refreshToken);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{account}")
    public ResponseEntity<User> getUserByAccount(@AuthenticationPrincipal User current, @PathVariable String account) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            if (current.getAccount().equals(account) && !user.isDeleted()
                    || current.isAdmin()) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{account}/books")
    public ResponseEntity<?> getBooksByUser(@AuthenticationPrincipal User current, @PathVariable String account) {
        User user = userService.getUserByAccount(account);
        if (user != null && !user.isDeleted()) {
            if (current.getAccount().equals(account) || current.isAdmin()) {
                List<Book> books = bookService.getBooksByUser(account);
                return new ResponseEntity<>(books, HttpStatus.OK);
            }
            List<Book> books = bookService.getBooksByUserAndStatus(account, Book.Status.FOR_SALE);
            return new ResponseEntity<>(books, HttpStatus.OK);
        }
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{account}/books/{status}")
    public ResponseEntity<?> getBooksByUserAndStatus(@AuthenticationPrincipal User current,
                                                     @PathVariable String account,
                                                     @PathVariable String status) {
        User user = userService.getUserByAccount(account);
        Book.Status bookStatus;
        if (user != null && !user.isDeleted()) {
            if (bookService.isStatusLegal(status)) {
                bookStatus = Book.Status.valueOf(status);
            } else {
                return new ResponseEntity<>("status should be FOR_SALE, SOLD_OUT or REMOVED", HttpStatus.BAD_REQUEST);
            }
            if (current.getAccount().equals(account) ||
                    current.isAdmin()) {
                List<Book> books = bookService.getBooksByUserAndStatus(account, bookStatus);
                return new ResponseEntity<>(books, HttpStatus.OK);
            } else if (bookStatus.equals(Book.Status.FOR_SALE)) {
                List<Book> books = bookService.getBooksByUserAndStatus(account, bookStatus);
                return new ResponseEntity<>(books, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{account}/orders/purchase")
    public ResponseEntity<?> getPurchasesByUser(@AuthenticationPrincipal User current, @PathVariable String account) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            if (current.getAccount().equals(account) && !user.isDeleted() ||
                    current.isAdmin()) {
                List<UserOrder> orders = orderService.getPurchasesByUser(account);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{account}/orders/purchase/{status}")
    public ResponseEntity<?> getPurchasesByUserAndStatus(@AuthenticationPrincipal User current,
                                                         @PathVariable String account,
                                                         @PathVariable String status) {
        User user = userService.getUserByAccount(account);
        UserOrder.Status orderStatus;
        if (user != null) {
            if (orderService.isStatusLegal(status)) {
                orderStatus = UserOrder.Status.valueOf(status);
            } else {
                return new ResponseEntity<>("status should be IN_PROGRESS, COMPLETED or CANCELED", HttpStatus.BAD_REQUEST);
            }
            if (current.getAccount().equals(account) && !user.isDeleted() ||
                    current.isAdmin()) {
                List<UserOrder> orders = orderService.getPurchasesByUserAndStatus(account, orderStatus);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{account}/orders/sell")
    public ResponseEntity<?> getSellsByUser(@AuthenticationPrincipal User current, @PathVariable String account) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            if (current.getAccount().equals(account) && !user.isDeleted()
                    || current.isAdmin()) {
                List<UserOrder> orders = orderService.getSellsByUser(account);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{account}/orders/sell/{status}")
    public ResponseEntity<?> getSellsByUserAndStatus(@AuthenticationPrincipal User current,
                                                     @PathVariable String account,
                                                     @PathVariable String status) {
        User user = userService.getUserByAccount(account);
        UserOrder.Status orderStatus;
        if (user != null) {
            if (orderService.isStatusLegal(status)) {
                orderStatus = UserOrder.Status.valueOf(status);
            } else {
                return new ResponseEntity<>("status should be IN_PROGRESS, COMPLETED or CANCELED", HttpStatus.BAD_REQUEST);
            }
            if (current.getAccount().equals(account) && !user.isDeleted() ||
                    current.isAdmin()) {
                List<UserOrder> orders = orderService.getSellsByUserAndStatus(account, orderStatus);
                return new ResponseEntity<>(orders, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{account}")
    public ResponseEntity<?> updateUserByAccount(@AuthenticationPrincipal User current,
                                                 @PathVariable String account,
                                                 @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            if (current.getAccount().equals(account) && !user.isDeleted() ||
                    current.isAdmin()) {
                User updatedUser = userService.updateUserByAccount(user, updateUserRequest);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/current/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User current,
                                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.getUserByAccount(current.getAccount());
        if (!user.isDeleted()) {
            if (!userService.isPasswordMatch(changePasswordRequest.oldPassword(), user.getPassword())) {
                return new ResponseEntity<>("Password incorrect", HttpStatus.BAD_REQUEST);
            }
            if (!changePasswordRequest.newPassword().equals(changePasswordRequest.confirmNewPassword())) {
                return new ResponseEntity<>("New password doesn't match", HttpStatus.BAD_REQUEST);
            } else if (changePasswordRequest.oldPassword().equals(changePasswordRequest.newPassword())) {
                return new ResponseEntity<>("Password couldn't be same", HttpStatus.BAD_REQUEST);
            }
            userService.changePassword(user, changePasswordRequest.newPassword());
            return new ResponseEntity<>("Password changed", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody String email) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            if (!user.isDeleted()) {
                PasswordResetToken passwordResetToken = jwtService.generatePasswordResetToken(user);
                userService.sendResetPasswordEmail(user, passwordResetToken);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        PasswordResetToken passwordResetToken = jwtService.getPasswordResetToken(resetPasswordRequest.passwordResetToken());
        if (passwordResetToken != null && !passwordResetToken.isExpired()) {
            User user = passwordResetToken.getUser();
            if (resetPasswordRequest.newPassword().equals(resetPasswordRequest.confirmPassword())) {
                userService.changePassword(user, resetPasswordRequest.newPassword());
                jwtService.deleteResetPasswordToken(passwordResetToken);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>("New password do not match", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Token doesn't exist or already expired, please try again", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{account}")
    public ResponseEntity<String> deleteUserByAccount(@AuthenticationPrincipal User current,
                                                      @PathVariable String account) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            if (current.getAccount().equals(account) && !user.isDeleted()
                    || current.isAdmin()) {
                jwtService.deleteAllTokensByUser(user);
                userService.deleteUser(user);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

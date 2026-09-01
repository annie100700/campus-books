package com.annie.campus_books.controller;

import com.annie.campus_books.dto.request.ChangePasswordRequest;
import com.annie.campus_books.dto.request.LoginRequest;
import com.annie.campus_books.dto.request.RegisterRequest;
import com.annie.campus_books.dto.request.UpdateUserRequest;
import com.annie.campus_books.dto.response.GenerateTokenResponse;
import com.annie.campus_books.dto.response.LoginResponse;
import com.annie.campus_books.model.Book;
import com.annie.campus_books.model.RefreshToken;
import com.annie.campus_books.model.User;
import com.annie.campus_books.model.UserOrder;
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
@RequestMapping("/api.campus-books.com/users")
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
        if (userService.accountBeenUsed(registerRequest.account())) {
            return new ResponseEntity<>("Account already exist", HttpStatus.BAD_REQUEST);
        } else if (userService.emailBeenUsed(registerRequest.email())) {
            return new ResponseEntity<>("Email already been used", HttpStatus.BAD_REQUEST);
        } else if (!userService.isPasswordMatch(registerRequest.password(), registerRequest.confirmPassword())) {
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
            login = userService.login(user, loginRequest);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (login) {
            GenerateTokenResponse jwt = jwtService.generateToken(user);
            return new ResponseEntity<>(new LoginResponse(jwt, user), HttpStatus.OK);
        }
        return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<GenerateTokenResponse> refresh(@RequestBody String refreshTokenStr) {
        RefreshToken refreshToken=jwtService.getRefreshToken(refreshTokenStr);
        if(refreshToken!=null){
            if (refreshToken.isExpired()) {
                jwtService.deleteByToken(refreshTokenStr);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            GenerateTokenResponse generateTokenResponse = jwtService.refreshToken(refreshToken);
            return new ResponseEntity<>(generateTokenResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user, @RequestBody String refreshTokenStr) {
        RefreshToken refreshToken = jwtService.getRefreshToken(refreshTokenStr);
        if (refreshToken != null) {
            String tokenOwner = refreshToken.getUser().getAccount();
            if (user.getAccount().equals(tokenOwner) || user.isAdmin()) {
                jwtService.deleteByToken(refreshTokenStr);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{account}")
    public ResponseEntity<User> getUserByAccount(@PathVariable String userAccount) {
        User user = userService.getUserByAccount(userAccount);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/current/books")
    public ResponseEntity<?> getBooksOfCurrentUser(@AuthenticationPrincipal User user) {
        String account = user.getAccount();
        List<Book> books = bookService.getBooksByUser(account);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/current/books/{status}")
    public ResponseEntity<?> getBooksOfCurrentUserByStatus(@AuthenticationPrincipal User user,
                                                           @PathVariable Book.Status status) {
        String account = user.getAccount();
        List<Book> books = bookService.getBooksByUserAndStatus(account, status);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    //admin用
    @GetMapping("/{account}/books")
    public ResponseEntity<?> getBooksByUser(@PathVariable String account) {
        List<Book> books = bookService.getBooksByUser(account);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    //admin用
    @GetMapping("/{account}/books/{status}")
    public ResponseEntity<?> getBooksByUserAndStatus(@PathVariable String user, @PathVariable Book.Status status) {
        List<Book> books = bookService.getBooksByUserAndStatus(user, status);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/current/orders/purchase")
    public ResponseEntity<?> getPurchasesOfCurrentUser(@AuthenticationPrincipal User user) {
        String account = user.getAccount();
        List<UserOrder> orders = orderService.getPurchasesByUser(account);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/current/orders/purchase/{status}")
    public ResponseEntity<?> getPurchasesOfCurrentUserByStatus(@AuthenticationPrincipal User user,
                                                               @PathVariable UserOrder.Status status) {
        String account = user.getAccount();
        List<UserOrder> orders = orderService.getPurchasesByUserAndStatus(account, status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/current/orders/sell")
    public ResponseEntity<?> getSellsOfCurrentUser(@AuthenticationPrincipal User user) {
        String account = user.getAccount();
        List<UserOrder> orders = orderService.getSellsByUser(account);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/current/orders/sell/{status}")
    public ResponseEntity<?> getSellsOfCurrentUserByStatus(@AuthenticationPrincipal User user,
                                                           @PathVariable UserOrder.Status status) {
        String account = user.getAccount();
        List<UserOrder> orders = orderService.getSellsByUserAndStatus(account, status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //admin用
    @GetMapping("/{account}/orders/purchase")
    public ResponseEntity<?> getPurchasesByUser(@PathVariable String account) {
        List<UserOrder> orders = orderService.getPurchasesByUser(account);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //admin用
    @GetMapping("/{account}/orders/purchase/{status}")
    public ResponseEntity<?> getPurchasesByUserAndStatus(@PathVariable String account, @PathVariable UserOrder.Status status) {
        List<UserOrder> orders = orderService.getPurchasesByUserAndStatus(account, status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //admin用
    @GetMapping("/{account}/orders/sell")
    public ResponseEntity<?> getSellsByUser(@PathVariable String account) {
        List<UserOrder> orders = orderService.getSellsByUser(account);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //admin用
    @GetMapping("/{account}/orders/sell/{status}")
    public ResponseEntity<?> getSellsByUserAndStatus(@PathVariable String account, @PathVariable UserOrder.Status status) {
        List<UserOrder> orders = orderService.getSellsByUserAndStatus(account, status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PatchMapping("/current")
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
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/currrnt/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user,
                                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        if (userService.isPasswordMatch(user.getPassword(), changePasswordRequest.oldPassword())) {
            return new ResponseEntity<>("Password incorrect", HttpStatus.BAD_REQUEST);
        } else if (userService.isPasswordMatch(changePasswordRequest.newPassword(), changePasswordRequest.confirmNewPassword())) {
            return new ResponseEntity<>("New password doesn't match", HttpStatus.BAD_REQUEST);
        }
        if (userService.isPasswordMatch(changePasswordRequest.oldPassword(), changePasswordRequest.newPassword())) {
            return new ResponseEntity<>("Password couldn't be same", HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(user, changePasswordRequest);
        return new ResponseEntity<>("Password changed", HttpStatus.OK);
    }

    @DeleteMapping("/current")
    public ResponseEntity<String> deleteCurrentUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //admin用
    @DeleteMapping("/{account}")
    public ResponseEntity<String> deleteUserByAccount(@PathVariable String account) {
        User user = userService.getUserByAccount(account);
        if (user != null) {
            userService.deleteUser(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

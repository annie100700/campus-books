package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.ChangePasswordRequest;
import com.annie.campus_books.dto.request.LoginRequest;
import com.annie.campus_books.dto.request.RegisterRequest;
import com.annie.campus_books.dto.request.UpdateUserRequest;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest registerRequest) {
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User user = new User();
        user.setAccount(registerRequest.account());
        user.setPassword(encodedPassword);
        user.setEmail(registerRequest.email());
        if (registerRequest.address() != null) {
            user.setAddress(registerRequest.address());
        }
        user.setRole(User.Role.USER);
        return repo.save(user);
    }

    public boolean accountBeenUsed(String userAccount) {
        return repo.existsById(userAccount);
    }

    public boolean emailBeenUsed(String email) {
        return repo.existsUserByEmail(email);
    }

    public boolean isPasswordMatch(String password, String confirmPassword) {
        return passwordEncoder.matches(password, confirmPassword);
    }

    public boolean login(User user, LoginRequest loginRequest) {
        return passwordEncoder.matches(loginRequest.password(), user.getPassword());
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserByAccount(String userAccount) {
        return repo.findById(userAccount).orElse(null);
    }

    public User updateUserByAccount(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.email() != null) {
            user.setEmail(updateUserRequest.email());
        }
        if (updateUserRequest.address() != null) {
            user.setAddress(updateUserRequest.address());
        }
        return repo.save(user);
    }

    public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {
        String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.newPassword());
        user.setPassword(encodedNewPassword);
        repo.save(user);
    }

    public void deleteUser(User user) {
        user.setDeleted(true);
        repo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        return repo.findById(account).
                orElseThrow(() -> new UsernameNotFoundException("User " + account + " is not found"));
    }

}

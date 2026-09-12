package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.LoginRequest;
import com.annie.campus_books.dto.request.RegisterRequest;
import com.annie.campus_books.dto.request.UpdateUserRequest;
import com.annie.campus_books.model.PasswordResetToken;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.UserRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    public void changePassword(User user, String newPassword) {
        String encodedNewPassword = passwordEncoder.encode(newPassword);
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

    public User getUserByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    @Async
    public void sendResetPasswordEmail(User user, PasswordResetToken passwordResetToken) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("campusBooks@gmail.com");
            simpleMailMessage.setTo(user.getEmail());
            simpleMailMessage.setSubject("Please verify your email to reset password");

            String url = "http://localhost:8080/api.campus-books.com/users/reset-password?token=" + passwordResetToken.getToken();
            String text = String.format("Click here to reset your password, this links will expired in 10 minutes: %s", url);

            simpleMailMessage.setText(text);
            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            logger.error(user.getEmail(), e);
        }
    }

}

package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.UserInfoRequest;
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

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserByAccount(String userAccount) {
        return repo.findById(userAccount).orElse(null);
    }

    public User updateUserByAccount(User user, UpdateUserRequest updateUserRequest) {
        user.setEmail(updateUserRequest.email());
        user.setAddress(updateUserRequest.address());
        return repo.save(user);
    }

    public void deleteUser(User user) {
    user.setDeleted(true);
    }

    public boolean accountBeenUsed(String userAccount) {
        return repo.existsById(userAccount);
    }

    public boolean emailBeenUsed(String email) {
        return repo.existsById(email);
    }

    public User register(RegisterRequest  registerRequest) {
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User user = new User();
        user.setAccount(registerRequest.account());
        user.setPassword(encodedPassword);
        user.setEmail(registerRequest.email());
        if (registerRequest.address() != null){
            user.setAddress(registerRequest.address());
        }
        user.setRole(User.Role.USER);
        return repo.save(user);
    }

    public boolean login(User user, LoginRequest loginRequest) {
            return passwordEncoder.matches(user.getPassword(), loginRequest.password());
    }

    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        return repo.findById(account).
                orElseThrow(() -> new UsernameNotFoundException(account+" not exist"));
    }
}

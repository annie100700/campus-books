package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.UserInfoRequest;
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

    public User register(User tempUser) {
        String encodedPassword = passwordEncoder.encode(tempUser.getPassword());
        User user = new User();
        user.setAccount(tempUser.getAccount());
        user.setPassword(encodedPassword);
        user.setEmail(tempUser.getEmail());
        user.setRole(User.Role.USER);
        if (tempUser.getAddress() != null)
            user.setAddress(tempUser.getAddress());
        return repo.save(user);
    }

    public boolean login(String account, String password) {
        User user = repo.findById(account).orElse(null);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
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

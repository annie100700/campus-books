package com.annie.campus_books.service;

import com.annie.campus_books.dto.request.UserInfoRequest;
import com.annie.campus_books.model.User;
import com.annie.campus_books.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public void updateUserInfo(User user, UserInfoRequest userInfoRequest) {
        user.setEmail(userInfoRequest.getEmail());
        user.setAddress(userInfoRequest.getAddress());
        repo.save(user);
    }

    public void deleteUserByAccount(String userAccount) {
        repo.deleteById(userAccount);
    }

    public boolean accountBeenUsed(String userAccount) {
        return repo.existsById(userAccount);
    }

    public boolean emailBeenUsed(String email) {
        return repo.existsById(email);
    }

    public void register(String account, String password, String email, String address) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setAccount(account);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        List<User.Role> role = new ArrayList<>();
        role.add(User.Role.USER);
        user.setRole(role);
        if (address != null)
            user.setAddress(address);
        repo.save(user);
    }

    public boolean login(String account, String password) {
        User user = getUserByAccount(account);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByAccount(username);
        if (user == null) {
            throw new UsernameNotFoundException("Can't find user: " + username);
        }
        List<SimpleGrantedAuthority> roles = user.getRole().
                stream().
                map(role -> new SimpleGrantedAuthority(role.name())).
                toList();
        return org.springframework.security.core.userdetails.User.
                withUsername(username).
                password(user.getPassword()).
                authorities(roles).
                build();
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
    }
}

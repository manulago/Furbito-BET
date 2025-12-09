package com.furbitobet.backend.service;

import com.furbitobet.backend.model.User;
import com.furbitobet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(String username, String password, String email, User.Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(role);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBalance(user.getBalance().add(amount));
        return userRepository.save(user);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Autowired
    private com.furbitobet.backend.repository.BetRepository betRepository;

    public java.util.List<User> getRanking() {
        java.util.List<User> users = userRepository.findByRoleNotOrderByBalanceDesc(User.Role.ADMIN);
        java.util.List<Object[]> stats = betRepository.findNetProfitStats();

        java.util.Map<Long, BigDecimal> profits = new java.util.HashMap<>();
        for (Object[] row : stats) {
            Long userId = (Long) row[0];
            BigDecimal winnings = (BigDecimal) row[1];
            BigDecimal staked = (BigDecimal) row[2];
            if (winnings == null)
                winnings = BigDecimal.ZERO;
            if (staked == null)
                staked = BigDecimal.ZERO;
            profits.put(userId, winnings.subtract(staked));
        }

        for (User user : users) {
            user.setNetProfit(profits.getOrDefault(user.getId(), BigDecimal.ZERO));
        }

        return users;
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        if ("admin".equals(user.getUsername()) || user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        userRepository.deleteById(id);
    }

    public void updatePassword(Long userId, String newPassword) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}

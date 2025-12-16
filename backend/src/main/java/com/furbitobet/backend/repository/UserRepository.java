package com.furbitobet.backend.repository;

import com.furbitobet.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    java.util.List<User> findAllByOrderByBalanceDesc();

    java.util.List<User> findByRoleNotOrderByBalanceDesc(User.Role role);

    Optional<User> findByConfirmationToken(String confirmationToken);

    // SECURITY: Pessimistic write lock to prevent race conditions in balance
    // updates
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithLock(@org.springframework.data.repository.query.Param("id") Long id);
}

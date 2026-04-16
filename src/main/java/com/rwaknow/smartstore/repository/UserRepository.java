package com.rwaknow.smartstore.repository;

import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // ─ Admin analytics ─
    long countByRole(UserRole role);
    long countByRoleAndActive(UserRole role, boolean active);
    long countByRoleAndCreatedAtAfter(UserRole role, LocalDateTime date);
    List<User> findTop5ByOrderByCreatedAtDesc();
    boolean existsByEmailAndIdNot(String email, Long id);
}
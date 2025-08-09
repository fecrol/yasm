package com.github.fecrol.yasm.user.repositories;

import com.github.fecrol.yasm.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByHandle(String handle);
}

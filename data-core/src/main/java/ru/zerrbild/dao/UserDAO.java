package ru.zerrbild.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zerrbild.entities.UserEntity;

import java.util.Optional;

public interface UserDAO extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByTgUserId(long tgUserId);
    Optional<UserEntity> findByEmail(String email);
}

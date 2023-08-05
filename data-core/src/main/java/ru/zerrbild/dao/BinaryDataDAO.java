package ru.zerrbild.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zerrbild.entities.BinaryDataEntity;

public interface BinaryDataDAO extends JpaRepository<BinaryDataEntity, Long> {
}

package ru.zerrbild.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zerrbild.entities.DocumentEntity;

public interface DocumentDAO extends JpaRepository<DocumentEntity, Long> {
}

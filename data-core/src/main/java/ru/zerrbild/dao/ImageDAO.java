package ru.zerrbild.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zerrbild.entities.ImageEntity;

public interface ImageDAO extends JpaRepository<ImageEntity, Long> {
}

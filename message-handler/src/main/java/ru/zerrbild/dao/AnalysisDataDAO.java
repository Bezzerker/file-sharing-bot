package ru.zerrbild.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zerrbild.entities.AnalysisDataEntity;

public interface AnalysisDataDAO extends JpaRepository<AnalysisDataEntity, Long> {
}

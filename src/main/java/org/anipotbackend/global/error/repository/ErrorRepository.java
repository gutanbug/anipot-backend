package org.anipotbackend.global.error.repository;

import org.anipotbackend.global.error.model.entity.ErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorRepository extends JpaRepository<ErrorLogEntity, Long> {
}

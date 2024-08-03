package org.anipotbackend.global.error.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorRepository extends JpaRepository<ErrorLogEntity, Long> {
}

package org.anipotbackend.global.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface CustomJpaRepository<T extends BaseEntity, ID extends Long> extends JpaRepository<T, ID> {

    Optional<T> findOneById(ID id);

    Optional<T> findOneBySubId(UUID subId);

    @Override
    <S extends T> S save(S entity);
}

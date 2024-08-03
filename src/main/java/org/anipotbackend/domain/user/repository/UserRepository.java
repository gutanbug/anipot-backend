package org.anipotbackend.domain.user.repository;

import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.domain.user.repository.custom.UserCustomRepository;
import org.anipotbackend.global.base.CustomJpaRepository;

public interface UserRepository extends CustomJpaRepository<User, Long>, UserCustomRepository {
}

package org.anipotbackend.domain.user.repository.custom.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.domain.user.repository.custom.UserCustomRepository;
import org.anipotbackend.global.error.exception.NotSingleObjectException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<User> findByPhone(String validatePhone) {
        List<User> users = entityManager.createQuery(
                "select u from User u where u.phone = :validatePhone and u.isDeleted = false",
                User.class)
                .setParameter("validatePhone", validatePhone)
                .getResultList();

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        } else if (users.isEmpty()) {
            return Optional.empty();
        }
        throw new NotSingleObjectException();
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        List<User> users = entityManager.createQuery(
                "select u from User u where u.loginId = :loginId and u.isDeleted = false",
                User.class)
                .setParameter("loginId", loginId)
                .getResultList();

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        } else if (users.isEmpty()) {
            return Optional.empty();
        }
        throw new NotSingleObjectException();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> users = entityManager.createQuery(
                "select u from User u where u.email = :email and u.isDeleted = false",
                User.class)
                .setParameter("email", email)
                .getResultList();

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        } else if (users.isEmpty()) {
            return Optional.empty();
        }
        throw new NotSingleObjectException();
    }
}

package org.anipotbackend.global.base.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.anipotbackend.global.base.BaseEntity;
import org.anipotbackend.global.base.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.util.ProxyUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;

@Getter
@NoRepositoryBean
public class CustomJpaRepositoryImpl<T extends BaseEntity, ID extends Long> extends SimpleJpaRepository<T, ID> implements CustomJpaRepository<T, ID> {
    @PersistenceContext
    private final EntityManager entityManager;
    private final EntityInformation<T, ?> entityInformation;

    protected Class<T> getDomainClass() {
        return super.getDomainClass();
    }

    public CustomJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    public CustomJpaRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager), entityManager);
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Entity must not be null");
        if (this.entityInformation.isNew(entity)) {
            this.entityManager.persist(entity);
            return entity;
        } else {
            return this.entityManager.merge(entity);
        }
    }

    @Override
    public Optional<T> findOneById(ID id) {
        Assert.notNull(id, "The given id must not be null");
        Class<T> domainType = this.getDomainClass();
        return Optional.ofNullable(this.entityManager.find(domainType, id));
    }

    @Override
    public Optional<T> findOneBySubId(UUID subId) {
        Assert.notNull(subId, "The given subId must not be null");
        TypedQuery<T> query = getQuery(this.withSubId(subId), getDomainClass());
        try {
            return Optional.of(query.setMaxResults(2).getSingleResult());
        } catch (NoResultException var2) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void delete(T entity) {
        Assert.notNull(entity, "Entity must not be null");
        if (!this.entityInformation.isNew(entity)) {
            Class<?> type = ProxyUtils.getUserClass(entity);
            T existing = this.entityManager.find((Class<T>) type, this.entityInformation.getId(entity));
            if (existing != null) {
                this.entityManager.remove(this.entityManager.contains(entity) ? entity : this.entityManager.merge(entity));
            }
        }
    }

    private Specification<T> withSubId(UUID subId) {
        return (root, query, cb) -> cb.equal(root.get("subId"), subId);
    }

    protected TypedQuery<T> getQuery(Specification<T> spec, Class<T> domainClass) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(domainClass);
        Root<T> root = this.applySpecificationToCriteria(spec, domainClass, query);
        query.select(root);

        return this.entityManager.createQuery(query);
    }

    private Root<T> applySpecificationToCriteria(@Nullable Specification<T> spec, Class<T> domainClass, CriteriaQuery<T> query) {
        Assert.notNull(domainClass, "Domain class must not be null");
        Assert.notNull(query, "CriteriaQuery must not be null");
        Root<T> root = query.from(domainClass);
        if (spec == null) {
            return root;
        } else {
            CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
            Predicate predicate = spec.toPredicate(root, query, builder);
            if (predicate != null) {
                query.where(predicate);
            }

            return root;
        }
    }
}

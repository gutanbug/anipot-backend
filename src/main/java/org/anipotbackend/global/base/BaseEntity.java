package org.anipotbackend.global.base;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
    public BaseEntity() {
        this.subId = UUID.randomUUID();
        this.isDeleted = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subId", columnDefinition = "BINARY(16)")
    private UUID subId;

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public boolean notDeleted() {
        return !isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity baseEntity)) return false;
        return Objects.equals(id, baseEntity.id) && Objects.equals(subId, baseEntity.subId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subId);
    }
}

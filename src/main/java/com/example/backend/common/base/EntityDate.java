package com.example.backend.common.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // 엔티티 수정 시 자동으로 생성, 수정일자 등록
public class EntityDate {
    @CreatedDate    // 엔티티 최초 생성 시 생성 일자를 자동으로 채워줌
    @Column(nullable = false, updatable = false, name = "created_at")    // null 값 받을 수 없음 / update도 할 수 없음
    private LocalDateTime createdAt;

    @LastModifiedDate   // 엔티티 수정 시 수정 일자를 자동으로 채워줌
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}


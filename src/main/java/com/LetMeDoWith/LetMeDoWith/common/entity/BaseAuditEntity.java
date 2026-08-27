package com.LetMeDoWith.LetMeDoWith.common.entity;

import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
@Getter
public class BaseAuditEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    private String getAuditingMemberId() {
        String memberId = AuthUtil.getMemberId();
        if (memberId == null) return "system";
        else return memberId;
    }

    /**
     * JPA로 영속화하지 않는 경우, INSERT 전에 해당 메서드 실행
     */
    public void setCreateAuditingInfo() {
        LocalDateTime now = SystemTimeUtil.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.createdBy = this.getAuditingMemberId();
        this.updatedBy = this.getAuditingMemberId();
    }

    /**
     * JPA로 영속화하지 않는 경우, UPDATE 전에 해당 메서드 실행
     */
    public void setUpdateAuditingInfo() {
        LocalDateTime now = SystemTimeUtil.now();
        this.updatedAt = now;
        this.updatedBy = this.getAuditingMemberId();
    }
}

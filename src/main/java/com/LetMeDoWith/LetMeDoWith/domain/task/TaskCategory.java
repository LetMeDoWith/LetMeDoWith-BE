package com.LetMeDoWith.LetMeDoWith.domain.task;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AggregateRoot
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCategory extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    @Builder.Default
    private Yn isActive = Yn.FALSE;
    
    @Column(nullable = false)
    private TaskCategoryCreationType creationType;
    
    @Column(nullable = false)
    private String emoji;
    
    @Column
    private Long categoryHolderId;
    
    @Getter
    @AllArgsConstructor
    public enum TaskCategoryCreationType implements BaseEnum {
        COMMON("COMMON", "공통"),
        USER_CUSTOM("USER_CUSTOM", "유저 개인");
        
        private final String code;
        private final String description;
    }
}
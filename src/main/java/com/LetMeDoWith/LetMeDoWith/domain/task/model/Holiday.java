package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "holiday")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holiday extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "country_code", nullable = false)
    private CountryCode countryCode = CountryCode.KR;
    
    @Column(nullable = false)
    private LocalDate date; // 공휴일 날짜
    
    @Column(nullable = false)
    private String name; // 공휴일 명칭 (예: 설날, 추석)
    
    public static Holiday of(CountryCode countryCode, LocalDate date, String name) {
        return Holiday.builder()
                      .countryCode(countryCode)
                      .date(date)
                      .name(name)
                      .build();
    }
}
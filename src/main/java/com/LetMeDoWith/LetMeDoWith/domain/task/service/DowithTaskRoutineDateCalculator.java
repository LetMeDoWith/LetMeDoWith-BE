package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DomainService
public class DowithTaskRoutineDateCalculator {

    public RoutineDateResult getRoutineDatesToModify(DowithTask dowithTask, Set<LocalDate> newRoutineDates) {

        LocalDateTime now = SystemTimeUtil.now();
        LocalDate nowDate = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        // input routineDates 중에서 업데이트 불가한 routine 일자(과거일자)가 DB에 저장된 routine 중 업데이트 불가한
        // 일자와 일치하는지 확인
        Set<LocalDate> notUpdateAvailDates = newRoutineDates.stream()
                .filter(date -> DateTimeUtil.isBeforeOrEqual(date, nowDate))
                .filter(date -> !date.isEqual(nowDate) || nowTime.isBefore(dowithTask.getStartTime()))
                .collect(Collectors.toSet());
        if (!dowithTask.getUpdateNotAvailRoutineDates().equals(notUpdateAvailDates)) {
            return RoutineDateResult.ofInvalid();
        }

        Set<LocalDate> updateAvailDates = new HashSet<>(newRoutineDates);
        updateAvailDates.removeAll(notUpdateAvailDates);

        Set<LocalDate> existingUpdateAvailDates = dowithTask.getUpdateAvailRoutineDates();

        // 기존 Routine Dates 중에서 삭제해야할 routine 선별
        Set<LocalDate> toDeleteRoutineDates = new HashSet<>(existingUpdateAvailDates);
        toDeleteRoutineDates.removeAll(updateAvailDates);

        // new Routine Dates 중에서 추가해야할 routine 선별
        Set<LocalDate> toCreateRoutineDates = new HashSet<>(updateAvailDates);
        toCreateRoutineDates.removeAll(existingUpdateAvailDates);

        return RoutineDateResult.of(toCreateRoutineDates, toDeleteRoutineDates);
    }

    @AllArgsConstructor
    public static class RoutineDateResult {

        private boolean isNewRoutineDatesValid;

        @Getter
        private Set<LocalDate> toCreateRoutineDates;

        @Getter
        private Set<LocalDate> toDeleteRoutineDates;

        public static RoutineDateResult of(Set<LocalDate> toCreateRoutineDates, Set<LocalDate> toDeleteRoutineDates) {
            return new RoutineDateResult(true, toCreateRoutineDates, toDeleteRoutineDates);
        }

        public static RoutineDateResult ofInvalid() {
            return new RoutineDateResult(false, null, null);
        }

        public boolean isValid() {
            return isNewRoutineDatesValid;
        }
    }
}

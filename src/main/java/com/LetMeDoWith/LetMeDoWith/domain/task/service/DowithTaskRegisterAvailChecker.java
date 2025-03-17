package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DomainService
public class DowithTaskRegisterAvailService {

  private static final int DEFAULT_AVAIL_COUNT_PER_DAY = 1; // 일일 두윗모드 기본 사용가능 개수
  private static final int FEED_BACK_COUNT_PER_DAY_FOR_NEW_TASK = 3; // 두윗모드 생성 위한 일별 잔소리

  public RegisterAvailResult isRegisterAvail(Set<LocalDate> targetDates,
      List<DowithTask> existingTasks) {

    Map<LocalDate, List<DowithTask>> dowithTaskMap = new HashMap<>();
    targetDates.forEach(date -> dowithTaskMap.computeIfAbsent(date, key -> new ArrayList<>()));

    existingTasks.forEach(task -> dowithTaskMap.get(task.getDate()).add(task));

    boolean isAvail = true;
    List<LocalDate> notAvailDates = new ArrayList<>();
    for (Map.Entry<LocalDate, List<DowithTask>> entry : dowithTaskMap.entrySet()) {
      if (entry.getValue().size() >= DEFAULT_AVAIL_COUNT_PER_DAY) {
        isAvail = false;
        notAvailDates.add(entry.getKey());
      }
      // TODO - 추후 FeedBack Aggregate 생성 시, 잔소리 관련 정책 적용
    }

    return new RegisterAvailResult(isAvail, notAvailDates);
  }

  public record RegisterAvailResult(
      boolean isAvail,
      List<LocalDate> notAvailDates
  ) {

  }

}

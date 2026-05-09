package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.FailedDowithTaskCountQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.FeedbackAvailableDowithTasksQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.MemberTaskSuccessStatsQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DowithTaskQueryRepository {

    List<DowithTaskQueryDto> getDowithTasks(String memberId, LocalDate startDate, LocalDate endDate);

    Optional<DowithTaskDetailQueryDto> getDowithTask(String memberId, Long dowithTaskId);

    List<SuccessDowithTaskQueryDto> getSuccessDowithTasks(String requestMemberId, int offset, int limit);

    Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds);

    List<FeedbackAvailableDowithTasksQueryDto> getFeedbackAvailableDowithTasks(Long offset, int size);

    Long countFeedbackAvailableDowithTasks();

    List<FailedDowithTaskCountQueryDto> getFailedTaskCountsByMember(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime);

    /**
     * 집계 기간 내 수행 대상이었던 두윗을 기준으로 회원별 성공/전체 건수를 조회한다.
     */
    List<MemberTaskSuccessStatsQueryDto> getTaskSuccessStatsByMember(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime);
}

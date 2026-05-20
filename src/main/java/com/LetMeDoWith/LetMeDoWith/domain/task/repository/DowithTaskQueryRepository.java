package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskLikeMemberQueryDto;
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

    /** 과제별 좋아요 수. 단건 카운트({@link #countDowithTaskLikes(Long)})와 동일하게 {@code MemberStatus.NORMAL} 회원 좋아요만 집계한다. */
    Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds);

    /** 단건 카운트. 집계 기준은 {@link #countDowithTaskLikes(java.util.Set)} 와 동일하다. */
    long countDowithTaskLikes(Long dowithTaskId);

    List<DowithTaskLikeMemberQueryDto> getDowithTaskLikers(Long dowithTaskId, int offset, int limit);

    List<FeedbackAvailableDowithTasksQueryDto> getFeedbackAvailableDowithTasks(
            Long offset, int size, String excludeMemberId);

    Long countFeedbackAvailableDowithTasks(String excludeMemberId);

    List<FailedDowithTaskCountQueryDto> getFailedTaskCountsByMember(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime);

    /**
     * 집계 기간 내 수행 대상이었던 두윗을 기준으로 회원별 성공/전체 건수를 조회한다.
     */
    List<MemberTaskSuccessStatsQueryDto> getTaskSuccessStatsByMember(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime);
}

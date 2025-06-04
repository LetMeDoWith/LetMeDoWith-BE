package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.task.TaskCompleteLevel;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "task_summary")
public class TaskSummary extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false, length = 26)
    private String memberId;

    @Column(name = "remained_dowith_task_count", nullable = false)
    private int availDowithTaskCount = 0;

    @Column(name = "remained_dowith_task_count_updated_at")
    private LocalDateTime availDowithTaskCountUpdatedAt;

    @Column(name = "last_attendance_date")
    private LocalDate lastAttendanceDate;

    @Column(name = "task_complete_level", nullable = false)
    private TaskCompleteLevel taskCompleteLevel = TaskCompleteLevel.GOOD;

    public static TaskSummary of(String memberId) {
        return TaskSummary.builder()
                .memberId(memberId)
                .availDowithTaskCount(0)
                .taskCompleteLevel(TaskCompleteLevel.GOOD)
                .build();
    }

    /**
     * 사용 가능 DowithTask 수 증가
     *
     * @param count 증가할 DowithTask 수
     */
    public void plusDowithTaskCount(int count) {
        this.availDowithTaskCount += count;
        this.availDowithTaskCountUpdatedAt = SystemTimeUtil.now();
    }

    /**
     * 사용 가능 DowithTask 수 감수
     *
     * @param count 감소할 DowithTask 수
     */
    public void deductDowithTaskCount(int count) {
        if (this.availDowithTaskCount < count) {
            throw new RestApiException(FailResponseStatus.BAD_REQUEST);
        }
        this.availDowithTaskCount -= count;
        this.availDowithTaskCountUpdatedAt = SystemTimeUtil.now();
    }

    /** 출석 보상 지급 - 출석 보상은 하루에 한 번만 지급 가능 - 출석 보상 지급 시, 오늘 날짜로 출석 날짜를 갱신하고, 남은 DowithTask 수 +1 */
    public void rewardAttendance() {
        if (this.lastAttendanceDate.equals(SystemTimeUtil.now().toLocalDate())) {
            throw new RestApiException(FailResponseStatus.BAD_REQUEST);
        }
        this.lastAttendanceDate = SystemTimeUtil.now().toLocalDate();
        this.plusDowithTaskCount(1);
    }
}

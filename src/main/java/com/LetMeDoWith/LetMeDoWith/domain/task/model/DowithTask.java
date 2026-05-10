package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "dowith_task")
@AggregateRoot
public class DowithTask extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false, length = 26)
    private String memberId;

    @Column(name = "task_category_id", nullable = true)
    private Long taskCategoryId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    private DowithTaskStatus status;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    @Column(name = "success_at")
    private LocalDateTime successDateTime;

    @Column(name = "complete_at")
    private LocalDateTime completeDateTime;

    @OneToMany(mappedBy = "dowithTask", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DowithTaskSuccess> successes;

    @OneToMany(mappedBy = "dowithTask", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DowithTaskLike> likes;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "dowith_task_routine_id")
    private DowithTaskRoutine routine;

    public static DowithTask of(
            String memberId, Long taskCategoryId, String title, LocalDate date, LocalTime startTime) {
        DowithTask task = DowithTask.builder()
                .memberId(memberId)
                .taskCategoryId(taskCategoryId)
                .title(title)
                .status(DowithTaskStatus.WAIT)
                .date(date)
                .startTime(startTime)
                .routine(null)
                .successes(null)
                .build();
        task.validateStartDateTime();
        return task;
    }

    public static DowithTask of(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            LocalDate routineRangeStartDate,
            LocalDate routineRangeEndDate,
            TaskRoutineCycle routineCycle,
            Set<Integer> routinePattern,
            boolean isExcludeHolidays) {
        DowithTaskRoutine dowithTaskRoutine = DowithTaskRoutine.of(
                routineRangeStartDate, routineRangeEndDate, routineCycle, routinePattern, isExcludeHolidays);
        return DowithTask.builder()
                .memberId(memberId)
                .taskCategoryId(taskCategoryId)
                .title(title)
                .status(DowithTaskStatus.WAIT)
                .date(date)
                .startTime(startTime)
                .routine(dowithTaskRoutine)
                .successes(null)
                .build();
    }

    public static List<DowithTask> of(DowithTask dowithTask, Set<LocalDate> routineDates) {
        List<DowithTask> result = new ArrayList<>();
        routineDates.remove(dowithTask.getDate());
        routineDates.forEach(date -> result.add(DowithTask.builder()
                .memberId(dowithTask.getMemberId())
                .taskCategoryId(dowithTask.getTaskCategoryId())
                .title(dowithTask.getTitle())
                .status(DowithTaskStatus.WAIT)
                .date(date)
                .startTime(dowithTask.getStartTime())
                .routine(dowithTask.getRoutine())
                .successes(null)
                .build()));
        return result;
    }

    /**
     * 루틴 여부
     *
     * @return
     */
    public boolean isRoutine() {
        return routine != null;
    }

    /**
     * 삭제 가능 여부
     *
     * @return
     */
    public boolean isDeleteAvail() {
        return SystemTimeUtil.now().isBefore(LocalDateTime.of(this.date, this.startTime));
    }

    /**
     * 루틴의 공휴알 제외 설정 여부
     *
     * @return
     */
    public boolean isRoutineExcludeHolidays() {
        return isRoutine() && this.routine.isExcludeHolidays();
    }

    /**
     * 두윗모드Task 시작 여부
     *
     * @return
     */
    public boolean isStarted() {
        return SystemTimeUtil.now().isAfter(LocalDateTime.of(this.date, this.startTime));
    }

    /**
     * 루틴 생성
     *
     * @param rangeStartDate
     * @param rangeEndDate
     * @param taskRoutineCycle
     * @param taskRoutinePattern
     * @param isExcludeHolidays
     */
    public void createRoutine(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle taskRoutineCycle,
            Set<Integer> taskRoutinePattern,
            boolean isExcludeHolidays) {

        this.routine = DowithTaskRoutine.of(
                rangeStartDate, rangeEndDate, taskRoutineCycle, taskRoutinePattern, isExcludeHolidays);
    }

    /**
     * 컨텐츠 수정
     *
     * @param title
     * @param taskCategoryId
     */
    public void updateContents(String title, Long taskCategoryId) {

        this.title = title;
        this.taskCategoryId = taskCategoryId;
    }

    /**
     * 컨텐츠 수정
     *
     * @param title
     * @param taskCategoryId
     * @param date
     * @param startTime
     */
    public void updateContents(String title, Long taskCategoryId, LocalDate date, LocalTime startTime) {

        this.title = title;
        this.taskCategoryId = taskCategoryId;
        this.date = date;
        this.startTime = startTime;

        this.validateStartDateTime();
    }

    /**
     * 컨텐츠 수정
     *
     * @param title
     * @param taskCategoryId
     * @param startTime
     */
    public void updateContents(String title, Long taskCategoryId, LocalTime startTime) {

        this.title = title;
        this.taskCategoryId = taskCategoryId;
        this.startTime = startTime;

        this.validateStartDateTime();
    }

    /**
     * 루틴 업데이트
     *
     * @param rangeStartDate
     * @param rangeEndDate
     * @param taskRoutineCycle
     * @param taskRoutinePattern
     * @param isExcludeHolidays
     */
    public void updateRoutine(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle taskRoutineCycle,
            Set<Integer> taskRoutinePattern,
            boolean isExcludeHolidays) {

        this.routine.updateRoutineCondition(
                rangeStartDate, rangeEndDate, taskRoutineCycle, taskRoutinePattern, isExcludeHolidays);
    }

    public void updateRoutine(DowithTaskRoutine routine) {
        this.routine = routine;
    }

    public void isRoutineModified(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle taskRoutineCycle,
            Set<Integer> taskRoutinePattern,
            boolean isExcludeHolidays) {
        if (!this.routine.getRangeStartDate().equals(rangeStartDate)
                || !this.routine.getRangeEndDate().equals(rangeEndDate)
                || !this.routine.getCycle().equals(taskRoutineCycle)
                || !this.routine.getPattern().getPattern().equals(taskRoutinePattern)
                || this.routine.isExcludeHolidays() != isExcludeHolidays) {
            throw new RestApiException(INVALID_REQUEST);
        }
    }

    /**
     * 인증 이미지 키 생성
     *
     * @return
     */
    public void validateSuccessImageUploadAvailable() {
        if (!status.equals(DowithTaskStatus.WAIT)) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (SystemTimeUtil.now()
                .isAfter(LocalDateTime.of(this.date, this.startTime).plusHours(1))) {
            throw new RestApiException(INVALID_REQUEST);
        }
    }

    /**
     * 두윗모드Task 인증
     *
     * @param imageUrls
     */
    public void success(List<String> imageUrls) {

        if (!status.equals(DowithTaskStatus.WAIT)) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (SystemTimeUtil.now().isAfter(LocalDateTime.of(this.date, this.startTime))) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (successes == null) {
            successes = new ArrayList<>();
        }

        for (String imageUrl : imageUrls) {
            successes.add(DowithTaskSuccess.of(this, imageUrl));
        }

        this.status = DowithTaskStatus.SUCCESS;
        this.successDateTime = SystemTimeUtil.now();
    }

    public void deleteRoutine() {
        this.routine = null;
    }

    /**
     * 두윗모드Task 잔소리 가능 여부 잔소리는 시작 시간 이후부터, 한시간 동안 가능하다.
     *
     * @return 잔소리 가능 여부
     */
    public boolean isFeedbackAvailable() {
        LocalDateTime now = SystemTimeUtil.now();
        LocalDateTime taskStartDateTime = LocalDateTime.of(this.date, this.startTime);

        // 현재 시간이 Task 시작 시간 이후이고, Task 시작 시간으로부터 1시간 이내인 경우
        return now.isAfter(taskStartDateTime.minusMinutes(1))
                && now.isBefore(taskStartDateTime.plusHours(1).plusMinutes(1))
                && status.equals(DowithTaskStatus.WAIT);
    }

    /**
     * 좋아요 생성. 이미 좋아요한 경우 예외.
     * 반환된 Like는 애플리케이션 서비스에서 별도 persist (aggregate 컬렉션에 넣지 않아 dowith_task UPDATE 방지).
     */
    public DowithTaskLike like(String memberId) {
        return DowithTaskLike.of(memberId, this);
    }

    private void validateStartDateTime() {
        LocalDateTime nowDateTime = SystemTimeUtil.now();
        LocalDate nowDate = nowDateTime.toLocalDate();
        LocalTime nowTime = nowDateTime.toLocalTime();
        if (nowDate.isEqual(date)) {
            if (nowTime.isAfter(startTime)) {
                throw new RestApiException(INVALID_REQUEST);
            }
        }
        if (date.isBefore(nowDateTime.toLocalDate())) {
            throw new RestApiException(INVALID_REQUEST);
        }
    }
}

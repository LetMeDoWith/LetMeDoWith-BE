package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import lombok.*;

/**
 * TodoTask 엔티티 클래스
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "todo_task")
@AggregateRoot
public class TodoTask extends BaseAuditEntity {

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
    private TodoTaskStatus status;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "todo_task_routine_id")
    private TodoTaskRoutine routine;

    /**
     * TodoTask 생성 메서드
     *
     * @param memberId       회원 ID
     * @param taskCategoryId 작업 카테고리 ID
     * @param title          제목
     * @param date           날짜
     * @param startTime      시작 시간
     * @return 생성된 TodoTask 객체
     */
    public static TodoTask of(String memberId, Long taskCategoryId, String title, LocalDate date, LocalTime startTime) {
        TodoTask newTodoTask = TodoTask.builder()
                .memberId(memberId)
                .taskCategoryId(taskCategoryId)
                .title(title)
                .status(TodoTaskStatus.WAIT)
                .date(date)
                .startTime(startTime)
                .routine(null)
                .build();
        newTodoTask.validate();
        return newTodoTask;
    }

    /**
     * 루틴을 포함한 TodoTask 리스트 생성 메서드
     *
     * @param memberId       회원 ID
     * @param taskCategoryId 작업 카테고리 ID
     * @param title          제목
     * @param startTime      시작 시간
     * @param routineDates   루틴 날짜 세트
     * @return 생성된 TodoTask 리스트
     */
    public static TodoTask ofWithRoutine(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {

        TodoTaskRoutine routine = TodoTaskRoutine.of(rangeStartDate, rangeEndDate, cycle, pattern, isExcludeHolidays);

        TodoTask newTodoTask = TodoTask.builder()
                .memberId(memberId)
                .taskCategoryId(taskCategoryId)
                .title(title)
                .status(TodoTaskStatus.WAIT)
                .routine(routine)
                .date(date)
                .startTime(startTime)
                .build();

        newTodoTask.validate();

        return newTodoTask;
    }

    /**
     * 루틴을 포함한 TodoTask 리스트 생성 메서드
     *
     * @param todoTask       기존 TodoTask
     * @param routineDates   루틴 날짜 세트
     * @return 생성된 TodoTask 리스트
     */
    public static List<TodoTask> of(TodoTask todoTask, Set<LocalDate> routineDates) {
        List<TodoTask> result = new ArrayList<>();
        routineDates.remove(todoTask.getDate());

        routineDates.forEach(date -> result.add(TodoTask.builder()
                .memberId(todoTask.getMemberId())
                .taskCategoryId(todoTask.getTaskCategoryId())
                .title(todoTask.getTitle())
                .status(TodoTaskStatus.WAIT)
                .date(date)
                .startTime(todoTask.getStartTime())
                .routine(todoTask.getRoutine())
                .build()));
        return result;
    }

    /**
     * TodoTask 루틴 생성
     *
     * @param rangeStartDate 루틴 시작 날짜
     * @param rangeEndDate 루틴 종료 날짜
     * @param cycle 루틴 반복 주기
     * @param pattern 루틴 반복 패턴
     * @param isExcludeHolidays 루틴 반복 주기에 공휴일 제외 여부
     * @return 생성된 TodoTask 리스트
     */
    public void createRoutine(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {
        this.routine = TodoTaskRoutine.of(rangeStartDate, rangeEndDate, cycle, pattern, isExcludeHolidays);
    }

    /**
     * 루틴 여부 확인
     *
     * @return 루틴 여부
     */
    public boolean isRoutine() {
        return routine != null;
    }

    /**
     * 내용 수정 가능 여부 확인 현재 비즈니스 요구사항에 의해 TodoTask는 조건 없이 수정 가능.
     *
     * @return 내용 수정 가능 여부
     */
    public Boolean isContentsEditable() {

        return Boolean.TRUE;
    }

    /**
     * 수정 가능한 루틴 날짜 조회
     *
     * @return 수정 가능한 루틴 날짜 세트
     */
    @Deprecated
    public Set<LocalDate> getUpdateAvailableDates() {
        return Set.of();
    }

    /**
     * TodoTask 내용 업데이트
     *
     * @param title          제목
     * @param taskCategoryId 작업 카테고리 ID
     * @param startTime      시작 시간
     */
    public void updateContent(String title, Long taskCategoryId, LocalTime startTime) {

        if (!isContentsEditable()) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        this.title = title;
        this.taskCategoryId = taskCategoryId;
        this.startTime = startTime;
        validate();
    }

    /**
     * TodoTask 내용 업데이트
     *
     * @param title          제목
     * @param taskCategoryId 작업 카테고리 ID
     * @param date           날짜
     * @param startTime      시작 시간
     */
    public void updateContent(String title, Long taskCategoryId, LocalDate date, LocalTime startTime) {

        if (!isContentsEditable()) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        this.title = title;
        this.taskCategoryId = taskCategoryId;
        this.date = date;
        this.startTime = startTime;
        validate();
    }

    /**
     * TodoTask 루틴 업데이트
     *
     * @param routine 루틴
     */
    public void updateRoutine(TodoTaskRoutine routine) {
        this.routine = routine;
    }

    /**
     * TodoTask 루틴 삭제
     *
     * @return 삭제된 루틴
     */
    public TodoTaskRoutine detachRoutine() {
        if (isRoutine()) {
            TodoTaskRoutine toDelete = this.routine;
            this.routine = null;
            return toDelete;
        } else {
            return null;
        }
    }

    public TodoTask success() {
        if (this.status != TodoTaskStatus.WAIT) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        this.status = TodoTaskStatus.SUCCESS;

        return this;
    }

    public TodoTask uncomplete() {
        if (this.status != TodoTaskStatus.SUCCESS) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        this.status = TodoTaskStatus.WAIT;

        return this;
    }

    /**
     * 유효성 검사
     */
    private void validate() {
        // if (LocalDate.now().isEqual(this.date) &&
        // LocalTime.now().isAfter(this.startTime))
        // {
        // throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        // }
        //
        // if (date.isBefore(LocalDate.now())) {
        // throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        // }

        if (this.title == null || this.title.isBlank()) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        if (this.date == null) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        if (this.date.isBefore(SystemTimeUtil.nowDate())) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoTask todoTask = (TodoTask) o;
        return Objects.equals(id, todoTask.id) && Objects.equals(memberId, todoTask.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberId);
    }
}

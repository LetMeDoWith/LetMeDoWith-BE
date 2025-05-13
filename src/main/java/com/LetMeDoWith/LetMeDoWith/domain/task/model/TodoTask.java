package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** TodoTask 엔티티 클래스 */
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "todo_task_routine_id")
    private TodoTaskRoutine routine;

    /**
     * TodoTask 생성 메서드
     *
     * @param memberId 회원 ID
     * @param taskCategoryId 작업 카테고리 ID
     * @param title 제목
     * @param date 날짜
     * @param startTime 시작 시간
     * @return 생성된 TodoTask 객체
     */
    public static TodoTask of(
            String memberId, Long taskCategoryId, String title, LocalDate date, LocalTime startTime) {
        TodoTask newTodoTask =
                TodoTask.builder()
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
     * 루틴을 포함한 TodoTask 생성 메서드
     *
     * @param memberId 회원 ID
     * @param taskCategoryId 작업 카테고리 ID
     * @param title 제목
     * @param date 날짜
     * @param startTime 시작 시간
     * @param routine 루틴
     * @return 생성된 TodoTask 객체
     */
    public static TodoTask of(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            TodoTaskRoutine routine) {
        TodoTask newTodoTask =
                TodoTask.builder()
                        .memberId(memberId)
                        .taskCategoryId(taskCategoryId)
                        .title(title)
                        .status(TodoTaskStatus.WAIT)
                        .date(date)
                        .startTime(startTime)
                        .routine(routine)
                        .build();
        newTodoTask.validate();
        return newTodoTask;
    }

    /**
     * 루틴을 포함한 TodoTask 리스트 생성 메서드
     *
     * @param memberId 회원 ID
     * @param taskCategoryId 작업 카테고리 ID
     * @param title 제목
     * @param date 날짜
     * @param startTime 시작 시간
     * @param routineDateSet 루틴 날짜 세트
     * @return 생성된 TodoTask 리스트
     */
    public static List<TodoTask> ofWithRoutine(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            Set<LocalDate> routineDateSet) {
        List<TodoTask> result = new ArrayList<>();
        Set<LocalDate> targetDateSet = new TreeSet<>(routineDateSet);
        targetDateSet.add(date);

        TodoTaskRoutine routine = TodoTaskRoutine.from(targetDateSet);
        targetDateSet.stream()
                .sorted()
                .toList()
                .forEach(
                        e -> {
                            TodoTask newTodoTask =
                                    TodoTask.builder()
                                            .memberId(memberId)
                                            .taskCategoryId(taskCategoryId)
                                            .title(title)
                                            .status(TodoTaskStatus.WAIT)
                                            .routine(routine)
                                            .date(e)
                                            .startTime(startTime)
                                            .build();
                            newTodoTask.validate();
                            result.add(newTodoTask);
                        });

        return result;
    }

    /**
     * 루틴을 포함한 TodoTask 리스트 생성 메서드 (공휴일 제외)
     *
     * @param memberId 회원 ID
     * @param taskCategoryId 작업 카테고리 ID
     * @param title 제목
     * @param date 날짜
     * @param startTime 시작 시간
     * @param routineDateSet 루틴 날짜 세트
     * @param holidays 제외할 공휴일 세트
     * @return 생성된 TodoTask 리스트
     */
    public static List<TodoTask> ofWithRoutine(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            Set<LocalDate> routineDateSet,
            Set<LocalDate> holidays) {
        Set<LocalDate> filteredDates =
                routineDateSet.stream()
                        .filter(routineDate -> !holidays.contains(routineDate))
                        .collect(Collectors.toSet());

        return ofWithRoutine(memberId, taskCategoryId, title, date, startTime, filteredDates);
    }

    /**
     * TodoTask 루틴 생성
     *
     * @param routineDates 루틴 날짜 세트
     * @return 생성된 TodoTask 리스트
     */
    public List<TodoTask> createRoutine(Set<LocalDate> routineDates) {
        TodoTaskRoutine routine = TodoTaskRoutine.from(routineDates);
        this.updateRoutine(routine);

        List<TodoTask> result = new ArrayList<>();
        result.add(this);
        routineDates.stream()
                .filter(date -> !date.isEqual(this.date))
                .collect(Collectors.toSet())
                .forEach(
                        date ->
                                result.add(
                                        TodoTask.of(
                                                this.memberId,
                                                this.taskCategoryId,
                                                this.title,
                                                date,
                                                this.startTime,
                                                routine)));

        return result;
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
     * 내용 수정 가능 여부 확인
     *
     * @return 내용 수정 가능 여부
     */
    public boolean isContentsEditable() {
        LocalDateTime now = LocalDateTime.now();
        if (now.toLocalDate().equals(this.date)) {
            return !now.toLocalTime().isAfter(this.startTime);
        }
        return true;
    }

    /**
     * 루틴 날짜 조회
     *
     * @return 루틴 날짜 세트
     */
    public Set<LocalDate> getRoutineDates() {
        if (isRoutine()) {
            return this.routine.getRoutineDates().getDates();
        } else {
            return Set.of();
        }
    }

    /**
     * 수정 가능한 루틴 날짜 조회
     *
     * @return 수정 가능한 루틴 날짜 세트
     */
    public Set<LocalDate> getUpdateAvailRoutineDates() {
        if (isRoutine()) {
            return this.routine.getDatesAfterAndEqual(LocalDate.now());
        } else {
            return Set.of();
        }
    }

    /**
     * 루틴 ID 조회
     *
     * @return 루틴 ID
     */
    public Long getRoutineId() {
        return isRoutine() ? routine.getId() : null;
    }

    /**
     * TodoTask 업데이트
     *
     * @param title 제목
     * @param taskCategoryId 작업 카테고리 ID
     * @param date 날짜
     * @param startTime 시작 시간
     * @param routine 루틴
     */
    public void update(
            String title,
            Long taskCategoryId,
            LocalDate date,
            LocalTime startTime,
            TodoTaskRoutine routine) {
        this.updateContent(title, taskCategoryId, date, startTime);
        this.updateRoutine(routine);
    }

    /**
     * TodoTask 내용 업데이트
     *
     * @param title 제목
     * @param taskCategoryId 작업 카테고리 ID
     * @param date 날짜
     * @param startTime 시작 시간
     */
    public void updateContent(
            String title, Long taskCategoryId, LocalDate date, LocalTime startTime) {
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
    public TodoTaskRoutine deleteRoutine() {
        if (isRoutine()) {
            TodoTaskRoutine toDelete = this.routine;
            this.routine = null;
            return toDelete;
        } else {
            return null;
        }
    }

    /** 유효성 검사 */
    private void validate() {
        if (LocalDate.now().isEqual(this.date) && LocalTime.now().isAfter(this.startTime)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        if (date.isBefore(LocalDate.now())) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
    }
}

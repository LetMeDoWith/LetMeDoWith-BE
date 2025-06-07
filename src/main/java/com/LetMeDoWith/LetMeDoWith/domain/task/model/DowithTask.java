package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    private List<DowithTaskConfirm> confirms;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "dowith_task_routine_id")
    private DowithTaskRoutine routine;

    public static DowithTask of(
            String memberId, Long taskCategoryId, String title, LocalDate date, LocalTime startTime) {
        DowithTask task =
                DowithTask.builder()
                        .memberId(memberId)
                        .taskCategoryId(taskCategoryId)
                        .title(title)
                        .status(DowithTaskStatus.WAIT)
                        .date(date)
                        .startTime(startTime)
                        .routine(null)
                        .confirms(null)
                        .build();
        task.validate();
        return task;
    }

    public static DowithTask of(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            DowithTaskRoutine routine) {
        DowithTask task =
                DowithTask.builder()
                        .memberId(memberId)
                        .taskCategoryId(taskCategoryId)
                        .title(title)
                        .status(DowithTaskStatus.WAIT)
                        .date(date)
                        .startTime(startTime)
                        .routine(routine)
                        .confirms(null)
                        .build();
        task.validate();
        return task;
    }

    public static List<DowithTask> ofWithRoutine(
            String memberId,
            Long taskCategoryId,
            String title,
            LocalDate date,
            LocalTime startTime,
            Set<LocalDate> routineDateSet) {
        List<DowithTask> result = new ArrayList<>();
        Set<LocalDate> targetDateSet = new TreeSet<>(routineDateSet);
        targetDateSet.add(date);

        DowithTaskRoutine routine = DowithTaskRoutine.from(targetDateSet);
        targetDateSet.stream()
                .sorted()
                .forEach(
                        e -> {
                            DowithTask task =
                                    DowithTask.builder()
                                            .memberId(memberId)
                                            .taskCategoryId(taskCategoryId)
                                            .title(title)
                                            .status(DowithTaskStatus.WAIT)
                                            .routine(routine)
                                            .date(e)
                                            .startTime(startTime)
                                            .build();
                            if (task.getDate().isEqual(date)) {
                                task.validate();
                            }
                            result.add(task);
                        });

        return result;
    }

    /**
     * 두윗모드Task 루틴 생성
     *
     * @param routineDates 루틴대상일자
     * @return 루틴 생성된 DowithTask domain entity 리스트
     */
    public List<DowithTask> createRoutine(Set<LocalDate> routineDates) {

        routineDates.add(this.date);
        this.routine = DowithTaskRoutine.from(routineDates);

        List<DowithTask> result = new ArrayList<>();
        routineDates.stream()
                .filter(date -> !date.isEqual(this.date))
                .collect(Collectors.toSet())
                .forEach(
                        date ->
                                result.add(
                                        DowithTask.of(
                                                this.memberId,
                                                this.taskCategoryId,
                                                this.title,
                                                date,
                                                this.startTime,
                                                routine)));
        result.add(this);

        return result;
    }

    /**
     * 루틴 추가
     *
     * @param routineDates
     * @param dowithTaskRepository
     */
    public void addRoutine(Set<LocalDate> routineDates, DowithTaskRepository dowithTaskRepository) {

        if (isRoutine()) {
            this.routine.addDates(routineDates);
            List<DowithTask> result = new ArrayList<>();
            routineDates.forEach(
                    date ->
                            result.add(
                                    DowithTask.of(
                                            this.memberId,
                                            this.taskCategoryId,
                                            this.title,
                                            date,
                                            this.startTime,
                                            this.routine)));

            dowithTaskRepository.saveDowithTasks(result);
        }
    }

    /**
     * 두윗모드Task 인증 이미지 키 생성
     *
     * @param imageFileNames
     * @return
     */
    public List<String> generateConfirmImageKey(List<String> imageFileNames) {

        Pattern validExtensions = Pattern.compile("(?i)^.+\\.(jpg|jpeg|png|gif|bmp|webp)$");
        if (imageFileNames.stream().anyMatch(name -> !validExtensions.matcher(name).matches())) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (!status.equals(DowithTaskStatus.WAIT)) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (SystemTimeUtil.now().isAfter(LocalDateTime.of(this.date, this.startTime))) {
            throw new RestApiException(INVALID_REQUEST);
        }

        int shardIndex = (int) (this.id % 16);

        List<String> confirmImageKeys = new ArrayList<>();
        for (String imageFileName : imageFileNames) {
            String timestamp =
                    SystemTimeUtil.now().toString().replace("[:\\-T]", "").substring(0, 14) + "Z";
            String uuid = UUID.randomUUID().toString();
            confirmImageKeys.add(
                    String.format(
                            "dowith_task_confirms/%02d/%s_%s.%s", shardIndex, timestamp, uuid, imageFileName));
        }
        return confirmImageKeys;
    }

    /**
     * 두윗모드Task 인증
     *
     * @param imageUrls
     */
    public void confirm(List<String> imageUrls) {

        if (!status.equals(DowithTaskStatus.WAIT)) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (SystemTimeUtil.now().isAfter(LocalDateTime.of(this.date, this.startTime))) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (confirms == null) {
            confirms = new ArrayList<>();
        }

        for (String imageUrl : imageUrls) {
            confirms.add(DowithTaskConfirm.of(this, imageUrl));
        }

        this.status = DowithTaskStatus.SUCCESS;
        this.successDateTime = SystemTimeUtil.now();
    }

    /**
     * 두윗모드Task 루틴 여부
     *
     * @return
     */
    public boolean isRoutine() {
        return routine != null;
    }

    /**
     * 두윗모드Task 내용 수정 가능 여부
     *
     * @return
     */
    public boolean isContentsEditable() {
        LocalDateTime now = SystemTimeUtil.now();
        if (now.toLocalDate().equals(this.date)) {
            return !now.toLocalTime().isAfter(this.startTime);
        }
        return true;
    }

    /**
     * 두윗모드Task 루틴 일자 조회
     *
     * @return
     */
    public Set<LocalDate> getRoutineDates() {
        if (isRoutine()) {
            return this.routine.getRoutineDates().getDates();
        } else {
            return Set.of();
        }
    }

    /**
     * 수정가능한(현재 혹은 미래일자) 두윗모드Task 루틴 일자 조회
     *
     * @return
     */
    public Set<LocalDate> getUpdateAvailRoutineDates() {
        LocalDate nowDate = SystemTimeUtil.nowDate();
        Set<LocalDate> result = isRoutine() ? this.routine.getDatesAfterAndEqual(nowDate) : Set.of();

        LocalDateTime now = SystemTimeUtil.now();
        if (result.contains(nowDate) && now.isAfter(LocalDateTime.of(nowDate, this.startTime))) {
            result.remove(SystemTimeUtil.nowDate());
        }

        return result;
    }

    /**
     * 수정 불가한(과거 일자) 두윗모드Task 루틴 일자 조회
     *
     * @return
     */
    public Set<LocalDate> getUpdateNotAvailRoutineDates() {
        LocalDateTime now = SystemTimeUtil.now();
        LocalDate nowDate = now.toLocalDate();
        Set<LocalDate> result = isRoutine() ? this.routine.getDatesBeforeAndEqual(nowDate) : Set.of();

        if (result.contains(nowDate)
                && now.isBefore(LocalDateTime.of(now.toLocalDate(), this.startTime))) {
            result.remove(nowDate);
        }
        return result;
    }

    /**
     * 루틴 수정
     *
     * @param routine
     */
    public void updateRoutine(DowithTaskRoutine routine) {
        this.routine = routine;
    }

    /**
     * 컨텐츠 수정 (루틴이 없는 경우)
     *
     * @param title
     * @param taskCategoryId
     */
    public void updateContents(String title, Long taskCategoryId) {

        if (!isRoutine()) {
            this.title = title;
            this.taskCategoryId = taskCategoryId;
        }

        this.validate();
    }

    /**
     * 컨텐츠 수정 (루틴이 없는 경우)
     *
     * @param title
     * @param taskCategoryId
     * @param date
     * @param startTime
     */
    public void updateContents(
            String title, Long taskCategoryId, LocalDate date, LocalTime startTime) {

        if (!isContentsEditable()) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (!isRoutine()) {
            this.title = title;
            this.taskCategoryId = taskCategoryId;
            this.date = date;
            this.startTime = startTime;
        }

        this.validate();
    }

    /**
     * 컨텐츠 수정 (루틴이 있는 경우)
     *
     * @param title
     * @param taskCategoryId
     * @param date
     * @param startTime
     * @param dowithTaskRepository
     */
    public void updateContentsWithRoutine(
            String title,
            Long taskCategoryId,
            LocalDate date,
            LocalTime startTime,
            DowithTaskRepository dowithTaskRepository) {

        if (!isContentsEditable()) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (isRoutine()) {
            List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(this.routine);
            Set<LocalDate> updateAvailRoutineDates = getUpdateAvailRoutineDates();

            // 기존 routine에서 수정 가능한 일자 삭제 = 과거 task와 수정 task routine 분리
            this.routine.deleteDates(updateAvailRoutineDates);

            // 수정 가능한 일자를 기반으로 새 routine 생성
            DowithTaskRoutine newRoutine = DowithTaskRoutine.from(updateAvailRoutineDates);
            dowithTasks.forEach(
                    task -> {
                        if (updateAvailRoutineDates.contains(task.getDate())) {
                            task.updateContents(title, taskCategoryId, date, startTime);
                            task.updateRoutine(newRoutine);
                        }
                    });
        }
    }

    /**
     * 컨텐츠 수정 (루틴이 있는 경우)
     *
     * @param title
     * @param taskCategoryId
     * @param dowithTaskRepository
     */
    public void updateContentsWithRoutine(
            String title, Long taskCategoryId, DowithTaskRepository dowithTaskRepository) {

        if (isRoutine()) {
            List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(this.routine);
            Set<LocalDate> updateAvailRoutineDates = getUpdateAvailRoutineDates();

            // 기존 routine에서 수정 가능한 일자 삭제 = 과거 task와 수정 task routine 분리
            this.routine.deleteDates(updateAvailRoutineDates);

            // 수정 가능한 일자를 기반으로 새 routine 생성
            DowithTaskRoutine newRoutine = DowithTaskRoutine.from(updateAvailRoutineDates);
            dowithTasks.forEach(
                    task -> {
                        if (updateAvailRoutineDates.contains(task.getDate())) {
                            task.updateContents(title, taskCategoryId);
                            task.updateRoutine(newRoutine);
                        }
                    });
        }
    }

    /**
     * 두윗모드Task 삭제
     *
     * @param dowithTaskRepository
     * @param dowithTaskRoutineRepository
     */
    public void delete(
            DowithTaskRepository dowithTaskRepository,
            DowithTaskRoutineRepository dowithTaskRoutineRepository) {

        if (!SystemTimeUtil.now().isBefore(LocalDateTime.of(this.date, this.startTime))) {
            throw new RestApiException(INVALID_REQUEST);
        }

        if (isRoutine()) {
            this.routine.deleteDate(this.date);

            if (this.routine.getDates().isEmpty()) {
                dowithTaskRoutineRepository.delete(this.routine);
            }
        }

        dowithTaskRepository.delete(this);
    }

    /**
     * 두윗모드Task 루틴 삭제 (루틴 포함)
     *
     * @param dowithTaskRepository
     * @param dowithTaskRoutineRepository
     */
    public int deleteWithRoutine(
            DowithTaskRepository dowithTaskRepository,
            DowithTaskRoutineRepository dowithTaskRoutineRepository) {

        if (!SystemTimeUtil.now().isBefore(LocalDateTime.of(this.date, this.startTime))) {
            throw new RestApiException(INVALID_REQUEST);
        }

        int deleteDowithTaskCount = 1;
        if (isRoutine()) {
            Set<LocalDate> toDeleteDates = this.routine.getDatesAfter(this.date);

            List<DowithTask> routineDowithTasks =
                    dowithTaskRepository.getDowithTasks(this.routine).stream()
                            .filter(e -> toDeleteDates.contains(e.getDate()))
                            .toList();

            deleteDowithTaskCount += routineDowithTasks.size();

            dowithTaskRepository.delete(routineDowithTasks);

            this.routine.deleteDates(toDeleteDates);
            if (this.routine.getDates().isEmpty()) {
                dowithTaskRoutineRepository.delete(this.routine);
            }
        }

        dowithTaskRepository.delete(this);
        return deleteDowithTaskCount;
    }

    /**
     * 두윗모드Task 루틴 삭제
     *
     * @return 물리 삭제할 DowithTaskRoutine domain entity
     */
    public void deleteRoutine(
            Set<LocalDate> routineDates, DowithTaskRepository dowithTaskRepository) {

        if (isRoutine()) {

            dowithTaskRepository.delete(
                    dowithTaskRepository.getDowithTasks(this.routine).stream()
                            .filter(e -> routineDates.contains(e.getDate()))
                            .toList());

            this.routine.deleteDates(routineDates);
        }
    }

    private void validate() {
        LocalDateTime nowDateTime = SystemTimeUtil.now();
        LocalDate nowData = nowDateTime.toLocalDate();
        LocalTime nowTime = nowDateTime.toLocalTime();
        if (nowData.isEqual(date)) {
            if (nowTime.isAfter(startTime)) {
                throw new RestApiException(INVALID_REQUEST);
            }
        }
        if (date.isBefore(nowDateTime.toLocalDate())) {
            throw new RestApiException(INVALID_REQUEST);
        }
    }
}

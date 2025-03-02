package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
    @Column(name = "dowithTaskId", nullable = false)
    private Long id;
    
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    
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
    
    @OneToOne(mappedBy = "dowithTask", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DowithTaskConfirm confirms;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dowith_task_routine_id")
    private DowithTaskRoutine routine;
    
    
    public static DowithTask of(Long memberId, Long taskCategoryId, String title, LocalDate date,
                                LocalTime startTime) {
        DowithTask newDowithTask = DowithTask.builder()
                                             .memberId(memberId)
                                             .taskCategoryId(taskCategoryId)
                                             .title(title)
                                             .status(DowithTaskStatus.WAIT)
                                             .date(date)
                                             .startTime(startTime)
                                             .routine(null)
                                             .confirms(null)
                                             .build();
        newDowithTask.validate();
        return newDowithTask;
    }
    
    public static DowithTask of(Long memberId, Long taskCategoryId, String title, LocalDate date,
                                LocalTime startTime, DowithTaskRoutine routine) {
        DowithTask newDowithTask = DowithTask.builder()
                                             .memberId(memberId)
                                             .taskCategoryId(taskCategoryId)
                                             .title(title)
                                             .status(DowithTaskStatus.WAIT)
                                             .date(date)
                                             .startTime(startTime)
                                             .routine(null)
                                             .confirms(null)
                                             .build();
        newDowithTask.validate();
        return newDowithTask;
    }
    
    public static List<DowithTask> ofWithRoutine(Long memberId, Long taskCategoryId, String title,
                                                 LocalDate date, LocalTime startTime,
                                                 Set<LocalDate> routineDateSet) {
        List<DowithTask> result = new ArrayList<>();
        Set<LocalDate> targetDateSet = new TreeSet<>(routineDateSet);
        targetDateSet.add(date);
        
        DowithTaskRoutine routine = DowithTaskRoutine.from(targetDateSet);
        targetDateSet.stream().sorted().toList().forEach(e -> {
            DowithTask newDowithTask = DowithTask.builder()
                                                 .memberId(memberId)
                                                 .taskCategoryId(taskCategoryId)
                                                 .title(title)
                                                 .status(DowithTaskStatus.WAIT)
                                                 .routine(routine)
                                                 .date(e)
                                                 .startTime(startTime)
                                                 .build();
            newDowithTask.validate();
            result.add(newDowithTask);
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
        
        DowithTaskRoutine routine = DowithTaskRoutine.from(routineDates);
        this.updateRoutine(routine);
        
        List<DowithTask> result = new ArrayList<>();
        result.add(this);
        routineDates.stream().filter(date -> !date.isEqual(this.date))
                    .collect(Collectors.toSet()).forEach(date ->
                                                             result.add(
                                                                 DowithTask.of(this.memberId,
                                                                               this.taskCategoryId,
                                                                               this.title,
                                                                               date,
                                                                               this.startTime,
                                                                               routine))
                    );
        
        return result;
        
    }
    
    /**
     * 두윗모드Task 인증
     *
     * @param imageUrl 인증이미지URl
     */
    public void confirm(String imageUrl) {
        confirms = DowithTaskConfirm.of(this, imageUrl);
        this.status = DowithTaskStatus.SUCCESS;
        this.successDateTime = LocalDateTime.now();
    }
    
    /**
     * 두윗모드Task 완료
     */
    public void complete() {
        this.status = DowithTaskStatus.COMPLETE;
        this.completeDateTime = LocalDateTime.now();
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
        LocalDateTime now = LocalDateTime.now();
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
     * 수정가능한 두윗모드Task 루틴 일자 조회
     *
     * @return
     */
    public Set<LocalDate> getUpdateAvailRoutineDates() {
        if (isRoutine()) {
            return this.routine.getDatesAfterAndEqual(LocalDate.now());
        } else {
            return Set.of();
        }
    }
    
    public Long getRoutineId() {
        return isRoutine() ? routine.getId() : null;
    }
    
    public void update(String title, Long taskCategoryId, LocalDate date, LocalTime startTime,
                       DowithTaskRoutine routine) {
        this.updateContent(title, taskCategoryId, date, startTime);
        this.updateRoutine(routine);
    }
    
    public void updateContent(String title, Long taskCategoryId, LocalDate date,
                              LocalTime startTime) {
        
        this.title = title;
        this.taskCategoryId = taskCategoryId;
        this.date = date;
        this.startTime = startTime;
        
        validate();
        
    }
    
    public void updateRoutine(DowithTaskRoutine routine) {
        this.routine = routine;
    }
    
    /**
     * 두윗모드Task 루틴 삭제
     *
     * @return 물리 삭제할 DowithTaskRoutine domain entity
     */
    public DowithTaskRoutine deleteRoutine() {
        if (isRoutine()) {
            DowithTaskRoutine toDelete = this.routine;
            this.routine = null;
            return toDelete;
        } else {
            return null;
        }
    }
    
    private void validate() {
        if (LocalDate.now().isEqual(date)) {
            if (LocalTime.now().isAfter(startTime)) {
                throw new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_START_TIME);
            }
        }
        if (date.isBefore(LocalDate.now())) {
            throw new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
        }
    }
//  public boolean isEqual(LocalDate date, LocalTime startTime, Set<LocalDate> routineDates) {
//    if(!this.date.equals(date)) return false;
//    if(!this.startTime.equals(startTime)) return false;
//    if(isRoutine()) {
//      return this.routine.isEqual(routineDates);
//    }else {
//      return routineDates == null || routineDates.isEmpty();
//    }
//  }


}

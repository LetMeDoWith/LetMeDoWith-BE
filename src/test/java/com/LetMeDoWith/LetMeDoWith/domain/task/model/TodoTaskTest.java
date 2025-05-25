// package com.LetMeDoWith.LetMeDoWith.domain.task.model;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
// import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
// import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
// import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
// import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.List;
// import java.util.Set;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
//
// class TodoTaskTest {
//
//    private static final Long MEMBER_ID = 1L;
//    private static final Long TASK_CATEGORY_ID = 100L;
//    private static final String TITLE = "테스트 할 일";
//    private static final LocalDate FUTURE_DATE = SystemTimeUtil.nowDate().plusDays(5);
//    private static final LocalTime VALID_TIME = LocalTime.of(10, 0);
//
//    @Test
//    @DisplayName("[SUCCESS] 일반 TodoTask 생성 성공")
//    void testCreateNormalTodoTaskSuccess() {
//        // when
//        TodoTask todoTask = TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE,
// VALID_TIME);
//
//        // then
//        assertThat(todoTask).isNotNull();
//        assertThat(todoTask.getMemberId()).isEqualTo(MEMBER_ID);
//        assertThat(todoTask.getTaskCategoryId()).isEqualTo(TASK_CATEGORY_ID);
//        assertThat(todoTask.getTitle()).isEqualTo(TITLE);
//        assertThat(todoTask.getDate()).isEqualTo(FUTURE_DATE);
//        assertThat(todoTask.getStartTime()).isEqualTo(VALID_TIME);
//        assertThat(todoTask.getStatus()).isEqualTo(TodoTaskStatus.WAIT);
//        assertThat(todoTask.isRoutine()).isFalse();
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] 루틴 TodoTask 생성 성공")
//    void testCreateRoutineTodoTasksSuccess() {
//        // given
//        Set<LocalDate> routineDates =
//                Set.of(
//                        FUTURE_DATE,
//                        FUTURE_DATE.plusDays(7),
//                        FUTURE_DATE.plusDays(14),
//                        FUTURE_DATE.plusDays(21));
//
//        // when
//        List<TodoTask> todoTasks =
//                TodoTask.ofWithRoutine(
//                        MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE, VALID_TIME,
// routineDates);
//
//        // then
//        assertThat(todoTasks).hasSize(4);
//
//        // 루틴 객체 공유 확인
//        TodoTaskRoutine routine = todoTasks.get(0).getRoutine();
//        assertThat(routine).isNotNull();
//
//        // 각 태스크 검증
//        for (TodoTask task : todoTasks) {
//            assertThat(task.getMemberId()).isEqualTo(MEMBER_ID);
//            assertThat(task.getTaskCategoryId()).isEqualTo(TASK_CATEGORY_ID);
//            assertThat(task.getTitle()).isEqualTo(TITLE);
//            assertThat(task.getStatus()).isEqualTo(TodoTaskStatus.WAIT);
//            assertThat(task.getStartTime()).isEqualTo(VALID_TIME);
//            assertThat(task.isRoutine()).isTrue();
//            assertThat(task.getRoutine()).isSameAs(routine);
//        }
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] 루틴 생성 후 루틴 날짜 조회 성공")
//    void testGetRoutineDatesSuccess() {
//        // given
//        Set<LocalDate> routineDates =
//                Set.of(FUTURE_DATE, FUTURE_DATE.plusDays(7), FUTURE_DATE.plusDays(14));
//        List<TodoTask> todoTasks =
//                TodoTask.ofWithRoutine(
//                        MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE, VALID_TIME,
// routineDates);
//        TodoTask todoTask = todoTasks.get(0);
//
//        // when
//        Set<LocalDate> retrievedDates = todoTask.getRoutineDates();
//
//        // then
//        assertThat(retrievedDates).containsExactlyInAnyOrderElementsOf(routineDates);
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] 일반 TodoTask에서 루틴 생성 성공")
//    void testCreateRoutineFromNormalTaskSuccess() {
//        // given
//        TodoTask todoTask = TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE,
// VALID_TIME);
//        Set<LocalDate> routineDates =
//                Set.of(FUTURE_DATE, FUTURE_DATE.plusDays(7), FUTURE_DATE.plusDays(14));
//
//        // when
//        List<TodoTask> routineTasks = todoTask.createRoutine(routineDates);
//
//        // then
//        assertThat(routineTasks).hasSize(3);
//        assertThat(todoTask.isRoutine()).isTrue();
//
//        // 동일한 루틴 공유 확인
//        TodoTaskRoutine routine = todoTask.getRoutine();
//        for (TodoTask task : routineTasks) {
//            assertThat(task.getRoutine()).isSameAs(routine);
//        }
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] TodoTask 내용 업데이트 성공")
//    void testUpdateContentSuccess() {
//        // given
//        TodoTask todoTask = TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE,
// VALID_TIME);
//        String newTitle = "수정된 제목";
//        Long newCategoryId = 200L;
//        LocalDate newDate = FUTURE_DATE.plusDays(1);
//        LocalTime newTime = LocalTime.of(14, 0);
//
//        // when
//        todoTask.updateContent(newTitle, newCategoryId, newDate, newTime);
//
//        // then
//        assertThat(todoTask.getTitle()).isEqualTo(newTitle);
//        assertThat(todoTask.getTaskCategoryId()).isEqualTo(newCategoryId);
//        assertThat(todoTask.getDate()).isEqualTo(newDate);
//        assertThat(todoTask.getStartTime()).isEqualTo(newTime);
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] TodoTask 루틴 삭제 성공")
//    void testDeleteRoutineSuccess() {
//        // given
//        Set<LocalDate> routineDates =
//                Set.of(FUTURE_DATE, FUTURE_DATE.plusDays(7), FUTURE_DATE.plusDays(14));
//        List<TodoTask> todoTasks =
//                TodoTask.ofWithRoutine(
//                        MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE, VALID_TIME,
// routineDates);
//        TodoTask todoTask = todoTasks.get(0);
//        TodoTaskRoutine routine = todoTask.getRoutine();
//
//        // when
//        TodoTaskRoutine deletedRoutine = todoTask.deleteRoutine();
//
//        // then
//        assertThat(deletedRoutine).isSameAs(routine);
//        assertThat(todoTask.getRoutine()).isNull();
//        assertThat(todoTask.isRoutine()).isFalse();
//    }
//
//    @Test
//    @DisplayName("[FAIL] 과거 날짜로 TodoTask 생성 실패")
//    void testCreateTodoTaskWithPastDateFail() {
//        // given
//        LocalDate pastDate = SystemTimeUtil.nowDate().minusDays(1);
//
//        // when & then
//        assertThatThrownBy(() -> TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, pastDate,
// VALID_TIME))
//                .isInstanceOf(RestApiException.class)
//                .hasFieldOrPropertyWithValue("status", FailResponseStatus.INVALID_REQUEST);
//    }
//
//    @Test
//    @DisplayName("[FAIL] 오늘 날짜이지만 지난 시간으로 TodoTask 생성 실패")
//    void testCreateTodoTaskWithPastTimeFail() {
//        // given
//        LocalDate today = SystemTimeUtil.nowDate();
//        LocalTime pastTime = SystemTimeUtil.nowTime().minusHours(1);
//
//        // when & then
//        assertThatThrownBy(() -> TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, today, pastTime))
//                .isInstanceOf(RestApiException.class)
//                .hasFieldOrPropertyWithValue("status", FailResponseStatus.INVALID_REQUEST);
//    }
//
//    @Test
//    @DisplayName("[FAIL] 지난 날짜로 TodoTask 내용 업데이트 실패")
//    void testUpdateContentWithPastDateFail() {
//        // given
//        TodoTask todoTask = TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE,
// VALID_TIME);
//        LocalDate pastDate = SystemTimeUtil.nowDate().minusDays(1);
//
//        // when & then
//        assertThatThrownBy(() -> todoTask.updateContent(TITLE, TASK_CATEGORY_ID, pastDate,
// VALID_TIME))
//                .isInstanceOf(RestApiException.class)
//                .hasFieldOrPropertyWithValue("status", FailResponseStatus.INVALID_REQUEST);
//    }
//
//    @Test
//    @DisplayName("[FAIL] 오늘 날짜이지만 지난 시간으로 내용 업데이트 실패")
//    void testUpdateContentWithPastTimeFail() {
//        // given
//        TodoTask todoTask = TodoTask.of(MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE,
// VALID_TIME);
//        LocalDate today = SystemTimeUtil.nowDate();
//        LocalTime pastTime = SystemTimeUtil.nowTime().minusHours(1);
//
//        // when & then
//        assertThatThrownBy(() -> todoTask.updateContent(TITLE, TASK_CATEGORY_ID, today, pastTime))
//                .isInstanceOf(RestApiException.class)
//                .hasFieldOrPropertyWithValue("status", FailResponseStatus.INVALID_REQUEST);
//    }
//
//    @Test
//    @DisplayName("[SUCCESS] 공휴일 제외 루틴 TodoTask 생성 성공")
//    void testCreateRoutineTodoTasksWithHolidayExclusionSuccess() {
//        // given
//        Set<LocalDate> routineDates =
//                Set.of(
//                        FUTURE_DATE,
//                        FUTURE_DATE.plusDays(7),
//                        FUTURE_DATE.plusDays(14),
//                        FUTURE_DATE.plusDays(21));
//        Set<LocalDate> holidays = Set.of(FUTURE_DATE.plusDays(7), FUTURE_DATE.plusDays(21));
//
//        // when
//        List<TodoTask> todoTasks =
//                TodoTask.ofWithRoutine(
//                        MEMBER_ID, TASK_CATEGORY_ID, TITLE, FUTURE_DATE, VALID_TIME, routineDates,
// holidays);
//
//        // then
//        assertThat(todoTasks).hasSize(2);
//
//        // 루틴 객체 공유 확인
//        TodoTaskRoutine routine = todoTasks.get(0).getRoutine();
//        assertThat(routine).isNotNull();
//
//        // 각 태스크 검증
//        for (TodoTask task : todoTasks) {
//            assertThat(task.getMemberId()).isEqualTo(MEMBER_ID);
//            assertThat(task.getTaskCategoryId()).isEqualTo(TASK_CATEGORY_ID);
//            assertThat(task.getTitle()).isEqualTo(TITLE);
//            assertThat(task.getStatus()).isEqualTo(TodoTaskStatus.WAIT);
//            assertThat(task.getStartTime()).isEqualTo(VALID_TIME);
//            assertThat(task.isRoutine()).isTrue();
//            assertThat(task.getRoutine()).isSameAs(routine);
//        }
//
//        // 공휴일이 제외되었는지 확인
//        Set<LocalDate> taskDates =
//
// todoTasks.stream().map(TodoTask::getDate).collect(java.util.stream.Collectors.toSet());
//        assertThat(taskDates).containsExactlyInAnyOrder(FUTURE_DATE, FUTURE_DATE.plusDays(14));
//    }
// }
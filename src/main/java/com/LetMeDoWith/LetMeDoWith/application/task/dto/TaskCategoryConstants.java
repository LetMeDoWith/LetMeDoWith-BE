package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory.TaskCategoryCreationType;
import java.util.Arrays;
import java.util.List;

/** TaskCategory 관련 상수 모음 클래스 */
public class TaskCategoryConstants {

    /** 공통 Task Category 모음 공통 카테고리는 상수의 성격을 가지므로, 데이터베이스에서 조회하지 말고, VO 형태로 애플리케이션에서 사용하도록 한다. */
    public static final TaskCategoryVO APPOINTMENT = new TaskCategoryVO(
            1L, // id는 미리 정한 값으로 설정
            "약속",
            TaskCategoryCreationType.COMMON,
            "🙋‍♀️",
            null);

    public static final TaskCategoryVO EXAM =
            new TaskCategoryVO(2L, "시험", TaskCategoryCreationType.COMMON, "🗓️", null);

    public static final TaskCategoryVO EXERCISE =
            new TaskCategoryVO(3L, "운동", TaskCategoryCreationType.COMMON, "🦺", null);

    public static final TaskCategoryVO DAILY_LIFE =
            new TaskCategoryVO(4L, "일상", TaskCategoryCreationType.COMMON, "🏙️", null);

    public static final TaskCategoryVO STUDY =
            new TaskCategoryVO(5L, "공부", TaskCategoryCreationType.COMMON, "📝", null);

    public static final TaskCategoryVO READING =
            new TaskCategoryVO(6L, "독서", TaskCategoryCreationType.COMMON, "📘", null);

    public static final TaskCategoryVO WORK =
            new TaskCategoryVO(7L, "작업", TaskCategoryCreationType.COMMON, "👩‍💻", null);

    public static final TaskCategoryVO OTHER = new TaskCategoryVO(8L, "기타", TaskCategoryCreationType.COMMON, "⏰", null);

    // COMMON 카테고리들의 리스트
    public static final List<TaskCategoryVO> COMMON_ALL =
            Arrays.asList(APPOINTMENT, EXAM, EXERCISE, DAILY_LIFE, STUDY, READING, WORK, OTHER);
}

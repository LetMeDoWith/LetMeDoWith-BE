package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory.TaskCategoryCreationType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import java.util.List;

@Schema(description = "Task 카테고리 조회 결과")
@SchemaProperty(name = "id", schema = @Schema(description = "조회한 Task Category의 dowithTaskId"))
@SchemaProperty(name = "title", schema = @Schema(description = "Task Category의 이름"))
@SchemaProperty(name = "creationType", schema = @Schema(description = "Task Category의 타입 (공통 / 유저 개인)", implementation = TaskCategoryCreationType.class))
@SchemaProperty(name = "emoji", schema = @Schema(description = "Task Category 표시 이모티콘"))
@SchemaProperty(name = "categoryHolderId", schema = @Schema(description = "유저 생성 Category 인 경우 생성한 member의 dowithTaskId"))
public record GetAllTaskCategoryRes(
    Long id,
    String title,
    TaskCategoryCreationType creationType,
    String emoji,
    Long categoryHolderId
) {
    
    // todo: 단일 객체 형태 말고 List 형태로 수정할 것.
    
    public static GetAllTaskCategoryRes from(TaskCategory taskCategory) {
        return new GetAllTaskCategoryRes(taskCategory.getId(),
                                         taskCategory.getTitle(),
                                         taskCategory.getCreationType(),
                                         taskCategory.getEmoji(),
                                         taskCategory.getCategoryHolderId());
    }
    
    public static List<GetAllTaskCategoryRes> from(List<TaskCategory> catList) {
        return catList.stream().map(GetAllTaskCategoryRes::from).toList();
    }
    
}
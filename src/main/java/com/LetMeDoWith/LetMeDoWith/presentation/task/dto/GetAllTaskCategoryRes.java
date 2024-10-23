package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.List;

public record GetAllTaskCategoryRes(
    Long id,
    String title,
    TaskCategoryCreationType creationType,
    String emoji,
    Long categoryHolderId
) {
    
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
package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskCategoryVO {
    
    private Long id;
    private String title;
    private TaskCategoryCreationType creationType;
    private String emoji;
    private Long categoryHolderId;
    
    public static TaskCategoryVO from(TaskCategory category) {
        return new TaskCategoryVO(
            category.getId(),
            category.getTitle(),
            category.getCreationType(),
            category.getEmoji(),
            category.getCategoryHolderId()
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskCategoryVO that = (TaskCategoryVO) o;
        return Objects.equals(id, that.id) && creationType == that.creationType;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, creationType);
    }
}
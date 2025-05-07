package com.LetMeDoWith.LetMeDoWith.integration.task;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TodoTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RetrieveTaskIntegrationTest extends AbstractIntegrationTest {
    
    static final String BASE_URL = "/api/v1/tasks";
    static final String RETRIEVE_TASKS_URL = BASE_URL;
    
    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;
    
    @Autowired
    TodoTaskJpaRepository todoTaskJpaRepository;
    
    @Autowired
    TaskCategoryJpaRepository taskCategoryJpaRepository;
    
    private TaskCategory taskCategory1, taskCategory2;
    
    @BeforeEach
    void setTestData() {
        
        taskCategory1 = taskCategoryJpaRepository.save(TaskCategory.of("test1",
                                                                       TaskCategory.TaskCategoryCreationType.COMMON,
                                                                       "test1-emoji",
                                                                       this.requestMember.getId()));
        
        taskCategory2 = taskCategoryJpaRepository.save(TaskCategory.of("test2",
                                                                       TaskCategory.TaskCategoryCreationType.COMMON,
                                                                       "test2-emoji",
                                                                       this.requestMember.getId()));
        
        // 1. TodoTask 2개
        TodoTask.of(
            this.requestMember.getId(),
            taskCategory1.getId(),
            "test todo task 1",
            LocalDate.now(),
            null,
            null,
            null,
            null,
            null
        );
        // 2. DowithTask 2개
        // 3. DowithTaskConfirm 2개
        // 4. TaskCategory 2개
    }
    
    @Test
    @DisplayName("[SUCCESS] Task 목록 조회")
    void retrieveTasks1() {
    
    }
    
    
}

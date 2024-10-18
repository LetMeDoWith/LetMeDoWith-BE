package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCategoryService {
    
    private final TaskCategoryRepository taskCategoryRepository;
    
    // TODO: 이 메서드는 추후 캐싱하여 응답한다.
    public List<TaskCategory> getAllCommonCategory() {
        return taskCategoryRepository.getCategories(TaskCategoryCreationType.COMMON);
    }
    
    /**
     * 한 멤버가 조회 가능한 모든 카테고리를 조회한다.
     *
     * @param memberId 조회하려는 멤버의 id
     * @return 멤버가 생성한 개인 카테고리 + 공통 카테고리
     */
    public List<TaskCategory> getAllCategory(Long memberId) {
        List<TaskCategory> userCreatedCategories = taskCategoryRepository.getCategories(memberId);
        
        return Stream.concat(getAllCommonCategory().stream(),
                             userCreatedCategories.stream()).toList();
    }
    
}
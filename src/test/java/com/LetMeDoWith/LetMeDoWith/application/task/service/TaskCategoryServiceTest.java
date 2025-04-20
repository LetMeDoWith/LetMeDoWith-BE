package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskCategoryVO;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TaskCategoryServiceTest {
    
    // 테스트에 사용할 멤버 ID
    private static final Long TEST_MEMBER_ID = 1L;
    @Mock
    private TaskCategoryRepository taskCategoryRepository;
    @InjectMocks
    private TaskCategoryService taskCategoryService;
    private List<TaskCategory> mockUserCategories;
    private List<TaskCategoryVO> mockUserCategoryVOs;
    
    @BeforeEach
    void setUp() {
        // 유저가 생성한 TaskCategory 엔티티 3개를 임의로 만든다.
        mockUserCategories = List.of(
            new TaskCategory(100L,
                             "User 카테고리1",
                             Yn.TRUE,
                             TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                             "🔧",
                             TEST_MEMBER_ID),
            new TaskCategory(101L,
                             "User 카테고리2",
                             Yn.TRUE,
                             TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                             "📅",
                             TEST_MEMBER_ID),
            new TaskCategory(102L,
                             "User 카테고리3",
                             Yn.TRUE,
                             TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                             "📚",
                             TEST_MEMBER_ID)
        );
    }
    
    @Test
    @DisplayName("[SUCCESS] 모든 카테고리 조회")
    void testGetAllCategory() {
        // Given: taskCategoryRepository가 memberId로 TaskCategory 엔티티를 반환할 때, 유저의 카테고리를 모킹한다.
        when(taskCategoryRepository.getCategories(TEST_MEMBER_ID, Yn.TRUE)).thenReturn(
            mockUserCategories);
        
        // When: 모든 카테고리를 조회하는 메서드 호출
        List<TaskCategory> result = taskCategoryService.getAllCategory(TEST_MEMBER_ID);
        
        // Then: 결과 검증 (공통 카테고리와 유저 카테고리가 모두 포함되어야 함)
        assertThat(result)
            .containsAll(mockUserCategories);
        
    }
    
}
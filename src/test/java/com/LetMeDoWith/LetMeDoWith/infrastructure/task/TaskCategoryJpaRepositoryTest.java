package com.LetMeDoWith.LetMeDoWith.infrastructure.task;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.config.TestQueryDslConfig;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TaskCategoryJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Slf4j
@Import(TestQueryDslConfig.class)
public class TaskCategoryJpaRepositoryTest {

    @Autowired TestEntityManager entityManager;

    @Autowired private TaskCategoryJpaRepository taskCategoryJpaRepository;

    private TaskCategory singleUserCategory;
    private List<TaskCategory> multipleUserCategories;

    @BeforeEach
    void setUp() {
        // 단일 TaskCategory 엔티티: 단일 엔티티 저장 및 조회 테스트에 사용
        singleUserCategory =
                new TaskCategory(
                        null,
                        "User 단일 카테고리",
                        Yn.TRUE,
                        TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                        "🔧",
                        1L);

        // 다중 TaskCategory 엔티티: 여러 엔티티 저장 및 조회 테스트에 사용
        multipleUserCategories =
                List.of(
                        new TaskCategory(
                                null,
                                "User 카테고리1",
                                Yn.TRUE,
                                TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                                "🔧",
                                1L),
                        new TaskCategory(
                                null,
                                "User 카테고리2",
                                Yn.TRUE,
                                TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                                "📅",
                                1L),
                        new TaskCategory(
                                null,
                                "User 카테고리3",
                                Yn.TRUE,
                                TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                                "📚",
                                1L));
    }

    @Test
    @DisplayName("[SUCCESS] 유저의 카테고리를 저장한다.")
    void testSaveCategorySuccess() {
        // When: 카테고리를 저장하는 메서드 호출
        TaskCategory savedCategory = taskCategoryJpaRepository.save(singleUserCategory);

        // Then: 저장된 카테고리가 기대값과 동일한지 확인
        assertThat(savedCategory.getId()).isNotNull(); // ID가 자동 생성되었는지 확인
        assertThat(savedCategory.getTitle()).isEqualTo(singleUserCategory.getTitle());
        assertThat(savedCategory.getCreationType()).isEqualTo(singleUserCategory.getCreationType());
    }

    @Test
    @DisplayName("[SUCCESS] ID로 카테고리를 조회한다.")
    void testGetCategoryByIdSuccess() {
        // Given: TaskCategory 저장
        TaskCategory savedCategory = taskCategoryJpaRepository.save(singleUserCategory);

        // When: ID로 카테고리를 조회하는 메서드 호출
        Optional<TaskCategory> result = taskCategoryJpaRepository.findById(savedCategory.getId());

        // Then: 조회 결과가 기대값과 동일한지 확인
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedCategory.getId());
        assertThat(result.get().getTitle()).isEqualTo(savedCategory.getTitle());
    }

    @Test
    @DisplayName("[FAIL] 존재하지 않는 ID로 카테고리를 조회할 때 빈 값을 반환한다.")
    void testGetCategoryByIdFail() {
        // When: 존재하지 않는 ID로 카테고리를 조회하는 메서드 호출
        Optional<TaskCategory> result = taskCategoryJpaRepository.findById(999L);

        // Then: 조회 결과가 빈 값인지 확인
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 모든 카테고리를 조회한다.")
    void testGetAllCategoriesSuccess() {
        // Given: 3개의 TaskCategory 저장
        taskCategoryJpaRepository.saveAll(multipleUserCategories);

        // When: 모든 카테고리를 조회하는 메서드 호출
        List<TaskCategory> result = taskCategoryJpaRepository.findAll();

        // Then: 결과가 기대값과 동일한지 확인
        assertThat(result).hasSize(3 + 8); // 기 저장된 Common 카테고리 개수 포함
    }

    @Test
    @DisplayName("[SUCCESS] 유저 커스텀 카테고리를 조회한다.")
    void testGetUserCustomCategoriesSuccess() {
        // Given: 유저 커스텀 카테고리 저장
        taskCategoryJpaRepository.saveAll(multipleUserCategories);

        // When: 특정 홀더 ID로 유저 커스텀 카테고리를 조회하는 메서드 호출
        List<TaskCategory> result =
                taskCategoryJpaRepository.findAllByCategoryHolderIdAndCreationTypeAndIsActive(
                        1L, TaskCategory.TaskCategoryCreationType.USER_CUSTOM, Yn.TRUE);

        // Then: 결과가 기대값과 동일한지 확인
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCreationType())
                .isEqualTo(TaskCategory.TaskCategoryCreationType.USER_CUSTOM);
    }

    @Test
    @DisplayName("[SUCCESS] 특정 creationType에 따른 카테고리를 조회한다.")
    void testGetCategoriesByCreationTypeSuccess() {
        // Given: 2개의 공통 카테고리와 1개의 유저 커스텀 카테고리 저장
        taskCategoryJpaRepository.save(
                new TaskCategory(
                        null, "공통 카테고리1", Yn.TRUE, TaskCategory.TaskCategoryCreationType.COMMON, "📘", null));
        taskCategoryJpaRepository.save(
                new TaskCategory(
                        null, "공통 카테고리2", Yn.TRUE, TaskCategory.TaskCategoryCreationType.COMMON, "📚", null));
        taskCategoryJpaRepository.save(
                new TaskCategory(
                        null,
                        "User 카테고리1",
                        Yn.TRUE,
                        TaskCategory.TaskCategoryCreationType.USER_CUSTOM,
                        "📅",
                        1L));

        // When: creationType이 공통(COMMON)인 카테고리를 조회하는 메서드 호출
        List<TaskCategory> result =
                taskCategoryJpaRepository.findAllByCreationTypeAndIsActive(
                        TaskCategory.TaskCategoryCreationType.COMMON, Yn.TRUE);

        // Then: 결과가 기대값과 동일한지 확인
        assertThat(result).hasSize(2 + 8); // 공통 카테고리가 2 + 8(기 저장된 카테고리) 개인지 확인
        assertThat(result.get(0).getCreationType())
                .isEqualTo(TaskCategory.TaskCategoryCreationType.COMMON);
    }
}

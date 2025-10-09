-- V0.0.4__insert_task_domain_test_data.sql
-- Flyway 마이그레이션: 초기 데이터 삽입 (String Member ID 체계 적용)
-- 주의: 이 스크립트는 Member ID가 자동 생성(TSID)되는 새 스키마에 맞춰 초기 데이터를 다시 삽입합니다.
-- Member ID 참조 시에는 주로 nickname을 사용하여 조회합니다.

-- member 테이블 데이터 삽입


INSERT INTO task_summary (member_id, remained_dowith_task_count, remained_dowith_task_count_updated_at,
                          last_attendance_date, task_complete_level)
VALUES ((SELECT id FROM member WHERE nickname = 'a1'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a2'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a3'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a4'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a5'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a6'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a7'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a8'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a9'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a10'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a11'), 0, NOW(), NULL, '3'),
       ((SELECT id FROM member WHERE nickname = 'a12'), 0, NOW(), NULL, '3');

-- task_category 테이블 데이터 삽입
INSERT INTO task_category (create_at, updated_at, created_by, updated_by, title, active_yn,
                           creation_type, emoji, category_holder_id)
VALUES (NOW(), NOW(), 'admin', 'admin', '약속', 'Y', 'COMMON', '🙋‍♀️', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '시험', 'Y', 'COMMON', '🗓️', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '운동', 'Y', 'COMMON', '🦺', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '일상', 'Y', 'COMMON', '🏙️', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '공부', 'Y', 'COMMON', '📝', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '독서', 'Y', 'COMMON', '📘', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '작업', 'Y', 'COMMON', '👩‍💻', 'SYSTEM'),
       (NOW(), NOW(), 'admin', 'admin', '기타', 'Y', 'COMMON', '⏰', 'SYSTEM');



INSERT INTO dowith_task (id, member_id, title, status, date, start_time)
VALUES (1, 'test-member-id', '테스트 태스크', 'WAIT', CURRENT_DATE(),
        DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 10 MINUTE), '%H:%i:%s'));

-- 2. 테스트용 피드백 템플릿 생성 (id=1)
INSERT INTO task_feedback_template (id, emoji_url, title, description, is_active, create_at, updated_at, created_by,
                                    updated_by)
VALUES (1, 'http://test.emoji.url', '칭찬', '잘했어요!', 'Y', NOW(), NOW(), 'system', 'system');

-- 3. 테스트용 피드백 템플릿 메시지 생성 (id=1)
INSERT INTO task_feedback_template_message (id, task_feedback_template_id, message, language, create_at, updated_at,
                                            created_by, updated_by)
VALUES (1, 1, '정말 잘했어요!', 'KR', NOW(), NOW(), 'system', 'system');


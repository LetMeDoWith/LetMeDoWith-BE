-- V0.3.1__insert_feedback_test_data.sql
-- 통합 테스트용 피드백 관련 데이터 삽입

-- 1. 테스트용 두윗 태스크 생성 (id=1)
INSERT INTO dowith_task (id, member_id, title, status, date, start_time)
VALUES (1, 'test-member-id', '테스트 태스크', 'WAIT', CURRENT_DATE(),
        DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 10 MINUTE), '%H:%i:%s'));

-- 2. 테스트용 피드백 템플릿 생성 (id=1)
INSERT INTO task_feedback_template (id, emoji_url, title, description, is_active, create_at, updated_at, created_by,
                                    updated_by)
VALUES (1, 'http://test.emoji.url', '잔소리1', '잔소리1', 'Y', NOW(), NOW(), 'system', 'system'),
       (2, 'http://test.emoji.url', '잔소리2', '잔소리2', 'Y', NOW(), NOW(), 'system', 'system'),
       (3, 'http://test.emoji.url', '잔소리3', '잔소리3', 'Y', NOW(), NOW(), 'system', 'system'),
       (4, 'http://test.emoji.url', '잔소리4', '잔소리4', 'Y', NOW(), NOW(), 'system', 'system');

-- 3. 테스트용 피드백 템플릿 메시지 생성 (id=1)
INSERT INTO task_feedback_template_message (id, task_feedback_template_id, message, language, create_at, updated_at,
                                            created_by, updated_by)
VALUES (1, 1, '두윗 안 하시는 거, 다 - 들켰습니다', 'KR', NOW(), NOW(), 'system', 'system'),
       (2, 2, '두윗 미루기 대회 나가시면 1등 각이에요', 'KR', NOW(), NOW(), 'system', 'system'),
       (3, 3, '행은 안 하고 잔소리만 모으시나요?', 'KR', NOW(), NOW(), 'system', 'system'),
       (4, 4, '윗 안 한 사람 특: 나중에 하고 후회함 ㅎ', 'KR', NOW(), NOW(), 'system', 'system');

-- 4. 테스트용 잔소리(피드백) 생성 (id=1)
-- INSERT INTO dowith_task_feedback (id, task_feedback_template_id, dowith_task_id, sender_id, receiver_id, is_checked, create_at, updated_at, created_by, updated_by)
-- VALUES (1, 1, 1, 'test-member-id', 'receiver-test-id', 'N', NOW(), NOW(), 'system', 'system'); 
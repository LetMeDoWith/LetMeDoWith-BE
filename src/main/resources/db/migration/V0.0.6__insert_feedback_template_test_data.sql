-- 잔소리 템플릿 테스트 데이터 초기화 후 id=1~4 순서로 재적재
DELETE FROM task_feedback_template_message;
DELETE FROM task_feedback_template;

INSERT INTO task_feedback_template (id, emoji_url, title, description, is_active, notification_template_code,
                                    created_at, updated_at, created_by, updated_by)
VALUES (1, 'https://letmedowith-dev.s3.ap-northeast-2.amazonaws.com/static/images/feedback_templates/FEEDBACK_TEMPLATE_1.png',
        'FEEDBACK_TEMPLATE_A', '{{receiverNickname}}님아;; 아직도 안했구나?🤨', 'Y', 'FEEDBACK_RECEIVED_1', NOW(), NOW(), 'system', 'system'),
       (2, 'https://letmedowith-dev.s3.ap-northeast-2.amazonaws.com/static/images/feedback_templates/FEEDBACK_TEMPLATE_2.png',
        'FEEDBACK_TEMPLATE_B', '혹시 잡도리 수집중이니?🤓', 'Y', 'FEEDBACK_RECEIVED_2', NOW(), NOW(), 'system', 'system'),
       (3, 'https://letmedowith-dev.s3.ap-northeast-2.amazonaws.com/static/images/feedback_templates/FEEDBACK_TEMPLATE_3.png',
        'FEEDBACK_TEMPLATE_C', '{{receiverNickname}}, 발등에 불 떨어지고 나서야 하려고?🔥', 'Y', 'FEEDBACK_RECEIVED_3', NOW(), NOW(), 'system', 'system'),
       (4, 'https://letmedowith-dev.s3.ap-northeast-2.amazonaws.com/static/images/feedback_templates/FEEDBACK_TEMPL_4.png',
        'FEEDBACK_TEMPLATE_D', '님 그러다 또 후회함😕', 'Y', 'FEEDBACK_RECEIVED_4', NOW(), NOW(), 'system', 'system');

-- 잔소리 템플릿 메시지 테스트 데이터 (id=1~4, name은 줄바꿈 단위를 "|"로 구분)
INSERT INTO task_feedback_template_message (id, task_feedback_template_id, name, message, language,
                                            created_at, updated_at, created_by, updated_by)
VALUES (1, 1, '아직도|안하네', '{{receiverNickname}}님아;; 아직도 안했구나?🤨', 'KR', NOW(), NOW(), 'system', 'system'),
       (2, 2, '잡도리|수집중', '혹시 잡도리 수집중이니?🤓', 'KR', NOW(), NOW(), 'system', 'system'),
       (3, 3, '발등|튀김됨', '{{receiverNickname}}, 발등에 불 떨어지고 나서야 하려고?🔥', 'KR', NOW(), NOW(), 'system', 'system'),
       (4, 4, '님 또|후회함', '님 그러다 또 후회함😕', 'KR', NOW(), NOW(), 'system', 'system');

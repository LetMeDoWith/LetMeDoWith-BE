INSERT INTO letmedowith_app.task_category (create_at, updated_at, created_by, updated_by, title,
                                           creation_type, emoji, category_holder_id)
VALUES (NOW(), NOW(), 'admin', 'admin', '약속', 'COMMON', '🙋‍♀️', NULL),
       (NOW(), NOW(), 'admin', 'admin', '시험', 'COMMON', '🗓️', NULL),
       (NOW(), NOW(), 'admin', 'admin', '운동', 'COMMON', '🦺', NULL),
       (NOW(), NOW(), 'admin', 'admin', '일상', 'COMMON', '🏙️', NULL),
       (NOW(), NOW(), 'admin', 'admin', '공부', 'COMMON', '📝', NULL),
       (NOW(), NOW(), 'admin', 'admin', '독서', 'COMMON', '📘', NULL),
       (NOW(), NOW(), 'admin', 'admin', '작업', 'COMMON', '👩‍💻', NULL),
       (NOW(), NOW(), 'admin', 'admin', '기타', 'COMMON', '⏰', NULL);
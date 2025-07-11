-- V0.3.0__create_feedback_tables.sql
-- feedback 기능 도입을 위한 테이블 생성

-- 1. task_feedback_template 테이블
CREATE TABLE task_feedback_template
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    create_at   timestamp             NULL,
    updated_at  timestamp             NULL,
    created_by  VARCHAR(255)          NULL,
    updated_by  VARCHAR(255)          NULL,
    emoji_url   VARCHAR(255)          NOT NULL,
    title       VARCHAR(255)          NOT NULL,
    description VARCHAR(255)          NOT NULL,
    is_active   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_task_feedback_template PRIMARY KEY (id)
);

-- 2. task_feedback_template_message 테이블
CREATE TABLE task_feedback_template_message
(
    id                        BIGINT AUTO_INCREMENT NOT NULL,
    create_at                 timestamp             NULL,
    updated_at                timestamp             NULL,
    created_by                VARCHAR(255)          NULL,
    updated_by                VARCHAR(255)          NULL,
    task_feedback_template_id BIGINT                NOT NULL,
    message                   VARCHAR(255)          NOT NULL,
    language                  VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_task_feedback_template_message PRIMARY KEY (id)
);

-- 3. dowith_task_feedback 테이블
CREATE TABLE dowith_task_feedback
(
    id                        BIGINT AUTO_INCREMENT NOT NULL,
    create_at                 timestamp             NULL,
    updated_at                timestamp             NULL,
    created_by                VARCHAR(255)          NULL,
    updated_by                VARCHAR(255)          NULL,
    task_feedback_template_id BIGINT                NOT NULL,
    dowith_task_id            BIGINT                NOT NULL,
    sender_member_id          VARCHAR(26)           NOT NULL,
    receiver_member_id        VARCHAR(26)           NOT NULL,
    is_checked                VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_dowith_task_feedback PRIMARY KEY (id)
);

-- Unique Constraints
ALTER TABLE dowith_task_feedback
    ADD CONSTRAINT uc_dowith_task_feedback_dowith_task UNIQUE (dowith_task_id);

-- Foreign Key Constraints


ALTER TABLE task_feedback_template_message
    ADD CONSTRAINT FK_TASK_FEEDBACK_TEMPLATE_MESSAGE_ON_TASK_FEEDBACK_TEMPLATE FOREIGN KEY (task_feedback_template_id) REFERENCES task_feedback_template (id);

ALTER TABLE dowith_task_feedback
    ADD CONSTRAINT FK_DOWITH_TASK_FEEDBACK_ON_TASK_FEEDBACK_TEMPLATE FOREIGN KEY (task_feedback_template_id) REFERENCES task_feedback_template (id);

ALTER TABLE dowith_task_feedback
    ADD CONSTRAINT FK_DOWITH_TASK_FEEDBACK_ON_DOWITH_TASK FOREIGN KEY (dowith_task_id) REFERENCES dowith_task (id); 
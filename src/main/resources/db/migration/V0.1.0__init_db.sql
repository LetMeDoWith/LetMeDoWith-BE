CREATE TABLE badge
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    create_at    timestamp             NULL,
    updated_at   timestamp             NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    status       VARCHAR(255)          NOT NULL,
    name         VARCHAR(255)          NOT NULL,
    description  VARCHAR(255)          NULL,
    acquire_hint VARCHAR(255)          NULL,
    image_url    VARCHAR(255)          NULL,
    sort_order   INT                   NULL,
    CONSTRAINT pk_badge PRIMARY KEY (id)
);

CREATE TABLE dev_refresh_token
(
    token        VARCHAR(512) NOT NULL,
    access_token VARCHAR(800) NULL,
    member_id    VARCHAR(255) NULL,
    user_agent   VARCHAR(255) NULL,
    expire_at    timestamp    NULL,
    CONSTRAINT pk_dev_refresh_token PRIMARY KEY (token)
);

CREATE TABLE dowith_task
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    create_at              timestamp             NULL,
    updated_at             timestamp             NULL,
    created_by             VARCHAR(255)          NULL,
    updated_by             VARCHAR(255)          NULL,
    member_id              VARCHAR(26)           NOT NULL,
    task_category_id       BIGINT                NULL,
    title                  VARCHAR(255)          NOT NULL,
    status                 VARCHAR(255)          NOT NULL,
    date                   date                  NOT NULL,
    start_time             time                  NULL,
    success_at             timestamp             NULL,
    complete_at            timestamp             NULL,
    dowith_task_routine_id BIGINT                NULL,
    CONSTRAINT pk_dowith_task PRIMARY KEY (id)
);

CREATE TABLE dowith_task_success
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    create_at      timestamp             NULL,
    updated_at     timestamp             NULL,
    created_by     VARCHAR(255)          NULL,
    updated_by     VARCHAR(255)          NULL,
    dowith_task_id BIGINT                NOT NULL,
    image_url      VARCHAR(255)          NULL,
    CONSTRAINT pk_dowith_task_success PRIMARY KEY (id)
);

CREATE TABLE dowith_task_routine
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    range_start_date    date                  NOT NULL,
    range_end_date      date                  NOT NULL,
    cycle               VARCHAR(20)           NOT NULL,
    pattern             text                  NOT NULL,
    exclude_holidays_yn BOOLEAN               NOT NULL,
    exclude_dates       text                  NULL,
    create_at           timestamp             NULL,
    updated_at          timestamp             NULL,
    created_by          VARCHAR(255)          NULL,
    updated_by          VARCHAR(255)          NULL,
    CONSTRAINT pk_dowith_task_routine PRIMARY KEY (id)
);

CREATE TABLE holiday
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    create_at    timestamp             NULL,
    updated_at   timestamp             NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    country_code VARCHAR(255)          NOT NULL,
    date         date                  NOT NULL,
    name         VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_holiday PRIMARY KEY (id)
);

CREATE TABLE member
(
    id                VARCHAR(26)  NOT NULL,
    create_at         timestamp    NULL,
    updated_at        timestamp    NULL,
    created_by        VARCHAR(255) NULL,
    updated_by        VARCHAR(255) NULL,
    subject           VARCHAR(255) NULL,
    status            VARCHAR(255) NOT NULL,
    nickname          VARCHAR(255) NULL,
    self_description  VARCHAR(255) NULL,
    gender            VARCHAR(2)   NULL,
    date_of_birth     date         NULL,
    type              VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255) NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE member_alarm_setting
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    create_at     timestamp             NULL,
    updated_at    timestamp             NULL,
    created_by    VARCHAR(255)          NULL,
    updated_by    VARCHAR(255)          NULL,
    member_id     VARCHAR(26)           NOT NULL,
    base_alarm_yn BIT(1)                NOT NULL,
    todo_bot_yn   BIT(1)                NOT NULL,
    feedback_yn   BIT(1)                NOT NULL,
    marketing_yn  BIT(1)                NOT NULL,
    CONSTRAINT pk_member_alarm_setting PRIMARY KEY (id)
);

CREATE TABLE member_badge
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    create_at  timestamp             NULL,
    updated_at timestamp             NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    member_id  VARCHAR(26)           NULL,
    badge_id   BIGINT                NOT NULL,
    main_yn    VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_member_badge PRIMARY KEY (id)
);

CREATE TABLE member_follow
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    create_at    timestamp             NULL,
    updated_at   timestamp             NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    follower_id  VARCHAR(26)           NOT NULL,
    following_id VARCHAR(26)           NOT NULL,
    CONSTRAINT pk_member_follow PRIMARY KEY (id)
);

CREATE TABLE member_social_account
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    create_at  timestamp             NULL,
    updated_at timestamp             NULL,
    created_by VARCHAR(255)          NULL,
    updated_by VARCHAR(255)          NULL,
    member_id  VARCHAR(26)           NOT NULL,
    provider   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_member_social_account PRIMARY KEY (id)
);

CREATE TABLE member_status_history
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    create_at         timestamp             NULL,
    updated_at        timestamp             NULL,
    created_by        VARCHAR(255)          NULL,
    updated_by        VARCHAR(255)          NULL,
    member_id         VARCHAR(26)           NOT NULL,
    status            VARCHAR(255)          NOT NULL,
    status_changed_at timestamp             NOT NULL,
    status_end_at     timestamp             NOT NULL,
    CONSTRAINT pk_member_status_history PRIMARY KEY (id)
);

CREATE TABLE member_term_agree
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    create_at      timestamp             NULL,
    updated_at     timestamp             NULL,
    created_by     VARCHAR(255)          NULL,
    updated_by     VARCHAR(255)          NULL,
    member_id      VARCHAR(26)           NOT NULL,
    terms_of_agree BIT(1)                NOT NULL,
    privacy        BIT(1)                NOT NULL,
    advertisement  BIT(1)                NOT NULL,
    CONSTRAINT pk_member_term_agree PRIMARY KEY (id)
);

CREATE TABLE task_category
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    create_at          timestamp             NULL,
    updated_at         timestamp             NULL,
    created_by         VARCHAR(255)          NULL,
    updated_by         VARCHAR(255)          NULL,
    title              VARCHAR(255)          NOT NULL,
    active_yn          VARCHAR(255)          NOT NULL,
    creation_type      VARCHAR(255)          NOT NULL,
    emoji              VARCHAR(255)          NOT NULL,
    category_holder_id VARCHAR(26)           NULL,
    CONSTRAINT pk_task_category PRIMARY KEY (id)
);

CREATE TABLE todo_task
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    create_at            timestamp             NULL,
    updated_at           timestamp             NULL,
    created_by           VARCHAR(255)          NULL,
    updated_by           VARCHAR(255)          NULL,
    member_id            VARCHAR(26)           NOT NULL,
    task_category_id     BIGINT                NULL,
    title                VARCHAR(255)          NOT NULL,
    status               VARCHAR(255)          NOT NULL,
    date                 date                  NOT NULL,
    start_time           time                  NULL,
    todo_task_routine_id BIGINT                NULL,
    CONSTRAINT pk_todo_task PRIMARY KEY (id)
);

CREATE TABLE todo_task_routine
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    dates               text                  NULL,
    cycle               VARCHAR(20)           NOT NULL DEFAULT 'NONE',
    pattern             text                  NULL,
    is_exclude_holidays BOOLEAN               NOT NULL DEFAULT FALSE,
    create_at           timestamp             NULL,
    updated_at          timestamp             NULL,
    created_by          VARCHAR(255)          NULL,
    updated_by          VARCHAR(255)          NULL,

    CONSTRAINT pk_todo_task_routine PRIMARY KEY (id)
);

CREATE TABLE task_summary
(
    id                                    BIGINT AUTO_INCREMENT NOT NULL,
    member_id                             VARCHAR(26)           NOT NULL,
    remained_dowith_task_count            INT                   NOT NULL DEFAULT 0,
    remained_dowith_task_count_updated_at timestamp             NULL,
    last_attendance_date                  date                  NULL,
    task_complete_level                   VARCHAR(10)           NOT NULL DEFAULT 'GOOD',
    create_at                             timestamp             NULL,
    updated_at                            timestamp             NULL,
    created_by                            VARCHAR(255)          NULL,
    updated_by                            VARCHAR(255)          NULL,
    CONSTRAINT pk_task_summary PRIMARY KEY (id)
);

ALTER TABLE dowith_task_success
    ADD CONSTRAINT uc_dowith_task_success_dowith_task UNIQUE (dowith_task_id);

ALTER TABLE member_alarm_setting
    ADD CONSTRAINT uc_member_alarm_setting_member UNIQUE (member_id);

ALTER TABLE member_term_agree
    ADD CONSTRAINT uc_member_term_agree_member UNIQUE (member_id);

ALTER TABLE dowith_task_success
    ADD CONSTRAINT FK_DOWITH_TASK_SUCCESS_ON_DOWITH_TASK FOREIGN KEY (dowith_task_id) REFERENCES dowith_task (id);

ALTER TABLE dowith_task
    ADD CONSTRAINT FK_DOWITH_TASK_ON_DOWITH_TASK_ROUTINE FOREIGN KEY (dowith_task_routine_id) REFERENCES dowith_task_routine (id);

ALTER TABLE member_alarm_setting
    ADD CONSTRAINT FK_MEMBER_ALARM_SETTING_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member_badge
    ADD CONSTRAINT FK_MEMBER_BADGE_ON_BADGE FOREIGN KEY (badge_id) REFERENCES badge (id);

ALTER TABLE todo_task
    ADD CONSTRAINT FK_TODO_TASK_ON_TODO_TASK_ROUTINE FOREIGN KEY (todo_task_routine_id) REFERENCES todo_task_routine (id);

ALTER TABLE member_follow
    ADD CONSTRAINT FK_MEMBER_FOLLOW_ON_FOLLOWER FOREIGN KEY (follower_id) REFERENCES member (id);

ALTER TABLE member_follow
    ADD CONSTRAINT FK_MEMBER_FOLLOW_ON_FOLLOWING FOREIGN KEY (following_id) REFERENCES member (id);

ALTER TABLE member_social_account
    ADD CONSTRAINT FK_MEMBER_SOCIAL_ACCOUNT_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member_status_history
    ADD CONSTRAINT FK_MEMBER_STATUS_HISTORY_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE member_term_agree
    ADD CONSTRAINT FK_MEMBER_TERM_AGREE_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE task_summary
    ADD CONSTRAINT FK_TASK_SUMMARY_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);


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

-- Foreign Key Constraints


ALTER TABLE task_feedback_template_message
    ADD CONSTRAINT FK_TASK_FEEDBACK_TEMPLATE_MESSAGE_ON_TASK_FEEDBACK_TEMPLATE FOREIGN KEY (task_feedback_template_id) REFERENCES task_feedback_template (id);

ALTER TABLE dowith_task_feedback
    ADD CONSTRAINT FK_DOWITH_TASK_FEEDBACK_ON_TASK_FEEDBACK_TEMPLATE FOREIGN KEY (task_feedback_template_id) REFERENCES task_feedback_template (id);

ALTER TABLE dowith_task_feedback
    ADD CONSTRAINT FK_DOWITH_TASK_FEEDBACK_ON_DOWITH_TASK FOREIGN KEY (dowith_task_id) REFERENCES dowith_task (id);


CREATE TABLE notification
(
    id                         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id                  VARCHAR(26)  NOT NULL,
    title                      TEXT         NOT NULL,
    body                       TEXT         NOT NULL,
    deep_link                  TEXT,
    confirmed_yn               VARCHAR(1)   NOT NULL, -- ENUM 대체
    confirm_date_time          DATETIME,
    notification_template_code VARCHAR(50),
    create_at                  timestamp    NULL,
    updated_at                 timestamp    NULL,
    created_by                 VARCHAR(255) NULL,
    updated_by                 VARCHAR(255) NULL
);

CREATE TABLE notification_token
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  VARCHAR(26)  NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expired_yn VARCHAR(1)   NOT NULL, -- ENUM 대체
    create_at  timestamp    NULL,
    updated_at timestamp    NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL
);

CREATE TABLE notification_template
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    title         TEXT         NOT NULL,
    body          TEXT         NOT NULL,
    app_deep_link TEXT,
    create_at     timestamp    NULL,
    updated_at    timestamp    NULL,
    created_by    VARCHAR(255) NULL,
    updated_by    VARCHAR(255) NULL
);
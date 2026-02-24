CREATE TABLE `badge`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6)  DEFAULT NULL,
    `created_by`   varchar(255) DEFAULT NULL,
    `updated_at`   datetime(6)  DEFAULT NULL,
    `updated_by`   varchar(255) DEFAULT NULL,
    `acquire_hint` varchar(255) DEFAULT NULL,
    `status`       varchar(255) NOT NULL,
    `description`  varchar(255) DEFAULT NULL,
    `image_url`    varchar(255) DEFAULT NULL,
    `name`         varchar(255) NOT NULL,
    `sort_order`   int          DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `dev_refresh_token`
(
    `token`        varchar(255) NOT NULL,
    `access_token` varchar(255) DEFAULT NULL,
    `expire_at`    datetime(6)  DEFAULT NULL,
    `member_id`    varchar(255) DEFAULT NULL,
    `user_agent`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `dowith_task_routine`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `created_at`          datetime(6)  DEFAULT NULL,
    `created_by`          varchar(255) DEFAULT NULL,
    `updated_at`          datetime(6)  DEFAULT NULL,
    `updated_by`          varchar(255) DEFAULT NULL,
    `cycle`               varchar(20) NOT NULL,
    `exclude_holidays_yn` bit(1)       DEFAULT NULL,
    `pattern`             varchar(255) DEFAULT NULL,
    `range_end_date`      date        NOT NULL,
    `range_start_date`    date        NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `dowith_task`
(
    `id`                     bigint       NOT NULL AUTO_INCREMENT,
    `created_at`             datetime(6)  DEFAULT NULL,
    `created_by`             varchar(255) DEFAULT NULL,
    `updated_at`             datetime(6)  DEFAULT NULL,
    `updated_by`             varchar(255) DEFAULT NULL,
    `complete_at`            datetime(6)  DEFAULT NULL,
    `date`                   date         NOT NULL,
    `member_id`              varchar(26)  NOT NULL,
    `start_time`             time(6)      DEFAULT NULL,
    `status`                 varchar(255) NOT NULL,
    `success_at`             datetime(6)  DEFAULT NULL,
    `task_category_id`       bigint       DEFAULT NULL,
    `title`                  varchar(255) NOT NULL,
    `dowith_task_routine_id` bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKkjm1t4jam94wtf486023jdaam` (`dowith_task_routine_id`),
    CONSTRAINT `FKkjm1t4jam94wtf486023jdaam` FOREIGN KEY (`dowith_task_routine_id`) REFERENCES `dowith_task_routine` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `dowith_task_feedback`
(
    `id`                        bigint       NOT NULL AUTO_INCREMENT,
    `created_at`                datetime(6)  DEFAULT NULL,
    `created_by`                varchar(255) DEFAULT NULL,
    `updated_at`                datetime(6)  DEFAULT NULL,
    `updated_by`                varchar(255) DEFAULT NULL,
    `dowith_task_id`            bigint       NOT NULL,
    `is_checked`                varchar(255) NOT NULL,
    `receiver_member_id`        varchar(255) NOT NULL,
    `sender_member_id`          varchar(255) NOT NULL,
    `task_feedback_template_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `dowith_task_like`
(
    `id`             bigint      NOT NULL AUTO_INCREMENT,
    `created_at`     datetime(6)  DEFAULT NULL,
    `created_by`     varchar(255) DEFAULT NULL,
    `updated_at`     datetime(6)  DEFAULT NULL,
    `updated_by`     varchar(255) DEFAULT NULL,
    `member_id`      varchar(26) NOT NULL,
    `dowith_task_id` bigint      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dowith_task_like_1` (`member_id`, `dowith_task_id`),
    KEY `FKe3yxe45m46uwf8fhgul36un9v` (`dowith_task_id`),
    CONSTRAINT `FKe3yxe45m46uwf8fhgul36un9v` FOREIGN KEY (`dowith_task_id`) REFERENCES `dowith_task` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `dowith_task_success`
(
    `id`             bigint NOT NULL AUTO_INCREMENT,
    `created_at`     datetime(6)  DEFAULT NULL,
    `created_by`     varchar(255) DEFAULT NULL,
    `updated_at`     datetime(6)  DEFAULT NULL,
    `updated_by`     varchar(255) DEFAULT NULL,
    `image_url`      varchar(255) DEFAULT NULL,
    `dowith_task_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKg8cantau9xpnh63og64mn8svu` (`dowith_task_id`),
    CONSTRAINT `FKg8cantau9xpnh63og64mn8svu` FOREIGN KEY (`dowith_task_id`) REFERENCES `dowith_task` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `holiday`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6)  DEFAULT NULL,
    `created_by`   varchar(255) DEFAULT NULL,
    `updated_at`   datetime(6)  DEFAULT NULL,
    `updated_by`   varchar(255) DEFAULT NULL,
    `country_code` varchar(255) NOT NULL,
    `date`         date         NOT NULL,
    `name`         varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member`
(
    `id`                varchar(26)  NOT NULL,
    `created_at`        datetime(6)  DEFAULT NULL,
    `created_by`        varchar(255) DEFAULT NULL,
    `updated_at`        datetime(6)  DEFAULT NULL,
    `updated_by`        varchar(255) DEFAULT NULL,
    `date_of_birth`     date         DEFAULT NULL,
    `gender`            varchar(2)   DEFAULT NULL,
    `nickname`          varchar(255) DEFAULT NULL,
    `profile_image_url` varchar(255) DEFAULT NULL,
    `self_description`  varchar(255) DEFAULT NULL,
    `status`            varchar(255) NOT NULL,
    `subject`           varchar(255) DEFAULT NULL,
    `type`              varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member_alarm_setting`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  DEFAULT NULL,
    `created_by`    varchar(255) DEFAULT NULL,
    `updated_at`    datetime(6)  DEFAULT NULL,
    `updated_by`    varchar(255) DEFAULT NULL,
    `base_alarm_yn` bit(1)      NOT NULL,
    `feedback_yn`   bit(1)      NOT NULL,
    `marketing_yn`  bit(1)      NOT NULL,
    `todo_bot_yn`   bit(1)      NOT NULL,
    `member_id`     varchar(26) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_3fqfed76e6nv6je3yj98b7yag` (`member_id`),
    CONSTRAINT `FKj6uf0pevg5lu24k0fe5j1e38p` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member_badge`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6)  DEFAULT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `main_yn`    varchar(255) NOT NULL,
    `member_id`  varchar(26)  DEFAULT NULL,
    `badge_id`   bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK39altb7wsyx1gyx3m492wf2kb` (`badge_id`),
    CONSTRAINT `FK39altb7wsyx1gyx3m492wf2kb` FOREIGN KEY (`badge_id`) REFERENCES `badge` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member_follow`
(
    `id`           bigint      NOT NULL AUTO_INCREMENT,
    `created_at`   datetime(6)  DEFAULT NULL,
    `created_by`   varchar(255) DEFAULT NULL,
    `updated_at`   datetime(6)  DEFAULT NULL,
    `updated_by`   varchar(255) DEFAULT NULL,
    `follower_id`  varchar(26) NOT NULL,
    `following_id` varchar(26) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKrshygjds7c4j7prtkkok4ax6p` (`follower_id`),
    KEY `FK1wlcnyo4j7gl8oxjje8m27119` (`following_id`),
    CONSTRAINT `FK1wlcnyo4j7gl8oxjje8m27119` FOREIGN KEY (`following_id`) REFERENCES `member` (`id`),
    CONSTRAINT `FKrshygjds7c4j7prtkkok4ax6p` FOREIGN KEY (`follower_id`) REFERENCES `member` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member_social_account`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6)  DEFAULT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `provider`   varchar(255) NOT NULL,
    `member_id`  varchar(26)  NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK3f6sh5arekj1s8ta6j8g6ojtx` (`member_id`),
    CONSTRAINT `FK3f6sh5arekj1s8ta6j8g6ojtx` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member_status_history`
(
    `id`                bigint       NOT NULL AUTO_INCREMENT,
    `created_at`        datetime(6)  DEFAULT NULL,
    `created_by`        varchar(255) DEFAULT NULL,
    `updated_at`        datetime(6)  DEFAULT NULL,
    `updated_by`        varchar(255) DEFAULT NULL,
    `status`            varchar(255) NOT NULL,
    `status_changed_at` datetime(6)  NOT NULL,
    `status_end_at`     datetime(6)  NOT NULL,
    `member_id`         varchar(26)  NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK2oi1yq1grh5gq36u5w5rakqau` (`member_id`),
    CONSTRAINT `FK2oi1yq1grh5gq36u5w5rakqau` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `member_term_agree`
(
    `id`             bigint      NOT NULL AUTO_INCREMENT,
    `created_at`     datetime(6)  DEFAULT NULL,
    `created_by`     varchar(255) DEFAULT NULL,
    `updated_at`     datetime(6)  DEFAULT NULL,
    `updated_by`     varchar(255) DEFAULT NULL,
    `advertisement`  bit(1)      NOT NULL,
    `privacy`        bit(1)      NOT NULL,
    `terms_of_agree` bit(1)      NOT NULL,
    `member_id`      varchar(26) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_qhgrwea9j7tlelvr8tcu4lxfn` (`member_id`),
    CONSTRAINT `FK27udwntf6y3danhk4kf00ifyt` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `notification`
(
    `id`                         bigint       NOT NULL AUTO_INCREMENT,
    `created_at`                 datetime(6)  DEFAULT NULL,
    `created_by`                 varchar(255) DEFAULT NULL,
    `updated_at`                 datetime(6)  DEFAULT NULL,
    `updated_by`                 varchar(255) DEFAULT NULL,
    `body`                       text         NOT NULL,
    `confirm_date_time`          datetime(6)  DEFAULT NULL,
    `deep_link`                  text,
    `confirmed_yn`               varchar(255) NOT NULL,
    `member_id`                  varchar(255) NOT NULL,
    `notification_template_code` varchar(255) DEFAULT NULL,
    `title`                      text         NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `notification_template`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `created_at`    datetime(6)  DEFAULT NULL,
    `created_by`    varchar(255) DEFAULT NULL,
    `updated_at`    datetime(6)  DEFAULT NULL,
    `updated_by`    varchar(255) DEFAULT NULL,
    `app_deep_link` text,
    `body`          text         NOT NULL,
    `code`          varchar(255) NOT NULL,
    `title`         text         NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_78ljxu1rtyj29qj8a3bao9bfx` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `notification_token`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6)  DEFAULT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `expired_yn` varchar(255) NOT NULL,
    `member_id`  varchar(255) NOT NULL,
    `token`      varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `ranking_topic`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)  DEFAULT NULL,
    `created_by`  varchar(255) DEFAULT NULL,
    `updated_at`  datetime(6)  DEFAULT NULL,
    `updated_by`  varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `title`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `ranking`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT,
    `created_at`       datetime(6)  DEFAULT NULL,
    `created_by`       varchar(255) DEFAULT NULL,
    `updated_at`       datetime(6)  DEFAULT NULL,
    `updated_by`       varchar(255) DEFAULT NULL,
    `current_rank`     bigint      NOT NULL,
    `member_id`        varchar(26) NOT NULL,
    `previous_rank`    bigint       DEFAULT NULL,
    `year`             int         NOT NULL,
    `month`            int         NOT NULL,
    `week`             int         NOT NULL,
    `ranking_topic_id` bigint      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ranking_1` (`ranking_topic_id`, `member_id`),
    CONSTRAINT `FKggr9gbi0wjxwekbl0mpnrhm99` FOREIGN KEY (`ranking_topic_id`) REFERENCES `ranking_topic` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `task_category`
(
    `id`                 bigint       NOT NULL AUTO_INCREMENT,
    `created_at`         datetime(6)  DEFAULT NULL,
    `created_by`         varchar(255) DEFAULT NULL,
    `updated_at`         datetime(6)  DEFAULT NULL,
    `updated_by`         varchar(255) DEFAULT NULL,
    `category_holder_id` varchar(26)  DEFAULT NULL,
    `creation_type`      varchar(255) NOT NULL,
    `emoji`              varchar(255) NOT NULL,
    `active_yn`          varchar(255) NOT NULL,
    `title`              varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `task_feedback_template`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `created_at`  datetime(6)  DEFAULT NULL,
    `created_by`  varchar(255) DEFAULT NULL,
    `updated_at`  datetime(6)  DEFAULT NULL,
    `updated_by`  varchar(255) DEFAULT NULL,
    `description` varchar(255) NOT NULL,
    `emoji_url`   varchar(255) NOT NULL,
    `is_active`   varchar(255) NOT NULL,
    `title`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `task_feedback_template_message`
(
    `id`                        bigint       NOT NULL AUTO_INCREMENT,
    `created_at`                datetime(6)  DEFAULT NULL,
    `created_by`                varchar(255) DEFAULT NULL,
    `updated_at`                datetime(6)  DEFAULT NULL,
    `updated_by`                varchar(255) DEFAULT NULL,
    `language`                  varchar(255) NOT NULL,
    `message`                   varchar(255) NOT NULL,
    `task_feedback_template_id` bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK2xw1gd0ww0s08iv3lo6qat0oo` (`task_feedback_template_id`),
    CONSTRAINT `FK2xw1gd0ww0s08iv3lo6qat0oo` FOREIGN KEY (`task_feedback_template_id`) REFERENCES `task_feedback_template` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `task_summary`
(
    `id`                                    bigint       NOT NULL AUTO_INCREMENT,
    `created_at`                            datetime(6)  DEFAULT NULL,
    `created_by`                            varchar(255) DEFAULT NULL,
    `updated_at`                            datetime(6)  DEFAULT NULL,
    `updated_by`                            varchar(255) DEFAULT NULL,
    `last_attendance_date`                  date         DEFAULT NULL,
    `member_id`                             varchar(26)  NOT NULL,
    `remained_dowith_task_count`            int          NOT NULL,
    `remained_dowith_task_count_updated_at` datetime(6)  DEFAULT NULL,
    `task_complete_level`                   varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `todo_task_routine`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `created_at`          datetime(6)  DEFAULT NULL,
    `created_by`          varchar(255) DEFAULT NULL,
    `updated_at`          datetime(6)  DEFAULT NULL,
    `updated_by`          varchar(255) DEFAULT NULL,
    `cycle`               varchar(20) NOT NULL,
    `exclude_holidays_yn` bit(1)       DEFAULT NULL,
    `pattern`             varchar(255) DEFAULT NULL,
    `range_end_date`      date        NOT NULL,
    `range_start_date`    date        NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `todo_task`
(
    `id`                   bigint       NOT NULL AUTO_INCREMENT,
    `created_at`           datetime(6)  DEFAULT NULL,
    `created_by`           varchar(255) DEFAULT NULL,
    `updated_at`           datetime(6)  DEFAULT NULL,
    `updated_by`           varchar(255) DEFAULT NULL,
    `date`                 date         NOT NULL,
    `member_id`            varchar(26)  NOT NULL,
    `start_time`           time(6)      DEFAULT NULL,
    `status`               varchar(255) NOT NULL,
    `task_category_id`     bigint       DEFAULT NULL,
    `title`                varchar(255) NOT NULL,
    `todo_task_routine_id` bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKq9txtlns4mgoyh11t1jqumpme` (`todo_task_routine_id`),
    CONSTRAINT `FKq9txtlns4mgoyh11t1jqumpme` FOREIGN KEY (`todo_task_routine_id`) REFERENCES `todo_task_routine` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `notice`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `created_at`          datetime(6)  DEFAULT NULL,
    `created_by`          varchar(255) DEFAULT NULL,
    `updated_at`          datetime(6)  DEFAULT NULL,
    `updated_by`          varchar(255) DEFAULT NULL,
    `notice_type`         varchar(255) NOT NULL,
    `title`               varchar(255) NOT NULL,
    `content`             TEXT         NOT NULL,
    `start_date_time`     datetime(6)  NOT NULL,
    `end_date_time`       datetime(6)  NOT NULL,
    `delete_yn`           varchar(1)   NOT NULL,
    `thumbnail_image_url` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `notice_content_image`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6)  DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `updated_at` datetime(6)  DEFAULT NULL,
    `updated_by` varchar(255) DEFAULT NULL,
    `notice_id`  bigint       NOT NULL,
    `image_url`  varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_notice_content_image_notice` (`notice_id`),
    CONSTRAINT `FK_notice_content_image_notice` FOREIGN KEY (`notice_id`) REFERENCES `notice` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
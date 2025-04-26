CREATE TABLE IF NOT EXISTS holiday
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    country_code VARCHAR(255)          NOT NULL,
    date         DATE                  NOT NULL,
    name         VARCHAR(255)          NOT NULL,
    create_at    DATETIME              NULL,
    updated_at   DATETIME              NULL,
    created_by   VARCHAR(255)          NULL,
    updated_by   VARCHAR(255)          NULL,
    PRIMARY KEY (id)
);
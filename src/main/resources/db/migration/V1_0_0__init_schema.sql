CREATE TABLE wuan.`user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(255) NOT NULL UNIQUE,
    `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `password`    VARCHAR(255) NOT NULL,
    `enable`      BOOLEAN      NOT NULL DEFAULT true,
    PRIMARY KEY (`id`)
);

CREATE TABLE wuan.`permission`
(
    `id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE wuan.`role`
(
    `id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE wuan.`role_permission_map`
(
    `id`            BIGINT NOT NULL AUTO_INCREMENT,
    `role_id`       BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`role_id`) REFERENCES wuan.`role` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES wuan.`permission` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE wuan.`user_role_map`
(
    `id`      BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES wuan.`user` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES wuan.`role` (`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

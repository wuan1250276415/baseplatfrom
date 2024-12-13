INSERT INTO wuan.`user` (`username`, `password`)
VALUES ('admin', '$2a$10$7zfEdqQYJrBnmDdu7UkgS.zOAsJf4bB1ZYrVhCBAIvIoPbEmeVnVe');

INSERT INTO wuan.`role` (`code`, `name`)
VALUES ('ADMIN', 'ADMIN'),
       ('GENERAL', 'GENERAL');

INSERT INTO wuan.`permission` (`code`, `name`)
VALUES ('WRITE_USER_ROLE_PERMISSION', 'WRITE_USER_ROLE_PERMISSION'),
       ('READ_USER_ROLE_PERMISSION', 'READ_USER_ROLE_PERMISSION');

INSERT INTO wuan.`user_role_map` (`user_id`, `role_id`)
VALUES (1, 1);

INSERT INTO wuan.`role_permission_map` (`role_id`, `permission_id`)
VALUES (1, 1),
       (1, 2);

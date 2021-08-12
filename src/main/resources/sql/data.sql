DELETE FROM users WHERE id;
DELETE FROM role WHERE id;
DELETE FROM privilege WHERE id;
DELETE FROM verification_token WHERE id;
DELETE FROM tariff WHERE id;
DELETE FROM report_generator WHERE id;

ALTER TABLE users AUTO_INCREMENT = 100000;
INSERT INTO users (name, email, password, registered, enabled, provider, provider_id) VALUES
('Admin', 'admin@admin.com', '$2a$10$.ep1D0bvqWFKjfBGAMvGo.pO4deV.oC0y1fAM37PPWSaxy.dCQ4FK','2020-05-21', true, 'local', null),
('User', 'user@user.com', '$2a$10$twPQhTPltGPZ4lka./SUsuBbkmobOeZBFJ2CepmN5kSgJFSC0tuke','2020-05-21', true, 'local', null);

ALTER TABLE role AUTO_INCREMENT = 1;
INSERT INTO role (name) VALUES
('ROLE_ADMIN'),
('ROLE_USER'),
('ROLE_REPORT_GENERATOR');

ALTER TABLE privilege AUTO_INCREMENT = 1;
INSERT INTO privilege (name) VALUES
('READ_PRIVILEGE'),
('WRITE_PRIVILEGE'),
('CHANGE_PASSWORD_PRIVILEGE'),
('WRITE_REPORT_GENERATOR_PRIVILEGE'),
('WRITE_ONE_WINDOW_PRIVILEGE'),
('WRITE_E_SERVICE_CENTER_PRIVILEGE'),
('WRITE_CONTROL_CENTER_PRIVILEGE');

INSERT INTO users_roles (user_id, role_id) VALUES
(100000, 1),
(100001, 2);

INSERT INTO roles_privileges (role_id, privilege_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 1),
(2, 3),
(3, 4);

ALTER TABLE verification_token AUTO_INCREMENT = 1;
INSERT INTO verification_token (id, expiry_date, token, user_id) VALUES
(1, '2020-05-21', '7d9aa6e7-ca35-4ac8-a218-da96e63a1a96', 100000);

ALTER TABLE tariff AUTO_INCREMENT = 1;
INSERT INTO tariff (name, category, u_min, u_max, price, description) VALUES
('НДС', '1', '0', '0', '20.00', 'Налон на добавленную стоимость'),
('ГО1', '2', '1', '9', '85.00', 'Тариф от 1 до 9 пользователей'),
('ГО2', '2', '10', '19', '70.00', 'Тариф от 10 до 19 пользователей'),
('ГО3', '2', '20', '49', '60.00', 'Тариф от 20 до 49 пользователей'),
('ГО4', '2', '50', '1000', '45.00', 'Тариф более 50 полезователей');

ALTER TABLE report_generator AUTO_INCREMENT = 1;
INSERT INTO report_generator (name, c_type, c_number, c_date, unp, users_amount) VALUES
('Солигорскводоканал', '6ГО', 112, '2020-05-21', 123456789, 5),
('Витебскводоканал', '2ГО', 163, '2020-05-21', 987654321, 10);

ALTER TABLE report_generator AUTO_INCREMENT = 1;
INSERT INTO rep_gen_email (email, rep_gen_id) VALUES
('sol@sol.by', 1),
('solig@solig.by', 1),
('vit@vit.by', 2);
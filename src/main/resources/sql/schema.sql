DROP TABLE IF EXISTS password_reset_token;
DROP TABLE IF EXISTS verification_token;
DROP TABLE IF EXISTS roles_privileges;
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS privilege;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS tariff;

CREATE TABLE users
(
  id         INTEGER AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(120)             NOT NULL,
  email      VARCHAR(150)             NOT NULL,
  password   VARCHAR(100)             NOT NULL,
  registered DATETIME DEFAULT now()   NOT NULL,
  enabled    BOOL      DEFAULT FALSE  NOT NULL,
  provider   VARCHAR(30)              NOT NULL,
  provider_id VARCHAR(255)            NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  AUTO_INCREMENT = 100000;

CREATE UNIQUE INDEX users_unique_email_idx ON users (email);

CREATE TABLE role
(
  id     INTEGER AUTO_INCREMENT PRIMARY KEY,
  name   VARCHAR(32)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE privilege
(
  id        INTEGER AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(120)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE users_roles
(
  user_id   INTEGER NOT NULL,
  role_id   INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES role (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE roles_privileges
(
  role_id      INTEGER NOT NULL,
  privilege_id INTEGER NOT NULL,
  FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
  FOREIGN KEY (privilege_id) REFERENCES privilege (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE verification_token
(
  id          INTEGER AUTO_INCREMENT PRIMARY KEY,
  expiry_date DATETIME               NOT NULL,
  token       VARCHAR(255),
  user_id     INTEGER NOT NULL,
  FOREIGN KEY FK_VERIFY_USER (user_id) REFERENCES users (id) ON DELETE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE password_reset_token
(
  id          INTEGER AUTO_INCREMENT PRIMARY KEY,
  expiry_date DATETIME     NOT NULL,
  token       VARCHAR(255) NOT NULL,
  user_id     INTEGER      NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE tariff
(
  id          INTEGER AUTO_INCREMENT      PRIMARY KEY,
  name        VARCHAR(160)                NOT NULL,
  category    INTEGER                     NOT NULL,
  u_min       INTEGER                     NOT NULL,
  u_max       INTEGER                     NOT NULL,
  price       DECIMAL(7, 2)               NOT NULL,
  description VARCHAR(255)                NOT NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  AUTO_INCREMENT = 1;
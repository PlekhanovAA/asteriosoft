CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(128),
    email VARCHAR(128),
    enabled TINYINT NOT NULL
);

INSERT INTO users (username, password, email, enabled) VALUES ('user', '$2a$10$cRqfrdolNVFW6sAju0eNEOE0VC29aIyXwfsEsY2Fz2axy3MnH8ZGa', null, 1);
INSERT INTO users (username, password, email, enabled) VALUES ('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', null, 1);

CREATE TABLE IF NOT EXISTS authorities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(128) NOT NULL UNIQUE,
    authority VARCHAR(128) NOT NULL
);

ALTER TABLE authorities ADD FOREIGN KEY (username) REFERENCES users(username);

INSERT INTO authorities (username, authority) VALUES ('user', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_ADMIN');

CREATE TABLE IF NOT EXISTS authority_log (
   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   username VARCHAR(255),
   token VARCHAR(512),
   created_at TIMESTAMP,
   expired TINYINT NOT NULL,
   expired_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS banner (
   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL UNIQUE,
   price NUMERIC(20, 2) NOT NULL,
   is_deleted BOOLEAN NOT NULL
);

INSERT INTO banner (name, price, is_deleted) VALUES ('banner1', 10, FALSE);
INSERT INTO banner (name, price, is_deleted) VALUES ('banner2', 100.5, FALSE);

CREATE TABLE IF NOT EXISTS category (
   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL UNIQUE,
   request_id VARCHAR(64) NOT NULL UNIQUE,
   is_deleted BOOLEAN NOT NULL
);

INSERT INTO category (name, request_id, is_deleted) VALUES ('category1', 'cat_request_id_1', FALSE);
INSERT INTO category (name, request_id, is_deleted) VALUES ('category2', 'cat_request_id_2', FALSE);

CREATE TABLE IF NOT EXISTS log_record (
   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   user_ip VARCHAR(255),
   user_agent VARCHAR(255),
   request_time TIMESTAMP,
   banner_id BIGINT,
   category_ids VARCHAR(650),
   banner_price NUMERIC(20, 2),
   no_content_reason VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS category_banner (
   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   category_id BIGINT,
   banner_id BIGINT,
   FOREIGN KEY (category_id) references category(id),
   FOREIGN KEY (banner_id) references banner(id)
);

INSERT INTO category_banner (category_id, banner_id) VALUES (1, 1);
INSERT INTO category_banner (category_id, banner_id) VALUES (2, 1);
INSERT INTO category_banner (category_id, banner_id) VALUES (1, 2);



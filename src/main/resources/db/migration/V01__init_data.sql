CREATE TABLE IF NOT EXISTS users
(
    id         INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(100) NOT NULL,
    auth_token VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS files
(
    id      INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content BYTEA        NOT NULL,
    size    INT          NOT NULL,
    name    VARCHAR(100) not null unique,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

INSERT INTO users (username, password)
VALUES ('user1@mail.ru', '123')
ON CONFLICT DO NOTHING;

INSERT INTO users (username, password)
VALUES ('user2@mail.ru', '456')
ON CONFLICT DO NOTHING;

UPDATE users
SET password = '$2a$12$wVOUDc78Of1WAdkbRJk5ZeLDDGMAHDOGVPU/lziBPYL7ohZmfBx4a' --пароль 123
WHERE username = 'user1@mail.ru';

UPDATE users
SET password = '$2a$12$Vvk46VgUMS38f53mMTxj0O6Iql2Du0cAx/arzPQ9ECmrSv0C0cwXW' --пароль 456
WHERE username = 'user2@mail.ru';
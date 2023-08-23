INSERT INTO users (username, password)
VALUES ('user3@mail.ru', '456')
ON CONFLICT DO NOTHING;

UPDATE users
SET password = '$2a$12$Vvk46VgUMS38f53mMTxj0O6Iql2Du0cAx/arzPQ9ECmrSv0C0cwXW' --пароль 456
WHERE username = 'user3@mail.ru';
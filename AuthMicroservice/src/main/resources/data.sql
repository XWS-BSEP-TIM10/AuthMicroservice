INSERT INTO role (ID, NAME)
VALUES (1, 'ROLE_USER');

INSERT INTO role (ID, NAME)
VALUES (2, 'ROLE_ADMIN');

INSERT INTO users (ID, UUID, PASSWORD, USERNAME, ROLE_ID)
VALUES (101, '76607110-3f09-43b2-a7f7-2ead1a2f81c2', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username2', 1);

INSERT INTO role (ID, NAME)
VALUES (1, 'ROLE_USER');

INSERT INTO role (ID, NAME)
VALUES (2, 'ROLE_ADMIN');

INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb3', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username2', false);
INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb2', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username1', true);
INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb4', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username3', true);

INSERT INTO permission(id, name)
VALUES (1, 'UPDATE_PROFILE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (2, 'CONNECT_PERMISSION');
INSERT INTO permission(id, name)
VALUES (3, 'CHANGE_CONNECTION_STATUS_PERMISSION');
INSERT INTO permission(id, name)
VALUES (4, 'CRUD_EXPERIENCE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (5, 'CRUD_INTEREST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (6, 'CREATE_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (7, 'REACT_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (8, 'COMMENT_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (9, 'GET_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (10, 'GET_FEED_PERMISSION');
INSERT INTO permission(id, name)
VALUES (11, 'CHANGE_PASSWORD_PERMISSION');
INSERT INTO permission(id, name)
VALUES (12, 'GET_CONNECTION_STATUS_PERMISSION');

INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 1);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 2);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 3);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 4);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 5);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 6);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 7);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 8);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 9);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 10);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 11);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 12);

INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb3');
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb2');
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb4');


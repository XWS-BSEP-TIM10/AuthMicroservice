INSERT INTO role (ID, NAME)
VALUES (1, 'ROLE_USER');

INSERT INTO role (ID, NAME)
VALUES (2, 'ROLE_ADMIN');

INSERT INTO users (ID, PASSWORD, USERNAME, ROLE_ID, ACTIVATED)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb3', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username2', 1, false);

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
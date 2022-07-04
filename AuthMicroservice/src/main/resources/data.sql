INSERT INTO role (ID, NAME)
VALUES (1, 'ROLE_USER');

INSERT INTO role (ID, NAME)
VALUES (2, 'ROLE_ADMIN');

INSERT INTO role (ID, NAME)
VALUES (3, 'ROLE_AGENT');

INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED, SECRET, IS_USING_2FA)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb3', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username2', true, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK', false);
INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED, SECRET, IS_USING_2FA)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb2', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username1', true, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK', true);
INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED, SECRET, IS_USING_2FA)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb4', '$2a$10$28MUwyYgna28OIxoUnE7VOpjby0JRJUU0WQV0UZdMX5XA46XAvBCK', 'username3', true, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK', false);


INSERT INTO users (ID, PASSWORD, USERNAME, ACTIVATED, SECRET, IS_USING_2FA)
VALUES ('d12602fd-b7af-4da1-b1ca-bad8166d1fb5', 'fgdgfhgfdhdfghfdghdfdfdfdfddfg', 'agent', true, 'QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK', false);

INSERT INTO permission(id, name)
VALUES (1, 'CHANGE_PASSWORD_PERMISSION');
INSERT INTO permission(id, name)
VALUES (2, 'CREATE_CONNECTION_PERMISSION');
INSERT INTO permission(id, name)
VALUES (3, 'CHANGE_CONNECTION_STATUS_PERMISSION');
INSERT INTO permission(id, name)
VALUES (4, 'GET_CONNECTION_STATUS_PERMISSION');
INSERT INTO permission(id, name)
VALUES (5, 'CREATE_EXPERIENCE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (6, 'UPDATE_EXPERIENCE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (7, 'DELETE_EXPERIENCE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (8, 'CREATE_INTEREST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (9, 'DELETE_INTEREST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (10, 'CREATE_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (11, 'REACT_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (12, 'COMMENT_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (13, 'GET_POST_PERMISSION');
INSERT INTO permission(id, name)
VALUES (14, 'GET_FEED_PERMISSION');
INSERT INTO permission(id, name)
VALUES (15, 'UPDATE_PROFILE_PERMISSION');
INSERT INTO permission(id, name)
VALUES (16, 'ADMIN_PERMISSION');
INSERT INTO permission(id, name)
VALUES (17, 'CREATE_API_TOKEN');
INSERT INTO permission(id, name)
VALUES (18, 'CREATE_JOB_AD');
INSERT INTO permission(id, name)
VALUES (19, 'GET_JOB_ADS');
INSERT INTO permission(id, name)
VALUES (20, 'UPDATE_2FA_STATUS');
INSERT INTO permission(id, name)
VALUES (21, 'CHECK_2FA_STATUS');
INSERT INTO permission(id, name)
VALUES (22, 'CREATE_BLOCK_PERMISSION');
INSERT INTO permission(id, name)
VALUES (23, 'GET_RECOMMENDED_CONNECTIONS');
INSERT INTO permission(id, name)
VALUES (24, 'GET_PENDING_CONNECTIONS');



INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 1);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 1);
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
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 13);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 14);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 15);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 16);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 17);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (3, 18);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 18);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 19);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 20);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 20);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 21);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (2, 21);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 22);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 23);
INSERT INTO roles_permissions(role_id, permission_id)
VALUES (1, 24);

INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (2, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb3');
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb2');
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (1, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb4');
INSERT INTO user_role (ROLE_ID, USER_ID)
VALUES (3, 'd12602fd-b7af-4da1-b1ca-bad8166d1fb5');


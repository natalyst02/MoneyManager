create sequence ADMIN_PORTAL.PAP_USER_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
create table ADMIN_PORTAL.PAP_USER
(
    ID            NUMBER default "ADMIN_PORTAL"."PAP_USER_ID_SEQ"."NEXTVAL" not null primary key,
    USERNAME      VARCHAR2(50 char)                                     not null,
    KEYCLOAK_ID   VARCHAR2(50 char)                                     not null,
    EMPLOYEE_CODE VARCHAR2(50 char)                                     not null,
    FULL_NAME     VARCHAR2(250 char),
    PHONE_NUMBER  VARCHAR2(15 char),
    EMAIL         VARCHAR2(100 char),
    JOB_NAME      VARCHAR2(250 char),
    ORG_NAME      VARCHAR2(2000 char),
    REASON        VARCHAR2(2000 char),
    ACTIVE        NUMBER(1,0) default 0,
    CREATED_BY    VARCHAR2(50 char)                                not null,
    CREATED_AT    TIMESTAMP(6) WITH TIME ZONE                not null,
    UPDATED_BY    VARCHAR2(50 char)                                not null,
    UPDATED_AT    TIMESTAMP(6) WITH TIME ZONE                not null
);
create unique index PAP_USER_USERNAME_UINDEX
    on ADMIN_PORTAL.PAP_USER (USERNAME);
create unique index PAP_USER_USERNAME_KEYCLOAK_ID_EMPLOYEE_CODE_UINDEX
    on ADMIN_PORTAL.PAP_USER (USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE);

create sequence ADMIN_PORTAL.PAP_USER_HISTORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
CREATE TABLE ADMIN_PORTAL.PAP_USER_HISTORY
(
    ID            NUMBER default "ADMIN_PORTAL"."PAP_USER_HISTORY_ID_SEQ"."NEXTVAL" not null
        primary key,
    ACTION        VARCHAR2(20 char)                                        not null,
    USER_ID       NUMBER                                             not null,
    USERNAME      VARCHAR2(50 char)                                             not null,
    KEYCLOAK_ID   VARCHAR2(50 char)                                             not null,
    EMPLOYEE_CODE VARCHAR2(50 char)                                             not null,
    FULL_NAME     VARCHAR2(250 char),
    PHONE_NUMBER  VARCHAR2(15 char),
    EMAIL         VARCHAR2(100 char),
    JOB_NAME      VARCHAR2(250 char),
    ORG_NAME      VARCHAR2(2000 char),
    REASON        VARCHAR2(2000 char),
    ACTIVE        NUMBER(1, 0) default 0,
    CREATED_BY    VARCHAR2(50 char)                                        not null,
    CREATED_AT    TIMESTAMP(6) WITH TIME ZONE                        not null,
    UPDATED_BY    VARCHAR2(50 char)                                        not null,
    UPDATED_AT    TIMESTAMP(6) WITH TIME ZONE                        not null
);
CREATE TRIGGER PAP_USER_AFTER_UPDATE
    AFTER UPDATE
    ON ADMIN_PORTAL.PAP_USER
    FOR EACH ROW
BEGIN
    INSERT INTO ADMIN_PORTAL.PAP_USER_HISTORY(ACTION, USER_ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL, JOB_NAME, ORG_NAME, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('UPDATE', :NEW.ID, :NEW.USERNAME, :NEW.KEYCLOAK_ID, :NEW.EMPLOYEE_CODE, :NEW.FULL_NAME, :NEW.PHONE_NUMBER, :NEW.EMAIL, :NEW.JOB_NAME, :NEW.ORG_NAME, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
CREATE TRIGGER PAP_USER_AFTER_INSERT
    AFTER INSERT
    ON ADMIN_PORTAL.PAP_USER
    FOR EACH ROW
BEGIN
    INSERT INTO ADMIN_PORTAL.PAP_USER_HISTORY(ACTION, USER_ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL, JOB_NAME, ORG_NAME, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('INSERT', :NEW.ID, :NEW.USERNAME, :NEW.KEYCLOAK_ID, :NEW.EMPLOYEE_CODE, :NEW.FULL_NAME, :NEW.PHONE_NUMBER, :NEW.EMAIL, :NEW.JOB_NAME, :NEW.ORG_NAME, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;

create sequence ADMIN_PORTAL.PAP_ROLE_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
create table ADMIN_PORTAL.PAP_ROLE
(
    ID          NUMBER default "ADMIN_PORTAL"."PAP_ROLE_ID_SEQ"."NEXTVAL" not null primary key,
    CODE        VARCHAR2(50 char)    not null,
    NAME        VARCHAR2(250 char)    not null,
    TYPE        VARCHAR2(50 char)    not null,
    DESCRIPTION VARCHAR2(2000 char),
    PERMISSIONS VARCHAR2(4000 char),
    REASON      VARCHAR2(2000 char),
    ACTIVE      NUMBER(1,0) default 0,
    CREATED_BY  VARCHAR2(50 char)                                                                   not null,
    CREATED_AT  TIMESTAMP(6) WITH TIME ZONE                not null,
    UPDATED_BY  VARCHAR2(50 char)                                                                   not null,
    UPDATED_AT  TIMESTAMP(6) WITH TIME ZONE                not null
);
create unique index PAP_ROLE_CODE_UINDEX
    on ADMIN_PORTAL.PAP_ROLE (CODE);

create sequence ADMIN_PORTAL.PAP_ROLE_HISTORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
CREATE TABLE ADMIN_PORTAL.PAP_ROLE_HISTORY
(
    ID          NUMBER default "ADMIN_PORTAL"."PAP_ROLE_HISTORY_ID_SEQ"."NEXTVAL" not null
        primary key,
    ACTION      VARCHAR2(20 char) not null,
    ROLE_ID     NUMBER                                             not null,
    CODE        VARCHAR2(50 char)    not null,
    NAME        VARCHAR2(250 char)    not null,
    TYPE        VARCHAR2(50 char)    not null,
    DESCRIPTION VARCHAR2(2000 char),
    PERMISSIONS VARCHAR2(4000 char),
    REASON      VARCHAR2(2000 char),
    ACTIVE      NUMBER(1,0) default 0,
    CREATED_BY  VARCHAR2(50 char)                                                                   not null,
    CREATED_AT  TIMESTAMP(6) WITH TIME ZONE                        not null,
    UPDATED_BY  VARCHAR2(50 char)                                                                   not null,
    UPDATED_AT  TIMESTAMP(6) WITH TIME ZONE                        not null
);
CREATE TRIGGER PAP_ROLE_AFTER_UPDATE
    AFTER UPDATE
    ON ADMIN_PORTAL.PAP_ROLE
    FOR EACH ROW
BEGIN
    INSERT INTO ADMIN_PORTAL.PAP_ROLE_HISTORY(ACTION, ROLE_ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE,
                                 CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('UPDATE', :NEW.ID, :NEW.CODE, :NEW.NAME, :NEW.TYPE, :NEW.DESCRIPTION, :NEW.PERMISSIONS, :NEW.REASON,
            :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
CREATE TRIGGER PAP_ROLE_AFTER_INSERT
    AFTER INSERT
    ON ADMIN_PORTAL.PAP_ROLE
    FOR EACH ROW
BEGIN
    INSERT INTO ADMIN_PORTAL.PAP_ROLE_HISTORY(ACTION, ROLE_ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE,
                                 CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('INSERT', :NEW.ID, :NEW.CODE, :NEW.NAME, :NEW.TYPE, :NEW.DESCRIPTION, :NEW.PERMISSIONS, :NEW.REASON,
            :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;

create sequence ADMIN_PORTAL.PAP_PERMISSION_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
create table ADMIN_PORTAL.PAP_PERMISSION
(
    ID            NUMBER default "ADMIN_PORTAL"."PAP_PERMISSION_ID_SEQ"."NEXTVAL" not null primary key,
    MODULE        VARCHAR2(50 char)    not null,
    ACTION        VARCHAR2(50 char)    not null,
    DESCRIPTION   VARCHAR2(2000 char),
    BITMASK_VALUE NUMBER                                           not null,
    CREATED_AT    TIMESTAMP(6) WITH TIME ZONE                      not null
);

create unique index PAP_PERMISSION_MODULE_ACTION_UINDEX
    on ADMIN_PORTAL.PAP_PERMISSION (MODULE, ACTION);

create unique index PAP_PERMISSION_MODULE_ACTION_BITMASK_VALUE_UINDEX
    on ADMIN_PORTAL.PAP_PERMISSION (MODULE, ACTION, BITMASK_VALUE);

create sequence ADMIN_PORTAL.PAP_PERMISSION_CATEGORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
create table PAP_PERMISSION_CATEGORY
(
    ID            NUMBER default "ADMIN_PORTAL"."PAP_PERMISSION_CATEGORY_ID_SEQ"."NEXTVAL" not null primary key,
    PERMISSION_ID        NUMBER    not null,
    TYPE          VARCHAR2(50 char)    not null,
    SUB_TYPE          VARCHAR2(50 char)    ,
    CREATED_AT    TIMESTAMP(6) WITH TIME ZONE                      not null
);

create unique index PAP_PERMISSION_CATEGORY_PERMISSION_ID_TYPE_UINDEX
    on ADMIN_PORTAL.PAP_PERMISSION_CATEGORY (PERMISSION_ID, TYPE);

create sequence ADMIN_PORTAL.PAP_USER_ROLE_ID_SEQ maxvalue 99999999999999999999 cache 1000;
create table ADMIN_PORTAL.PAP_USER_ROLE
(
    ID         NUMBER default "ADMIN_PORTAL"."PAP_USER_ROLE_ID_SEQ"."NEXTVAL" not null primary key,
    USER_ID    NUMBER                                          not null,
    ROLE_ID    NUMBER                                          not null,
    CREATED_BY VARCHAR2(50 char)                               not null,
    CREATED_AT TIMESTAMP(6) WITH TIME ZONE                     not null,
    UPDATED_BY VARCHAR2(50 char)                               not null,
    UPDATED_AT TIMESTAMP(6) WITH TIME ZONE                     not null
);
create sequence ADMIN_PORTAL.PAP_USER_ROLE_HISTORY_ID_SEQ minvalue 1 maxvalue 99999999999999999999 start with 1 increment by 1 cache 1000;
CREATE TABLE ADMIN_PORTAL.PAP_USER_ROLE_HISTORY
(
    ID           NUMBER default "ADMIN_PORTAL"."PAP_USER_ROLE_HISTORY_ID_SEQ"."NEXTVAL" not null primary key,
    ACTION       VARCHAR2(20 char)                                       not null,
    USER_ROLE_ID NUMBER                                                  not null,
    USER_ID      NUMBER                                                  not null,
    ROLE_ID      NUMBER                                                  not null,
    CREATED_BY   VARCHAR2(50 char)                                       not null,
    CREATED_AT   TIMESTAMP(6) WITH TIME ZONE                             not null,
    UPDATED_BY   VARCHAR2(50 char)                                       not null,
    UPDATED_AT   TIMESTAMP(6) WITH TIME ZONE                             not null
);
CREATE TRIGGER PAP_USER_ROLE_AFTER_UPDATE
    AFTER UPDATE
    ON ADMIN_PORTAL.PAP_USER_ROLE
    FOR EACH ROW
BEGIN
    INSERT INTO ADMIN_PORTAL.PAP_USER_ROLE_HISTORY(ACTION, USER_ROLE_ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('UPDATE', :NEW.ID, :NEW.USER_ID, :NEW.ROLE_ID, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
CREATE TRIGGER PAP_USER_ROLE_AFTER_INSERT
    AFTER INSERT
    ON ADMIN_PORTAL.PAP_USER_ROLE
    FOR EACH ROW
BEGIN
    INSERT INTO ADMIN_PORTAL.PAP_USER_ROLE_HISTORY(ACTION, USER_ROLE_ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('INSERT',  :NEW.ID, :NEW.USER_ID, :NEW.ROLE_ID, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;

create sequence ADMIN_PORTAL.PAP_EVENT_MESSAGE_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;

create table ADMIN_PORTAL.PAP_EVENT_MESSAGE
(
    ID         NUMBER    default "ADMIN_PORTAL"."PAP_EVENT_MESSAGE_ID_SEQ"."NEXTVAL" not null
        primary key,
    TOPIC      VARCHAR2(50 char)                                                         not null,
    KEY        VARCHAR2(200 char),
    MESSAGE    VARCHAR2(4000 char)                                                       not null,
    SENT       NUMBER(1) default 0                                                       not null,
    CREATED_BY VARCHAR2(50 char)                                                         not null,
    CREATED_AT TIMESTAMP(6) WITH TIME ZONE                                               not null,
    UPDATED_BY VARCHAR2(50 char)                                                         not null,
    UPDATED_AT TIMESTAMP(6) WITH TIME ZONE                                               not null,
    PARTITION  NUMBER
);

create sequence ADMIN_PORTAL.PAP_ERROR_MESSAGE_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
create table ADMIN_PORTAL.PAP_ERROR_MESSAGE
(
    ID             NUMBER default "ADMIN_PORTAL"."PAP_ERROR_MESSAGE_ID_SEQ"."NEXTVAL" not null
        primary key,
    TOPIC          VARCHAR2(50 char)                                                 not null,
    PARTITION      NUMBER                                                            not null,
    OFFSET         NUMBER                                                            not null,
    KEY            VARCHAR2(200 char),
    MESSAGE        VARCHAR2(4000 char)                                               not null,
    ERROR          CLOB,
    STATUS         VARCHAR2(50 char),
    CREATED_BY     VARCHAR2(50 char)                                                 not null,
    CREATED_AT     TIMESTAMP(6) WITH TIME ZONE                                       not null,
    UPDATED_BY     VARCHAR2(50 char)                                                 not null,
    UPDATED_AT     TIMESTAMP(6) WITH TIME ZONE                                       not null,
    UTC_CREATED_AT as (SYS_EXTRACT_UTC("CREATED_AT"))
);
create index ERROR_MESSAGE_UTC_CREATED_AT_TOPIC_INDEX
    on ADMIN_PORTAL.PAP_ERROR_MESSAGE (UTC_CREATED_AT, TOPIC);
create index ERROR_MESSAGE_UTC_CREATED_AT_STATUS_TOPIC_INDEX
    on ADMIN_PORTAL.PAP_ERROR_MESSAGE (UTC_CREATED_AT, STATUS, TOPIC);
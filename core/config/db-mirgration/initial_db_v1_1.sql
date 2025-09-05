create sequence PAP_ALIAS_ACCOUNT_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;

create table PAP_ALIAS_ACCOUNT
(
    ID             NUMBER default "PAP_ALIAS_ACCOUNT_ID_SEQ"."NEXTVAL"   not null primary key,
    NAME               VARCHAR2(50 char) not null,
    PARTNER_TYPE       VARCHAR2(50 char) not null,
    PARTNER_ACCOUNT VARCHAR2(50 char)                                                     not null,
    GET_NAME_URL    VARCHAR2(200 char),
    CONFIRM_URL     VARCHAR2(200 char),
    PROTOCOL        VARCHAR2(50 char),
    CHANNEL            VARCHAR2(10 char),
    REGEX              VARCHAR2(50 char),
    MIN_TRANS_LIMIT    NUMBER(38,0),
    MAX_TRANS_LIMIT    NUMBER(38,0),
    PARTNER_PUBLIC_KEY VARCHAR2(2000 char),
    MB_PRIVATE_KEY     VARCHAR2(2000 char),
    REASON          VARCHAR2(2000 char),
    ACTIVE          NUMBER(1, 0) default 0,
    CREATED_BY      VARCHAR2(50 char)                                                not null,
    CREATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null,
    UPDATED_BY      VARCHAR2(50 char)                                                not null,
    UPDATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null
);
create unique index PAP_ALIAS_ACCOUNT_NAME_INDEX
    on PAP_ALIAS_ACCOUNT (NAME);

create sequence PAP_ALIAS_ACCOUNT_HISTORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;


CREATE TABLE PAP_ALIAS_ACCOUNT_HISTORY
(
    ID              NUMBER default "PAP_ALIAS_ACCOUNT_HISTORY_ID_SEQ"."NEXTVAL"      not null primary key,
    ACTION          VARCHAR2(20)                                                not null,
    NAME               VARCHAR2(50 char) not null,
    PARTNER_TYPE       VARCHAR2(50 char) not null,
    PARTNER_ACCOUNT VARCHAR2(50 char)                                                     not null,
    GET_NAME_URL    VARCHAR2(200 char),
    CONFIRM_URL     VARCHAR2(200 char),
    PROTOCOL        VARCHAR2(50 char),
    CHANNEL            VARCHAR2(10 char),
    REGEX              VARCHAR2(50 char),
    MIN_TRANS_LIMIT    NUMBER(38,0),
    MAX_TRANS_LIMIT    NUMBER(38,0),
    PARTNER_PUBLIC_KEY VARCHAR2(2000 char),
    MB_PRIVATE_KEY     VARCHAR2(2000 char),
    REASON          VARCHAR2(2000 char),
    ACTIVE          NUMBER(1, 0) default 0,
    CREATED_BY      VARCHAR2(50 char)                                                not null,
    CREATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null,
    UPDATED_BY      VARCHAR2(50 char)                                                not null,
    UPDATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null
);

create sequence PAP_ERROR_CODE_CONFIG_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;

create table PAP_ERROR_CODE_CONFIG
(
    ORIGINAL_SERVICE  VARCHAR2(100 char),
    ERROR_CODE        VARCHAR2(100 char)                                                      not null,
    ERROR_DESC        VARCHAR2(2000 char)                                                     not null,
    ID                NUMBER    default PAP_ERROR_CODE_CONFIG_ID_SEQ."NEXTVAL"                not null
        constraint PAP_ERROR_CODE_CONFIG_PK
            primary key,
    ACTIVE            NUMBER(1) default 0                                                     not null,
    TRANSFER_CHANNEL  VARCHAR2(100 char)                                                      not null,
    REASON            NVARCHAR2(2000)                                                         not null,
    TYPE              NUMBER(1) default 1,
    CREATED_BY        VARCHAR2(50 char)                                                       not null,
    UPDATED_BY        VARCHAR2(50 char)                                                       not null,
    CREATED_AT        TIMESTAMP(6) WITH TIME ZONE                                             not null,
    UPDATED_AT        TIMESTAMP(6) WITH TIME ZONE                                             not null
)

create unique index PAP_ERROR_CODE_CONFIG_TRANSFER_CHANNEL_ERROR_CODE_UINDEX
    on PAP_ERROR_CODE_CONFIG (TRANSFER_CHANNEL, ERROR_CODE);

create sequence PAP_ERROR_CODE_CONFIG_HISTORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;

create table PAP_ERROR_CODE_CONFIG_HISTORY
(
    ACTION                          VARCHAR2(20 char)                                                                 not null,
    PAP_ERROR_CODE_CONFIG_ID        NUMBER                                                                            not null,
    ORIGINAL_SERVICE                VARCHAR2(100 char)                                                                not null,
    ERROR_CODE                      VARCHAR2(100 char),
    ERROR_DESC                      VARCHAR2(2000 char),
    ID                              NUMBER    default PAP_ERROR_CODE_CONFIG_HISTORY_ID_SEQ."NEXTVAL"                  not null
        constraint PAP_ERROR_CODE_CONFIG_HISTORY_PK
            primary key,
    ACTIVE                          NUMBER(1) default 0,
    TRANSFER_CHANNEL                VARCHAR2(100 char)                                                                not null,
    REASON                          NVARCHAR2(2000)                                                                   not null,
    TYPE                            NUMBER(1) default 1,
    CREATED_BY                      VARCHAR2(50 char)                                                                 not null,
    UPDATED_BY                      VARCHAR2(50 char)                                                                 not null,
    CREATED_AT                      TIMESTAMP(6) WITH TIME ZONE                                                       not null,
    UPDATED_AT                      TIMESTAMP(6) WITH TIME ZONE                                                       not null
)

CREATE TRIGGER PAP_ALIAS_ACCOUNT_AFTER_UPDATE
    AFTER UPDATE
    ON PAP_ALIAS_ACCOUNT
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_ALIAS_ACCOUNT_HISTORY(ACTION, NAME, PARTNER_TYPE, PARTNER_ACCOUNT, GET_NAME_URL, CONFIRM_URL, PROTOCOL, CHANNEL, REGEX, MIN_TRANS_LIMIT, MAX_TRANS_LIMIT, PARTNER_PUBLIC_KEY, MB_PRIVATE_KEY, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('UPDATE', :NEW.NAME, :NEW.PARTNER_TYPE, :NEW.PARTNER_ACCOUNT, :NEW.GET_NAME_URL, :NEW.CONFIRM_URL, :NEW.PROTOCOL, :NEW.CHANNEL, :NEW.REGEX, :NEW.MIN_TRANS_LIMIT, :NEW.MAX_TRANS_LIMIT, :NEW.PARTNER_PUBLIC_KEY, :NEW.MB_PRIVATE_KEY, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
CREATE TRIGGER PAP_ALIAS_ACCOUNT_AFTER_INSERT
    AFTER INSERT
    ON PAP_ALIAS_ACCOUNT
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_ALIAS_ACCOUNT_HISTORY(ACTION, NAME, PARTNER_TYPE, PARTNER_ACCOUNT, GET_NAME_URL, CONFIRM_URL, PROTOCOL, CHANNEL, REGEX, MIN_TRANS_LIMIT, MAX_TRANS_LIMIT, PARTNER_PUBLIC_KEY, MB_PRIVATE_KEY, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('INSERT', :NEW.NAME, :NEW.PARTNER_TYPE, :NEW.PARTNER_ACCOUNT, :NEW.GET_NAME_URL, :NEW.CONFIRM_URL, :NEW.PROTOCOL, :NEW.CHANNEL, :NEW.REGEX, :NEW.MIN_TRANS_LIMIT, :NEW.MAX_TRANS_LIMIT, :NEW.PARTNER_PUBLIC_KEY, :NEW.MB_PRIVATE_KEY, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;

CREATE TRIGGER PAP_ERROR_CODE_HISTORY_AFTER_INSERT
    AFTER INSERT
    ON PAP_ERROR_CODE_CONFIG
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_ERROR_CODE_CONFIG_HISTORY
    (ACTION, REASON, PAP_ERROR_CODE_CONFIG_ID, ORIGINAL_SERVICE, ERROR_CODE, ERROR_DESC, ACTIVE, TRANSFER_CHANNEL, TYPE, CREATED_BY, UPDATED_BY, CREATED_AT, UPDATED_AT)
    VALUES ('INSERT', :NEW.REASON, :NEW.ID, :NEW.ORIGINAL_SERVICE, :NEW.ERROR_CODE, :NEW.ERROR_DESC, :NEW.ACTIVE, :NEW.TRANSFER_CHANNEL, :NEW.TYPE, :NEW.CREATED_BY, :NEW.UPDATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_AT);
end;

CREATE TRIGGER PAP_ERROR_CODE_HISTORY_AFTER_UPDATE
    AFTER UPDATE
    ON PAP_ERROR_CODE_CONFIG
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_ERROR_CODE_CONFIG_HISTORY
    (ACTION, REASON, PAP_ERROR_CODE_CONFIG_ID, ORIGINAL_SERVICE, ERROR_CODE, ERROR_DESC, ACTIVE, TRANSFER_CHANNEL, TYPE, CREATED_BY, UPDATED_BY, CREATED_AT, UPDATED_AT)
    VALUES ('UPDATE', :NEW.REASON, :NEW.ID, :NEW.ORIGINAL_SERVICE, :NEW.ERROR_CODE, :NEW.ERROR_DESC, :NEW.ACTIVE, :NEW.TRANSFER_CHANNEL, :NEW.TYPE, :NEW.CREATED_BY, :NEW.UPDATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_AT);
end;

create sequence PAP_ERROR_CONFIG_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;

create table PAP_ERROR_CONFIG
(
    ID             NUMBER default "PAP_ERROR_CONFIG_ID_SEQ"."NEXTVAL"   not null primary key,
    TRANSFER_CHANNEL    varchar2(50 char)    not null,
    BANK_CODE       varchar2(50 char)       not null,
    BANK_NAME       varchar2(250 char)      not null,
    MAX_ERROR_PERCENTAGE    NUMBER(5,2)   default 0   not null,
    MIN_TXN         NUMBER(9)   default 0 not null,
    CALCULATION_FREQUENCY   NUMBER(9) not null,
    OFFLINE_DURATION    NUMBER(9)   not null,
    REASON          VARCHAR2(2000 char)     not null,
    ACTIVE          NUMBER(1, 0) default 0,
    CREATED_BY      VARCHAR2(50 char)                                                not null,
    CREATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null,
    UPDATED_BY      VARCHAR2(50 char)                                                not null,
    UPDATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null
);
create unique index PAP_ERROR_CONFIG_TRANSFER_CHANNEL_BANK_CODE_UINDEX
    on PAP_ERROR_CONFIG (TRANSFER_CHANNEL, BANK_CODE);

create sequence PAP_ERROR_CONFIG_HISTORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;

CREATE TABLE PAP_ERROR_CONFIG_HISTORY
(
    ID              NUMBER default "PAP_ERROR_CONFIG_HISTORY_ID_SEQ"."NEXTVAL"      not null primary key,
    ACTION          VARCHAR2(20)                                                not null,
    PAP_ERROR_CONFIG_ID               NUMBER not null,
    TRANSFER_CHANNEL    varchar2(50 char)    not null,
    BANK_CODE       varchar2(50 char)       not null,
    BANK_NAME       varchar2(250 char)      not null,
    MAX_ERROR_PERCENTAGE    NUMBER(5,2)   default 0   not null,
    MIN_TXN         NUMBER(9)   default 0 not null,
    CALCULATION_FREQUENCY   NUMBER(9) not null,
    OFFLINE_DURATION    NUMBER(9)   not null,
    REASON          VARCHAR2(2000 char)     not null,
    ACTIVE          NUMBER(1, 0) default 0,
    CREATED_BY      VARCHAR2(50 char)                                                not null,
    CREATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null,
    UPDATED_BY      VARCHAR2(50 char)                                                not null,
    UPDATED_AT      TIMESTAMP(6) WITH TIME ZONE                                      not null
);
CREATE TRIGGER PAP_ERROR_CONFIG_AFTER_UPDATE
    AFTER UPDATE
    ON PAP_ERROR_CONFIG
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_ERROR_CONFIG_HISTORY(ACTION, PAP_ERROR_CONFIG_ID, TRANSFER_CHANNEL, BANK_CODE, BANK_NAME, MAX_ERROR_PERCENTAGE, MIN_TXN, CALCULATION_FREQUENCY, OFFLINE_DURATION, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('UPDATE', :NEW.ID, :NEW.TRANSFER_CHANNEL, :NEW.BANK_CODE, :NEW.BANK_NAME, :NEW.MAX_ERROR_PERCENTAGE, :NEW.MIN_TXN, :NEW.CALCULATION_FREQUENCY, :NEW.OFFLINE_DURATION, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
CREATE TRIGGER PAP_ERROR_CONFIG_AFTER_INSERT
    AFTER INSERT
    ON PAP_ERROR_CONFIG
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_ERROR_CONFIG_HISTORY(ACTION, PAP_ERROR_CONFIG_ID, TRANSFER_CHANNEL, BANK_CODE, BANK_NAME, MAX_ERROR_PERCENTAGE, MIN_TXN, CALCULATION_FREQUENCY, OFFLINE_DURATION, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('INSERT', :NEW.ID, :NEW.TRANSFER_CHANNEL, :NEW.BANK_CODE, :NEW.BANK_NAME, :NEW.MAX_ERROR_PERCENTAGE, :NEW.MIN_TXN, :NEW.CALCULATION_FREQUENCY, :NEW.OFFLINE_DURATION, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;

create sequence PAP_BANK_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
create table PAP_BANK
(
    ID         NUMBER default PAP_BANK_ID_SEQ.nextval not null primary key,
    CODE       VARCHAR2(50 char) not null,
    IBFT_CODE  VARCHAR2(50 char),
    SHORT_NAME VARCHAR2(250 char) not null,
    FULL_NAME  VARCHAR2(500 char),
    REASON     VARCHAR2(250 char),
    ACTIVE     NUMBER(1, 0) default 0,
    CREATED_BY VARCHAR2(50 char) not null,
    CREATED_AT TIMESTAMP(6) WITH TIME ZONE            not null,
    UPDATED_BY VARCHAR2(50 char) not null,
    UPDATED_AT TIMESTAMP(6) WITH TIME ZONE            not null
);
create unique index PAP_BANK_CODE_UINDEX on PAP_BANK (CODE);

create sequence PAP_BANK_HISTORY_ID_SEQ
    minvalue 1
    maxvalue 99999999999999999999
    start with 1
    increment by 1 cache 1000;
CREATE TABLE PAP_BANK_HISTORY
(
    ID         NUMBER default "PAP_BANK_HISTORY_ID_SEQ"."NEXTVAL" not null
        primary key,
    ACTION     VARCHAR2(20 char) not null,
    BANK_ID    NUMBER                                             not null,
    CODE       VARCHAR2(50 char) not null,
    IBFT_CODE  VARCHAR2(50 char),
    SHORT_NAME VARCHAR2(250 char) not null,
    FULL_NAME  VARCHAR2(500 char),
    REASON     VARCHAR2(250 char),
    ACTIVE     NUMBER(1, 0) default 0,
    CREATED_BY VARCHAR2(50 char) not null,
    CREATED_AT TIMESTAMP(6) WITH TIME ZONE                        not null,
    UPDATED_BY VARCHAR2(50 char) not null,
    UPDATED_AT TIMESTAMP(6) WITH TIME ZONE                        not null
);
CREATE TRIGGER PAP_BANK_AFTER_UPDATE
    AFTER UPDATE
    ON PAP_BANK
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_BANK_HISTORY(ACTION, BANK_ID, CODE, IBFT_CODE, SHORT_NAME, FULL_NAME, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('UPDATE', :NEW.ID, :NEW.CODE, :NEW.IBFT_CODE, :NEW.SHORT_NAME, :NEW.FULL_NAME, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
CREATE TRIGGER PAP_BANK_AFTER_INSERT
    AFTER INSERT
    ON PAP_BANK
    FOR EACH ROW
BEGIN
    INSERT INTO PAP_BANK_HISTORY(ACTION, BANK_ID, CODE, IBFT_CODE, SHORT_NAME, FULL_NAME, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES ('INSERT', :NEW.ID, :NEW.CODE, :NEW.IBFT_CODE, :NEW.SHORT_NAME, :NEW.FULL_NAME, :NEW.REASON, :NEW.ACTIVE, :NEW.CREATED_BY, :NEW.CREATED_AT, :NEW.UPDATED_BY, :NEW.UPDATED_AT);
end;
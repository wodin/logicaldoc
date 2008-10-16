CREATE TABLE CO_GROUPS (
  CO_GROUPNAME VARCHAR(255)   NOT NULL,
  CO_GROUPDESC VARCHAR(255),
    PRIMARY KEY ( CO_GROUPNAME ));

CREATE TABLE CO_USERGROUP (
  CO_GROUPNAME VARCHAR(255)   NOT NULL,
  CO_USERNAME  VARCHAR(255)   NOT NULL,
    PRIMARY KEY ( CO_GROUPNAME,CO_USERNAME ));

CREATE TABLE CO_USERS (
  CO_USERNAME   VARCHAR(255)   NOT NULL,
  CO_PASSWORD   VARCHAR(255),
  CO_NAME       VARCHAR(30),
  CO_FIRSTNAME  VARCHAR(30),
  CO_STREET     VARCHAR(100),
  CO_POSTALCODE VARCHAR(10),
  CO_CITY       VARCHAR(30),
  CO_COUNTRY    VARCHAR(30),
  CO_LANGUAGE   VARCHAR(10),
  CO_EMAIL      VARCHAR(255),
  CO_TELEPHONE  VARCHAR(30),
    PRIMARY KEY ( CO_USERNAME ));

CREATE TABLE LD_ARTICLE (
  LD_ID       BIGINT   NOT NULL,
  LD_DOCID    BIGINT,
  LD_SUBJECT  VARCHAR(255),
  LD_MESSAGE  VARCHAR(2000),
  LD_DATE     TIMESTAMP,
  LD_USERNAME VARCHAR(30),
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_ATTACHMENT (
  LD_MESSAGEID BIGINT   NOT NULL,
  LD_FILENAME  VARCHAR(255),
  LD_ICON      VARCHAR(255),
  LD_MIMETYPE  VARCHAR(255),
  LD_PARTID    INT   NOT NULL,
    PRIMARY KEY ( LD_MESSAGEID,LD_PARTID ));

CREATE TABLE LD_DOCUMENT (
  LD_ID           BIGINT   NOT NULL,
  LD_TITLE        VARCHAR(255),
  LD_VERSION      VARCHAR(10),
  LD_DATE         TIMESTAMP,
  LD_PUBLISHER    VARCHAR(30),
  LD_STATUS       INT,
  LD_TYPE         VARCHAR(255),
  LD_CHECKOUTUSER VARCHAR(30),
  LD_SOURCE       VARCHAR(255),
  LD_SOURCEAUTHOR VARCHAR(255),
  LD_SOURCEDATE   TIMESTAMP,
  LD_SOURCETYPE   VARCHAR(255),
  LD_COVERAGE     VARCHAR(255),
  LD_LANGUAGE     VARCHAR(10),
  LD_FILENAME     VARCHAR(255),
  LD_FILESIZE     BIGINT,
  LD_FOLDERID     BIGINT,
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_EMAIL (
  LD_ID            BIGINT   NOT NULL,
  LD_ACCOUNTID     BIGINT,
  LD_EMAILID       VARCHAR(255),
  LD_MESSAGETEXT   VARCHAR(255),
  LD_AUTHOR        VARCHAR(30),
  LD_SUBJECT       VARCHAR(255),
  LD_SENTDATE      VARCHAR(20),
  LD_RED           INT,
  LD_AUTHORADDRESS VARCHAR(255),
  LD_USERNAME      VARCHAR(30),
  LD_FOLDER        VARCHAR(30),
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_EMAILACCOUNT (
  LD_ID                BIGINT   NOT NULL,
  LD_USERNAME          VARCHAR(255),
  LD_MAILADDRESS       VARCHAR(255),
  LD_PROVIDER          VARCHAR(255),
  LD_HOST              VARCHAR(255),
  LD_PORT              VARCHAR(255),
  LD_USER              VARCHAR(255),
  LD_PASSWORD          VARCHAR(255),
  LD_ALLOWEDTYPES      VARCHAR(255),
  LD_DELETEFROMMAILBOX INT,
  LD_LANGUAGE          VARCHAR(255),
  LD_ENABLED           INT,
  LD_TARGETFOLDER      BIGINT,
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_HISTORY (
  LD_ID       BIGINT   NOT NULL,
  LD_DOCID    BIGINT,
  LD_DATE     TIMESTAMP,
  LO_USERNAME VARCHAR(30),
  LO_EVENT    VARCHAR(255),
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_KEYWORD (
  LD_DOCID   BIGINT   NOT NULL,
  LD_KEYWORD VARCHAR(255));

CREATE TABLE LD_MENU (
  LD_ID       BIGINT   NOT NULL,
  LD_TEXT     VARCHAR(255),
  LD_PARENTID BIGINT,
  LD_SORT     INT,
  LD_ICON     VARCHAR(255),
  LD_PATH     VARCHAR(255),
  LD_TYPE     INT,
  LD_REF      VARCHAR(255),
  LD_SIZE     BIGINT,
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_MENUGROUP (
  LD_MENUID      BIGINT   NOT NULL,
  LD_GROUPNAME   VARCHAR(255),
  LD_WRITEENABLE INT);

CREATE TABLE LD_RECIPIENT (
  LD_MESSAGEID BIGINT   NOT NULL,
  LD_ADDRESS   VARCHAR(255)   NOT NULL,
  LD_NAME      VARCHAR(255),
    PRIMARY KEY ( LD_MESSAGEID,LD_ADDRESS ));

CREATE TABLE LD_SYSTEMMESSAGE (
  LD_ID           BIGINT   NOT NULL,
  LD_AUTHOR       VARCHAR(100),
  LD_RECIPIENT    VARCHAR(100),
  LD_MESSAGETEXT  VARCHAR(2000),
  LD_SUBJECT      VARCHAR(255),
  LD_SENTDATE     VARCHAR(20)   NOT NULL,
  LD_DATESCOPE    INT,
  LD_PRIO         INT,
  LD_CONFIRMATION INT,
  LD_RED          INT   NOT NULL,
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_TERM (
  LD_DOCID     BIGINT   NOT NULL,
  LD_STEM      VARCHAR(255)   NOT NULL,
  LD_VALUE     FLOAT,
  LD_WORDCOUNT INT,
  LD_WORD      VARCHAR(255),
    PRIMARY KEY ( LD_DOCID,LD_STEM ));

CREATE TABLE LD_TICKET (
  LD_ID       BIGINT   NOT NULL,
  LD_TICKETID VARCHAR(255),
  LD_DOCID    BIGINT,
  LD_USERNAME VARCHAR(30),
    PRIMARY KEY ( LD_ID ));

CREATE TABLE LD_USERDOC (
  LD_DOCID    BIGINT   NOT NULL,
  LD_USERNAME VARCHAR(255)   NOT NULL,
  LD_DATE     TIMESTAMP,
    PRIMARY KEY ( LD_DOCID,LD_USERNAME ));

CREATE TABLE LD_VERSION (
  LD_DOCID   BIGINT   NOT NULL,
  LD_VERSION VARCHAR(10),
  LD_USER    VARCHAR(30),
  LD_DATE    TIMESTAMP,
  LD_COMMENT VARCHAR(2000));

alter table co_usergroup add constraint FK44CA21819E198925 foreign key (co_username) references co_users;
alter table co_usergroup add constraint FK44CA2181B6F18C05 foreign key (co_groupname) references co_groups;
alter table ld_attachment add constraint FK6C81064AAAE036A2 foreign key (ld_messageid) references ld_email;
alter table ld_document add constraint FK75ED9C027C565C60 foreign key (ld_folderid) references ld_menu;
alter table ld_emailaccount add constraint FK1013678CDFB2816 foreign key (ld_targetfolder) references ld_menu;
alter table ld_keyword add constraint FK55BBDA227C693DFD foreign key (ld_docid) references ld_document;
alter table ld_menugroup add constraint FKB4F7F679AA456AD1 foreign key (ld_menuid) references ld_menu;
alter table ld_recipient add constraint FK406A0412AAE036A2 foreign key (ld_messageid) references ld_email;
alter table ld_version add constraint FK9B3BD9117C693DFD foreign key (ld_docid) references ld_document;





INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE)
 VALUES  (1,
            'db.home',
            0,
            1,
            'home.png',
            '/',
            0,
            NULL,
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (2,
            'db.admin',
            1,
            1,
            'administration.png',
            '/',
            1,
            NULL,
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (4,
            'db.personal',
            1,
            3,
            'personal.png',
            '/',
            1,
            NULL,
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (5,
            'db.projects',
            1,
            4,
            'documents.png',
            '/',
            1,
            'document/browse',
            0);

INSERT INTO LD_MENU (
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (6,
            'db.user',
            2,
            1,
            'user.png',
            '/2',
            1,
            'admin/users',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (7,
            'db.group',
            2,
            2,
            'group.png',
            '/2',
            1,
            'admin/groups',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (8,
            'db.logging',
            2,
            3,
            'logging.png',
            '/2',
            1,
            'admin/logs',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (13,
            'db.messages',
            4,
            1,
            'message.png',
            '/4',
            1,
            'communication/messages',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (16,
            'db.changepassword',
            4,
            3,
            'password.png',
            '/4',
            1,
            'settings/password',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (17,
            'directory',
            2,
            5,
            'open.png',
            '/2',
            1,
            'admin/folders',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (19,
            'db.editme',
            4,
            4,
            'user.png',
            '/4',
            1,
            'settings/personalData',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (20,
            'db.emails',
            2,
            4,
            'mail.png',
            '/2',
            1,
            NULL,
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (23,
            'smtp',
            20,
            3,
            'mail_preferences.png',
            '/2/20',
            1,
            'admin/smtp',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (24,
            'db.emailaccounts',
            20,
            4,
            'mailbox.png',
            '/2/20',
            1,
            'admin/accounts',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (25,
            'db.searchengine',
            2,
            6,
            'search.png',
            '/2',
            1,
            'admin/searchEngine',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (26,
            'db.keywords',
            1,
            5,
            'keywords.png',
            '/',
            1,
            'search/keywords',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (27,
            'db.backup',
            2,
            7,
            'backup.png',
            '/2',
            1,
            'admin/backup',
            0);

INSERT INTO LD_MENU(
  LD_ID,
  LD_TEXT,
  LD_PARENTID,
  LD_SORT,
  LD_ICON,
  LD_PATH,
  LD_TYPE,
  LD_REF,
  LD_SIZE) 
 VALUES   (14,
            'task.tasks',
            2,
            8,
            'thread.png',
            '/2',
            1,
            'admin/tasks',
            0);

INSERT INTO CO_GROUPS
VALUES     ('admin',
            'Group of admins');

INSERT INTO CO_GROUPS
VALUES     ('author',
            'Group of authors');

INSERT INTO CO_GROUPS
VALUES     ('guest',
            'Group of guest');

INSERT INTO CO_USERS
           (CO_USERNAME,
            CO_PASSWORD,
            CO_NAME,
            CO_FIRSTNAME,
            CO_STREET,
            CO_POSTALCODE,
            CO_CITY,
            CO_COUNTRY,
            CO_LANGUAGE,
            CO_EMAIL,
            CO_TELEPHONE)
VALUES     ('admin',
            'd033e22ae348aeb566fc214aec3585c4da997',
            'Admin',
            'Admin',
            '',
            '',
            '',
            '',
            'en',
            'admin@admin.net',
            '');

INSERT INTO LD_MENUGROUP
VALUES     (1,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (2,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (4,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (5,
            'admin',
            1);

INSERT INTO LD_MENUGROUP
VALUES     (6,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (7,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (8,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (13,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (14,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (16,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (17,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (19,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (20,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (23,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (24,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (25,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (26,
            'admin',
            1);

INSERT INTO LD_MENUGROUP
VALUES     (27,
            'admin',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (1,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (4,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (5,
            'author',
            1);

INSERT INTO LD_MENUGROUP
VALUES     (13,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (16,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (19,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (20,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (23,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (24,
            'author',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (26,
            'author',
            1);

INSERT INTO LD_MENUGROUP
VALUES     (1,
            'guest',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (4,
            'guest',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (5,
            'guest',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (13,
            'guest',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (16,
            'guest',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (19,
            'guest',
            0);

INSERT INTO LD_MENUGROUP
VALUES     (26,
            'guest',
            0);

INSERT INTO co_usergroup (co_username,co_groupname) 
VALUES ('admin','admin');
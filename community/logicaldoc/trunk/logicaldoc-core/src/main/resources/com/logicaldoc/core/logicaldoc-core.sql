create table ld_article (ld_id bigint not null, ld_docid bigint not null, ld_subject varchar(255), ld_message varchar(2000), ld_date timestamp, ld_username varchar(255), primary key (ld_id));
create table ld_attachment (ld_messageid bigint not null, ld_filename varchar(255), ld_icon varchar(255), ld_mimetype varchar(255), ld_partid int not null, primary key (ld_messageid, ld_partid));
create table ld_document (ld_id bigint not null, ld_title varchar(255), ld_version varchar(10), ld_date timestamp, ld_publisher varchar(30), ld_status int, ld_type varchar(255), ld_checkoutuser varchar(30), ld_source varchar(255), ld_sourceauthor varchar(255), ld_sourcedate timestamp, ld_sourcetype varchar(255), ld_coverage varchar(255), ld_language varchar(10), ld_filename varchar(255), ld_filesize bigint, ld_folderid bigint, primary key (ld_id));
create table ld_email (ld_id bigint not null, ld_accountid bigint, ld_emailid varchar(255), ld_messageText varchar(255), ld_author varchar(30), ld_subject varchar(255), ld_sentdate varchar(20), ld_red int, ld_authoraddress varchar(255), ld_username varchar(30), ld_folder varchar(30), primary key (ld_id));
create table ld_emailaccount (ld_id bigint not null, ld_username varchar(255), ld_mailaddress varchar(255), ld_provider varchar(255), ld_host varchar(255), ld_port varchar(255), ld_user varchar(255), ld_password varchar(255), ld_allowedtypes varchar(255), ld_deletefrommailbox int, ld_language varchar(255), ld_enabled int, ld_targetfolder bigint, primary key (ld_id));
create table ld_group (ld_id bigint not null, ld_name varchar(255) not null, ld_description varchar(255), primary key (ld_id));
create table ld_history (ld_id bigint not null, ld_docid bigint not null, lo_userid bigint not null, ld_date timestamp, lo_username varchar(255), lo_event varchar(255), primary key (ld_id));
create table ld_keyword (ld_docid bigint not null, ld_keyword varchar(255));
create table ld_menu (ld_id bigint not null, ld_text varchar(255), ld_parentid bigint, ld_sort int, ld_icon varchar(255), ld_path varchar(255), ld_type int, ld_ref varchar(255), ld_size bigint, primary key (ld_id));
create table ld_menugroup (ld_menuid bigint not null, ld_groupid bigint, ld_writeenable int);
create table ld_recipient (ld_messageid bigint not null, ld_address varchar(255) not null, ld_name varchar(255), primary key (ld_messageid, ld_address));
create table ld_systemmessage (ld_id bigint not null, ld_author varchar(255), ld_recipient varchar(255), ld_messagetext varchar(2000), ld_subject varchar(255), ld_sentdate varchar(20) not null, ld_datescope int, ld_prio int, ld_confirmation int, ld_red int not null, primary key (ld_id));
create table ld_term (ld_id bigint not null, ld_docid bigint not null, ld_stem varchar(255) not null, ld_value float, ld_wordcount int, ld_word varchar(255), primary key (ld_id));
create table ld_ticket (ld_id bigint not null, ld_ticketid varchar(255) not null, ld_docid bigint, ld_username varchar(255), primary key (ld_id));
create table ld_user (ld_id bigint not null, ld_username varchar(255) not null, ld_password varchar(255), ld_name varchar(255), ld_firstname varchar(255), ld_street varchar(255), ld_postalcode varchar(255), ld_city varchar(255), ld_country varchar(30), ld_language varchar(10), ld_email varchar(255), ld_telephone varchar(255), primary key (ld_id));
create table ld_userdoc (ld_id bigint not null, ld_docid bigint not null, ld_userid bigint not null, ld_date timestamp, primary key (ld_id));
create table ld_usergroup (ld_groupid bigint not null, ld_userid bigint not null, primary key (ld_groupid, ld_userid));
create table ld_version (ld_docid bigint not null, ld_version varchar(10), ld_user varchar(255), ld_date timestamp, ld_comment varchar(2000));
alter table ld_attachment add constraint FK6C81064AAAE036A2 foreign key (ld_messageid) references ld_email;
alter table ld_document add constraint FK75ED9C027C565C60 foreign key (ld_folderid) references ld_menu;
alter table ld_emailaccount add constraint FK1013678CDFB2816 foreign key (ld_targetfolder) references ld_menu;
alter table ld_keyword add constraint FK55BBDA227C693DFD foreign key (ld_docid) references ld_document;
alter table ld_menugroup add constraint FKB4F7F679AA456AD1 foreign key (ld_menuid) references ld_menu;
alter table ld_recipient add constraint FK406A0412AAE036A2 foreign key (ld_messageid) references ld_email;
alter table ld_usergroup add constraint FK2435438DB8B12CA9 foreign key (ld_userid) references ld_user;
alter table ld_usergroup add constraint FK2435438D76F11EA1 foreign key (ld_groupid) references ld_group;
alter table ld_version add constraint FK9B3BD9117C693DFD foreign key (ld_docid) references ld_document;


alter table ld_user add constraint AK_USER unique(ld_username);
alter table ld_group add constraint AK_GROUP unique(ld_name);
alter table ld_ticket add constraint AK_TICKET unique(ld_ticketid);
alter table ld_userdoc add constraint AK_USERDOC unique(ld_docid,ld_userid);



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

INSERT INTO LD_GROUP
VALUES     (1,'admin',
            'Group of admins');

INSERT INTO LD_GROUP
VALUES     (2,'author',
            'Group of authors');

INSERT INTO LD_GROUP
VALUES     (3,'guest',
            'Group of guest');

INSERT INTO LD_USER
           (LD_ID,
            LD_USERNAME,
            LD_PASSWORD,
            LD_NAME,
            LD_FIRSTNAME,
            LD_STREET,
            LD_POSTALCODE,
            LD_CITY,
            LD_COUNTRY,
            LD_LANGUAGE,
            LD_EMAIL,
            LD_TELEPHONE)
VALUES     (1,'admin',
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
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (2,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (4,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (5,
            1,
            1);

INSERT INTO LD_MENUGROUP
VALUES     (6,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (7,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (8,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (13,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (14,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (16,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (17,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (19,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (20,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (23,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (24,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (25,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (26,
            1,
            1);

INSERT INTO LD_MENUGROUP
VALUES     (27,
            1,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (1,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (4,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (5,
            2,
            1);

INSERT INTO LD_MENUGROUP
VALUES     (13,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (16,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (19,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (20,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (23,
           2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (24,
            2,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (26,
            2,
            1);

INSERT INTO LD_MENUGROUP
VALUES     (1,
            3,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (4,
            3,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (5,
            3,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (13,
            3,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (16,
            3,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (19,
            3,
            0);

INSERT INTO LD_MENUGROUP
VALUES     (26,
            3,
            0);

INSERT INTO ld_usergroup (ld_userid,ld_groupid) 
VALUES (1,1);
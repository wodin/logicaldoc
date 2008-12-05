create table ld_article (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_docid bigint not null, ld_subject varchar(255), ld_message varchar(2000), ld_date timestamp, ld_username varchar(255), primary key (ld_id));
create table ld_attributes (ld_templateid bigint not null, ld_attribute varchar(255));
create table ld_document (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null,ld_immutable int not null, ld_customid varchar(4000), ld_title varchar(255), ld_version varchar(10), ld_date timestamp, ld_creation timestamp not null, ld_publisher varchar(30), ld_status int, ld_type varchar(255), ld_checkoutuser varchar(30), ld_source varchar(255), ld_sourceauthor varchar(255), ld_sourcedate timestamp, ld_sourcetype varchar(255), ld_coverage varchar(255), ld_language varchar(10), ld_filename varchar(255), ld_filesize bigint, ld_indexed int not null , ld_folderid bigint, ld_templateid bigint, primary key (ld_id));
create table ld_document_ext (ld_docid bigint not null, ld_value varchar(4000), ld_name varchar(255) not null, primary key (ld_docid, ld_name));
create table ld_group (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_name varchar(255) not null, ld_description varchar(255), ld_type int not null, primary key (ld_id));
create table ld_history (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_docid bigint not null, ld_userid bigint not null, ld_date timestamp, ld_username varchar(255), ld_event varchar(255), ld_comment varchar(4000), primary key (ld_id));
create table ld_keyword (ld_docid bigint not null, ld_keyword varchar(255));
create table ld_link (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_type varchar(255) not null, ld_docid1 bigint, ld_docid2 bigint, primary key (ld_id));
create table ld_menu (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_text varchar(255), ld_parentid bigint not null, ld_sort int, ld_icon varchar(255), ld_path varchar(255), ld_pathextended varchar(40000), ld_type int not null, ld_ref varchar(255), ld_size bigint, primary key (ld_id));
create table ld_menugroup (ld_menuid bigint not null, ld_groupid bigint not null, ld_write int not null, ld_addchild int not null, ld_managesecurity int not null, ld_manageimmutability int not null, ld_delete int not null, ld_rename int not null, ld_bulkimport int not null, ld_bulkexport int not null, primary key (ld_menuid, ld_groupid, ld_write, ld_addchild, ld_managesecurity, ld_delete, ld_rename, ld_manageimmutability, ld_bulkimport, ld_bulkexport));
create table ld_systemmessage (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_author varchar(255), ld_recipient varchar(255), ld_messagetext varchar(2000), ld_subject varchar(255), ld_sentdate varchar(20) not null, ld_datescope int, ld_prio int, ld_confirmation int, ld_red int not null, primary key (ld_id));
create table ld_template (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_name varchar(255) not null, ld_description varchar(2000), primary key (ld_id));
create table ld_ticket (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_ticketid varchar(255) not null, ld_docid bigint not null, ld_userid bigint not null, primary key (ld_id));
create table ld_user (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_username varchar(255) not null, ld_password varchar(255), ld_name varchar(255), ld_firstname varchar(255), ld_street varchar(255), ld_postalcode varchar(255), ld_city varchar(255), ld_country varchar(30), ld_language varchar(10), ld_email varchar(255), ld_telephone varchar(255), ld_type int not null, primary key (ld_id));
create table ld_userdoc (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_docid bigint not null, ld_userid bigint not null, ld_date timestamp, primary key (ld_id));
create table ld_usergroup (ld_groupid bigint not null, ld_userid bigint not null, primary key (ld_groupid, ld_userid));
create table ld_version (ld_docid bigint not null, ld_version varchar(10), ld_username varchar(255), ld_userid bigint not null, ld_date timestamp, ld_comment varchar(4000));
create table ld_generic (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_type varchar(255) not null, ld_subtype varchar(255) not null, ld_string1 varchar(4000), ld_string2 varchar(4000), ld_integer1 int, ld_integer2 int, ld_double1 float, ld_double2 float, ld_date1 timestamp, ld_date2 timestamp, primary key (ld_id));
create table ld_generic_ext (ld_genid bigint not null, ld_value varchar(4000), ld_name varchar(255) not null, primary key (ld_genid, ld_name));
alter table ld_attributes add constraint FKF9B7567E76C86307 foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_document add constraint FK75ED9C0276C86307 foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_document add constraint FK75ED9C027C565C60 foreign key (ld_folderid) references ld_menu(ld_id);
alter table ld_document_ext add constraint FK4E0884647C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_keyword add constraint FK55BBDA227C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_link add constraint FK1330661CADD6217 foreign key (ld_docid2) references ld_document(ld_id);
alter table ld_link add constraint FK1330661CADD6216 foreign key (ld_docid1) references ld_document(ld_id);
alter table ld_menugroup add constraint FKB4F7F679AA456AD1 foreign key (ld_menuid) references ld_menu(ld_id);
alter table ld_usergroup add constraint FK2435438DB8B12CA9 foreign key (ld_userid) references ld_user(ld_id);
alter table ld_usergroup add constraint FK2435438D76F11EA1 foreign key (ld_groupid) references ld_group(ld_id);
alter table ld_version add constraint FK9B3BD9117C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_generic_ext add constraint FK913AF772CF5D92AF foreign key (ld_genid) references ld_generic(ld_id);

alter table ld_ticket add constraint FK_TICKET_DOC foreign key (ld_docid) references ld_document on delete cascade;
alter table ld_ticket add constraint FK_TICKET_USER foreign key (ld_userid) references ld_user on delete cascade;
alter table ld_menugroup add constraint FK_MENUGROUP_GROUP foreign key (ld_groupid) references ld_group on delete cascade;
alter table ld_userdoc add constraint FK_USERDOC_DOC foreign key (ld_docid) references ld_document on delete cascade;
alter table ld_userdoc add constraint FK_USERDOC_USER foreign key (ld_userid) references ld_user on delete cascade;
alter table ld_article add constraint FK_ARTICLE_DOC foreign key (ld_docid) references ld_document on delete cascade;
alter table ld_menu add constraint FK_MENU_PARENT foreign key (ld_parentid) references ld_menu on delete cascade;
alter table ld_history add constraint FK_HISTORY_DOC foreign key (ld_docid) references ld_document on delete cascade;

create unique index  AK_DOCUMENT on ld_document (ld_customid);
create unique index  AK_USER on ld_user (ld_username);
create unique index  AK_GROUP on ld_group (ld_name);  
create unique index  AK_TICKET on ld_ticket (ld_ticketid);
create unique index  AK_USERGROUP on ld_usergroup (ld_groupid, ld_userid);
create unique index  AK_LINK on ld_link (ld_docid1, ld_docid2, ld_type);
create unique index  AK_TEMPLATE on ld_template (ld_name);
create unique index  AK_GENERIC on ld_generic (ld_type, ld_subtype);
alter table ld_history add constraint FK_HISTORY_USER foreign key (ld_userid) references ld_user(ld_id);
alter table ld_version add constraint FK_VERSION_USER foreign key (ld_userid) references ld_user(ld_id);


insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (1,'2008-10-22',0,'db.home',1,1,'home.png','/',1,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (2,'2008-10-22',0,'db.admin',1,1,'administration.png','/',1,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (4,'2008-10-22',0,'db.personal',1,3,'personal.png','/',1,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size, ld_pathextended)
values     (5,'2008-10-22',0,'db.projects',1,4,'documents.png','/',1,'document/browse',0, '/');

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (6,'2008-10-22',0,'db.user',2,1,'user.png','/2',1,'admin/users',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (7,'2008-10-22',0,'db.group',2,2,'group.png','/2',1,'admin/groups',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (8,'2008-10-22',0,'db.logging',2,3,'logging.png','/2',1,'admin/logs',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (13,'2008-10-22',0,'db.messages',4,1,'message.png','/4',1,'communication/messages',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (16,'2008-10-22',0,'db.changepassword',4,3,'password.png','/4',1,'settings/password',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (17,'2008-10-22',0,'directory',2,5,'open.png','/2',1,'admin/folders',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (19,'2008-10-22',0,'db.editme',4,4,'user.png','/4',1,'settings/personalData',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (20,'2008-10-22',0,'db.emails',2,4,'mail.png','/2',1,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (21,'2008-10-22',0,'templates',2,4,'template.png','/2',1,'admin/templates',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (23,'2008-10-22',0,'smtp',20,3,'mail_preferences.png','/2/20',1,'admin/smtp',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (25,'2008-10-22',0,'db.searchengine',2,6,'search.png','/2',1,'admin/searchEngine',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (18,'2008-10-22',0,'searches',1,6,'search.png','/1',1,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (26,'2008-10-22',0,'tags',18,5,'tags.png','/1/18',1,'search/tags',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (15,'2008-10-22',0,'search.advanced',18,5,'search.png','/1/18',1,'search/advancedSearch',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (14,'2008-10-22',0,'task.tasks',2,8,'thread.png','/2',1,'admin/tasks',0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (-1,'2008-12-01',0,'admin.security',2,12,'password.png','/2',1,'admin/security',0);

insert into ld_group
values     (1,'2008-10-22',0,'admin','Group of admins',0);

insert into ld_group
values     (2,'2008-10-22',0,'author','Group of authors',0);

insert into ld_group
values     (3,'2008-10-22',0,'guest','Group of guest',0);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type)
values     (1,'2008-10-22',0,'admin','d033e22ae348aeb566fc214aec3585c4da997','Admin','Admin','','','','','en','admin@admin.net','',0);
insert into ld_group
values     (-1,'2008-10-22',0,'_user_1','',1);
insert into ld_usergroup
values (1,1);
insert into ld_usergroup
values (-1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (1,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (2,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (4,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (5,1,1,1,1,1,1,1,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (6,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (7,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (8,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (13,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (14,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (16,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (17,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (19,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (20,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (21,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (23,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (25,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (26,1,1,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (18,1,1,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (15,1,1,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (-1,1,0,0,1,1,0,0,1,1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (1,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (4,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (5,2,1,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (13,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (16,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (19,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (20,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (23,2,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (26,2,1,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (18,2,1,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (15,2,1,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (1,3,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (4,3,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (5,3,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (13,3,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (16,3,0,0,0,0,0,0,0,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (19,3,0,0,0,0,0,0,0,0);
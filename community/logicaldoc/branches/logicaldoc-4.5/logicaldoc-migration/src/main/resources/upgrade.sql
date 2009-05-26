--This script migrates a 4.0.x database to a 4.5 (Community Edition)
alter table ld_attributes drop constraint FKF9B7567E76C86307;
alter table ld_document drop constraint FK75ED9C0276C86307;
alter table ld_document drop constraint FK75ED9C027C565C60;
alter table ld_document_ext drop constraint FK4E0884647C693DFD;
alter table ld_keyword drop constraint FK55BBDA227C693DFD;
alter table ld_link drop constraint FK1330661CADD6217;
alter table ld_link drop constraint FK1330661CADD6216;
alter table ld_menugroup drop constraint FKB4F7F679AA456AD1;
alter table ld_usergroup drop constraint FK2435438DB8B12CA9;
alter table ld_usergroup drop constraint FK2435438D76F11EA1;
alter table ld_version drop constraint FK9B3BD9117C693DFD;
alter table ld_generic_ext drop constraint FK913AF772CF5D92AF;
alter table ld_group_ext drop constraint FKB728EA5A76F11EA1;
alter table ld_ticket drop constraint FK_TICKET_DOC;
alter table ld_ticket drop constraint FK_TICKET_USER;
alter table ld_menugroup drop constraint FK_MENUGROUP_GROUP;
alter table ld_userdoc drop constraint FK_USERDOC_DOC;
alter table ld_userdoc drop constraint FK_USERDOC_USER;
alter table ld_article drop constraint FK_ARTICLE_DOC;
alter table ld_menu drop constraint FK_MENU_PARENT;
alter table ld_history drop constraint FK_HISTORY_DOC;
alter table ld_history drop constraint FK_HISTORY_USER;
alter table ld_version drop constraint FK_VERSION_USER;

drop index  AK_DOCUMENT;
drop index  AK_USER;
drop index  AK_GROUP;  
drop index  AK_TICKET;
drop index  AK_USERGROUP;
drop index  AK_LINK;
drop index  AK_TEMPLATE;
drop index  AK_GENERIC;

drop table ld_article;

alter table ld_document add column ld_fileversion   VARCHAR(10);
alter table ld_document add column ld_creator       VARCHAR(255);
alter table ld_document add column ld_creatorid  BIGINT  default 0  NOT NULL;
alter table ld_document drop column ld_checkoutuserid;
alter table ld_document add column  ld_lockuserid    BIGINT;
alter table ld_document add column ld_signed        INT   default 0 NOT NULL;
alter table ld_document add column ld_exportstatus  INT   default 0 NOT NULL;
alter table ld_document add column ld_exportid      BIGINT;
alter table ld_document add column ld_exportname    VARCHAR(255);
alter table ld_document add column ld_exportversion VARCHAR(10);

create table ld_tag (ld_docid bigint not null, ld_tag varchar(255));
insert into ld_tag(ld_docid,ld_tag) select ld_docid,ld_keyword from ld_keyword;
drop table ld_keyword;

alter table ld_menugroup add column  ld_sign  INT default 0 NOT NULL;
alter table ld_menugroup add column  ld_archive INT default 0 NOT NULL;
alter table ld_menugroup drop primary key;
alter table ld_menugroup drop constraint ld_menugroup_pkey;
alter table ld_menugroup add primary key (ld_menuid,ld_groupid,ld_write,ld_addchild,ld_managesecurity,ld_manageimmutability,ld_delete,ld_rename,ld_bulkimport,ld_bulkexport,ld_sign,ld_archive ); 

alter table ld_systemmessage drop column  ld_sentdate;
alter table ld_systemmessage add column  ld_sentdate  TIMESTAMP DEFAULT '2000-01-01' NOT NULL;

alter table ld_user add column  ld_enabled INT default 1 NOT NULL;
alter table ld_user add column  ld_state VARCHAR(255);
alter table ld_user add column  ld_telephone2 VARCHAR(255);
alter table ld_user add column  ld_passwordchanged TIMESTAMP;
alter table ld_user add column  ld_passwordexpires INT default 0 NOT NULL;

drop table ld_version;
create table ld_version (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_immutable int not null, ld_customid varchar(4000), ld_title varchar(255), ld_version varchar(10), ld_fileversion varchar(10), ld_date timestamp, ld_creation timestamp, ld_publisher varchar(255), ld_publisherid bigint not null,  ld_creator varchar(255), ld_creatorid bigint not null, ld_status int, ld_type varchar(255), ld_lockuserid bigint, ld_source varchar(255), ld_sourceauthor varchar(255), ld_sourcedate timestamp, ld_sourceid varchar(4000), ld_sourcetype varchar(255), ld_object varchar(4000), ld_coverage varchar(255), ld_language varchar(10), ld_filename varchar(255), ld_filesize bigint, ld_indexed int not null, ld_signed int not null, ld_digest varchar(255), ld_recipient varchar(4000), ld_folderid bigint, ld_foldername varchar(4000), ld_templateid bigint, ld_templatename varchar(4000), ld_tgs varchar(4000), ld_username varchar(255), ld_userid bigint, ld_versiondate timestamp, ld_comment varchar(4000),ld_event varchar(255), ld_documentid bigint, ld_exportstatus int not null, ld_exportid bigint, ld_exportname varchar(255), ld_exportversion varchar(10), primary key (ld_id));
insert into ld_version(ld_id, ld_lastmodified, ld_deleted, ld_immutable, ld_customid, ld_title, ld_version, ld_fileversion, ld_date, ld_creation, ld_publisher, ld_publisherid,  ld_creator, ld_creatorid, ld_status, ld_type, ld_lockuserid, ld_source, ld_sourceauthor, ld_sourcedate, ld_sourceid, ld_sourcetype, ld_object, ld_coverage, ld_language, ld_filename, ld_filesize, ld_indexed, ld_signed, ld_digest, ld_recipient, ld_folderid, ld_foldername, ld_templateid , ld_templatename, ld_tgs, ld_username, ld_userid, ld_versiondate, ld_comment,ld_event, ld_documentid, ld_exportstatus, ld_exportid, ld_exportname, ld_exportversion)
select ld_id, ld_lastmodified, ld_deleted, ld_immutable, ld_customid, ld_title, ld_version, ld_fileversion, ld_date, ld_creation, ld_publisher, ld_publisherid,  ld_creator, ld_creatorid, ld_status, ld_type, ld_lockuserid, ld_source, ld_sourceauthor, ld_sourcedate, ld_sourceid, ld_sourcetype, ld_object, ld_coverage, ld_language, ld_filename, ld_filesize, ld_indexed, ld_signed, ld_digest, ld_recipient, ld_folderid, null, ld_templateid, null, null, null, ld_publisherid, '2000-01-01', '','event.stored', ld_id, ld_exportstatus, ld_exportid, ld_exportname, ld_exportversion from ld_document;
create table ld_version_ext (ld_versionid bigint not null, ld_value varchar(4000), ld_name varchar(255) not null, primary key (ld_versionid, ld_name));

create table ld_dcomment (ld_threadid bigint not null, ld_replyto int, ld_replypath varchar(255), ld_userid bigint not null, ld_username varchar(255), ld_date timestamp, ld_subject varchar(255), ld_body varchar(4000), ld_deleted int not null, ld_id int not null, primary key (ld_threadid, ld_id));
create table ld_dthread (ld_id bigint not null, ld_lastmodified timestamp not null, ld_deleted int not null, ld_docid bigint not null, ld_creation timestamp, ld_creatorid bigint not null, ld_creatorname varchar(255), ld_lastpost timestamp, ld_subject varchar(255), ld_replies int, ld_views int, primary key (ld_id));

alter table ld_attributes add constraint FKF9B7567E76C86307 foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_document add constraint FK75ED9C0276C86307 foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_document add constraint FK75ED9C027C565C60 foreign key (ld_folderid) references ld_menu(ld_id);
alter table ld_document_ext add constraint FK4E0884647C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_generic_ext add constraint FK913AF772CF8376C7 foreign key (ld_genid) references ld_generic(ld_id);
alter table ld_group_ext add constraint FKB728EA5A76F11EA1 foreign key (ld_groupid) references ld_group(ld_id);
alter table ld_tag add constraint FK55BBDA227C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_link add constraint FK1330661CADD6217 foreign key (ld_docid2) references ld_document(ld_id);
alter table ld_link add constraint FK1330661CADD6216 foreign key (ld_docid1) references ld_document(ld_id);
alter table ld_menugroup add constraint FKB4F7F679AA456AD1 foreign key (ld_menuid) references ld_menu(ld_id);
alter table ld_usergroup add constraint FK2435438DB8B12CA9 foreign key (ld_userid) references ld_user(ld_id);
alter table ld_usergroup add constraint FK2435438D76F11EA1 foreign key (ld_groupid) references ld_group(ld_id);
alter table ld_version add constraint FK9B3BD9118A053CE foreign key (ld_documentid) references ld_document(ld_id);
alter table ld_version_ext add constraint FK78C3A1F3B90495EE foreign key (ld_versionid) references ld_version(ld_id);
alter table ld_dcomment add constraint FKF2C40628DBB5BF4 foreign key (ld_threadid) references ld_dthread(ld_id);

alter table ld_ticket add constraint FK_TICKET_DOC foreign key (ld_docid) references ld_document(ld_id) on delete cascade;
alter table ld_ticket add constraint FK_TICKET_USER foreign key (ld_userid) references ld_user(ld_id) on delete cascade;
alter table ld_menugroup add constraint FK_MENUGROUP_GROUP foreign key (ld_groupid) references ld_group(ld_id) on delete cascade;
alter table ld_userdoc add constraint FK_USERDOC_DOC foreign key (ld_docid) references ld_document(ld_id) on delete cascade;
alter table ld_userdoc add constraint FK_USERDOC_USER foreign key (ld_userid) references ld_user(ld_id) on delete cascade;
alter table ld_menu add constraint FK_MENU_PARENT foreign key (ld_parentid) references ld_menu(ld_id) on delete cascade;
alter table ld_history add constraint FK_HISTORY_DOC foreign key (ld_docid) references ld_document(ld_id) on delete cascade;

create unique index  AK_DOCUMENT on ld_document (ld_customid);
create unique index  AK_USER on ld_user (ld_username);
create unique index  AK_GROUP on ld_group (ld_name);  
create unique index  AK_TICKET on ld_ticket (ld_ticketid);
create unique index  AK_USERGROUP on ld_usergroup (ld_groupid, ld_userid);
create unique index  AK_LINK on ld_link (ld_docid1, ld_docid2, ld_type);
create unique index  AK_TEMPLATE on ld_template (ld_name);
create unique index  AK_GENERIC on ld_generic (ld_type, ld_subtype);
create unique index  AK_VERSION on ld_version (ld_documentid, ld_version);
alter table ld_history add constraint FK_HISTORY_USER foreign key (ld_userid) references ld_user(ld_id);
alter table ld_version add constraint FK_VERSION_USER foreign key (ld_userid) references ld_user(ld_id);

alter table ld_emailaccount add column ld_extracttags int default 0 not null;
update ld_emailaccount set ld_extracttags=ld_extractkeywords;
alter table ld_emailaccount drop column ld_extractkeywords;


--Rename event codes
update ld_history set ld_event='event.stored' where ld_event='history.stored';
update ld_history set ld_event='event.changed' where ld_event='history.changed';
update ld_history set ld_event='event.checkedin' where ld_event='history.checkedin';
update ld_history set ld_event='event.checkedout' where ld_event='history.checkedout';
update ld_history set ld_event='event.makeimmutable' where ld_event='history.makeimmutable';
update ld_history set ld_event='event.renamed' where ld_event='history.renamed';
update ld_history set ld_event='event.downloaded' where ld_event='history.downloaded';
update ld_history set ld_event='event.moved' where ld_event='history.moved';
update ld_history set ld_event='event.locked' where ld_event='history.locked';
update ld_history set ld_event='event.unlocked' where ld_event='history.unlocked';
update ld_history set ld_event='event.archived' where ld_event='history.archived';

--All documents must be reindexed
update ld_document set ld_indexed=0;

--Adjust menu hierarchy
insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (9,'2008-12-01',0,'admin.security',2,3,'password.png','/2',1,null,0);
insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (-2,'2008-12-01',0,'system',2,1,'system.png','/2',1,null,0);
insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (11,'2008-10-22',0,'menu.gui',-2,30,'gui.png','/-2',1,'admin/gui',0);
insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (10,'2008-10-22',0,'parameters',-2,35,'parameters.png','/-2',1,'admin/parameters',0);
insert into ld_menu   
		   (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (-20,'2009-02-23',0,'logicaldoc-external-authentication.ldap',9,10,'ldap.png','/9',3,'external-authentication/ldap',0);


insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport, ld_sign, ld_archive)
values     (-2,1,0,0,1,1,0,0,1,1,1,1);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport, ld_sign, ld_archive)
values     (9,1,0,0,1,1,0,0,1,1,1,1);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport, ld_sign, ld_archive)
values     (11,1,0,0,1,1,0,0,1,1,1,1);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport, ld_sign, ld_archive)
values     (10,1,0,0,1,1,0,0,1,1,1,1);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport, ld_sign, ld_archive) 
values     (-20,1,1,1,1,1,1,1,1,1,1,1);


update ld_menu 
   set ld_parentid = 9, ld_sort=5, ld_path='/9'
 where ld_id=6;
update ld_menu 
   set ld_parentid = 9, ld_sort=10, ld_path='/9'
 where ld_id=7;
update ld_menu 
   set ld_parentid = 9, ld_sort=15, ld_path='/9'
 where ld_id=-1;
update ld_menu 
   set ld_parentid = 2, ld_sort=10, ld_path='/2'
 where ld_id=14;
update ld_menu 
   set ld_parentid = -2, ld_sort=5, ld_path='/-2'
 where ld_id=17;
update ld_menu 
   set ld_parentid = -2, ld_sort=10, ld_path='/-2'
 where ld_id=8;
update ld_menu 
   set ld_parentid = -2, ld_sort=15, ld_path='/-2'
 where ld_id=25;
update ld_menu 
   set ld_parentid = -2, ld_sort=25, ld_path='/-2'
 where ld_id=23;
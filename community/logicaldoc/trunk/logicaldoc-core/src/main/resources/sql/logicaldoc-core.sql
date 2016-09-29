-- datetime -> timestamp 

create table ld_bookmark (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                          ld_deleted int not null, ld_tenantid bigint not null, ld_userid bigint not null, 
                          ld_docid bigint not null, ld_title varchar(255) not null, ld_description varchar(4000), 
                          ld_position int not null, ld_filetype varchar(40), ld_type int not null, primary key (ld_id));
create table ld_document (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null, 
						  ld_deleted int not null, ld_tenantid bigint not null, ld_immutable int not null,
                          ld_customid varchar(200), ld_title varchar(255), ld_version varchar(255), ld_fileversion varchar(10), ld_date timestamp, 
                          ld_creation timestamp not null, ld_publisher varchar(255), ld_publisherid bigint not null, ld_creator varchar(255),
                          ld_creatorid bigint not null, ld_status int, ld_type varchar(255), ld_lockuserid bigint, ld_lockuser varchar(255), ld_source varchar(4000),
                          ld_sourceauthor varchar(255), ld_sourceid varchar(1000),
                          ld_object varchar(1000), ld_coverage varchar(255), ld_language varchar(10), ld_filename varchar(255), 
                          ld_filesize bigint, ld_indexed int not null, ld_barcoded int not null, ld_signed int not null, ld_stamped int not null, 
                          ld_digest varchar(255), ld_recipient varchar(1000), ld_folderid bigint, ld_templateid bigint, ld_exportstatus int not null, 
                          ld_exportid bigint, ld_exportname varchar(255), ld_exportversion varchar(10), ld_docref bigint, ld_docreftype varchar(255),
                          ld_deleteuserid bigint, ld_rating int, ld_comment varchar(1000), ld_workflowstatus varchar(1000), 
                          ld_published int not null, ld_startpublishing timestamp, ld_stoppublishing timestamp null, ld_transactionid varchar(255), 
                          ld_extresid varchar(255), ld_tgs varchar(1000), ld_pages int not null, ld_nature int not null,
                          ld_formid bigint, primary key (ld_id));
create table ld_document_ext (ld_docid bigint not null, ld_mandatory int not null, ld_type int not null, 
                              ld_editor int not null, ld_position int not null, ld_stringvalue varchar(4000), 
                              ld_intvalue bigint, ld_doublevalue float, ld_datevalue timestamp null, 
                              ld_name varchar(255) not null, ld_label varchar(255), ld_setid bigint, 
                              primary key (ld_docid, ld_name));
create table ld_generic (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null, 
                         ld_deleted int not null, ld_tenantid bigint not null, ld_type varchar(255) not null, 
                         ld_subtype varchar(255) not null, ld_qualifier bigint null, ld_string1 varchar(4000), 
                         ld_string2 varchar(4000), ld_string3 varchar(4000), ld_integer1 bigint null, 
                         ld_integer2 bigint null, ld_integer3 bigint null, ld_double1 float, ld_double2 float, 
                         ld_date1 timestamp null, ld_date2 timestamp null, primary key (ld_id));
create table ld_generic_ext (ld_genid bigint not null, ld_mandatory int not null, ld_type int not null, 
                             ld_editor bigint not null, ld_position int not null, ld_stringvalue varchar(4000), 
                             ld_intvalue bigint, ld_doublevalue float, ld_datevalue timestamp null, 
                             ld_name varchar(255) not null, ld_label varchar(255), ld_setid bigint, 
                             primary key (ld_genid, ld_name));
create table ld_group (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null, 
                       ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255) not null, 
                       ld_description varchar(255), ld_type int not null, primary key (ld_id));
create table ld_history (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                         ld_deleted int not null, ld_tenantid bigint not null, ld_docid bigint, 
                         ld_folderid bigint not null, ld_userid bigint, ld_date timestamp, ld_username varchar(255), ld_event varchar(255), 
                         ld_comment varchar(4000), ld_version varchar(255), ld_title varchar(255), ld_titleold varchar(255), ld_path varchar(4000), 
                         ld_pathold varchar(4000), ld_notified int not null, ld_sessionid varchar(255), ld_new int, ld_filename varchar(255),
                         ld_filenameold varchar(255), primary key (ld_id));
create table ld_tag (ld_docid bigint, ld_tenantid bigint not null, ld_tag varchar(255));
create table ld_link (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,  
                      ld_deleted int not null, ld_tenantid bigint not null, 
                      ld_type varchar(255) not null, ld_docid1 bigint, 
                      ld_docid2 bigint, primary key (ld_id));
create table ld_menu (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null, 
                      ld_deleted int not null, ld_tenantid bigint not null, ld_text varchar(255), 
                      ld_parentid bigint not null, ld_securityref bigint, ld_icon varchar(255), 
                      ld_type int not null, ld_description varchar(4000), ld_position int not null, 
                      primary key (ld_id));
create table ld_menugroup (ld_menuid bigint not null, ld_groupid bigint not null, ld_write int not null, constraint PK_LD_MENUGROUP primary key (ld_menuid, ld_groupid));
create table ld_recipient (ld_messageid bigint not null, ld_name varchar(255) not null, ld_address varchar(255) not null, ld_mode varchar(255) not null, ld_type int not null, ld_read int not null);
create table ld_systemmessage (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                               ld_deleted int not null, ld_tenantid bigint not null, 
                               ld_author varchar(255), ld_messagetext varchar(4000), ld_subject varchar(255), 
                               ld_sentdate timestamp not null, ld_datescope int, ld_prio int, ld_confirmation int, 
                               ld_lastnotified timestamp, ld_status int not null, ld_trials int,
                               ld_type int not null, ld_html int not null, primary key (ld_id));
create table ld_template (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                          ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255) not null, 
                          ld_description varchar(2000), ld_readonly int not null, ld_type int not null, 
                          primary key (ld_id));                         
create table ld_template_ext (ld_templateid bigint not null, ld_mandatory int not null, ld_type int not null, 
                              ld_editor int not null, ld_position int not null, ld_stringvalue varchar(4000), 
                              ld_intvalue bigint, ld_doublevalue float, ld_datevalue timestamp null, 
                              ld_name varchar(255) not null, ld_label varchar(255), ld_setid bigint,
                              primary key (ld_templateid, ld_name));
create table ld_attributeset (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                              ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255) not null, 
                              ld_description varchar(2000), ld_readonly int not null, ld_type int not null, 
                              primary key (ld_id));
create table ld_attributeset_ext (ld_attsetid bigint not null, ld_mandatory int not null, ld_type int not null, 
                                  ld_editor int not null, ld_position int not null, ld_stringvalue varchar(4000), 
                                  ld_intvalue bigint, ld_doublevalue float, ld_datevalue timestamp null, 
                                  ld_name varchar(255) not null, ld_label varchar(255), ld_setid bigint, 
                                  primary key (ld_setid, ld_name));
create table ld_ticket (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                        ld_deleted int not null, ld_tenantid bigint not null, ld_ticketid varchar(255) not null, 
                        ld_docid bigint not null, ld_userid bigint not null, ld_type int not null, 
                        ld_creation timestamp not null, ld_expired timestamp, ld_count int not null, ld_suffix varchar(255), 
                        primary key (ld_id));
create table ld_user (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                      ld_deleted int not null, ld_tenantid bigint not null, ld_enabled int not null, 
                      ld_username varchar(255) not null, ld_password varchar(255), ld_passwordmd4 varchar(255), ld_name varchar(255), ld_firstname varchar(255), 
                      ld_street varchar(255), ld_postalcode varchar(255), ld_city varchar(255), ld_country varchar(255), 
                      ld_state varchar(255), ld_language varchar(10), ld_email varchar(255), ld_emailsignature varchar(4000), ld_telephone varchar(255), 
                      ld_telephone2 varchar(255), ld_type int not null, ld_passwordchanged timestamp, ld_passwordexpires int not null,
                      ld_source int not null, ld_quota bigint not null, ld_cert varchar(4000), ld_certsubject varchar(255), ld_certdigest varchar(255),
                      ld_key varchar(4000), ld_keydigest varchar(255), ld_welcomescreen int null, ld_ipwhitelist varchar(1000), 
                      ld_ipblacklist varchar(1000), ld_passwordexpired int not null,  primary key (ld_id));
create table ld_user_history (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                              ld_deleted int not null, ld_tenantid bigint not null, ld_userid bigint, 
                              ld_date timestamp, ld_username varchar(255), ld_event varchar(255), 
                              ld_comment varchar(4000), ld_notified int not null, ld_sessionid varchar(255), 
                              ld_new int, ld_filename varchar(255), primary key (ld_id));
create table ld_usergroup (ld_groupid bigint not null, ld_userid bigint not null, primary key (ld_groupid, ld_userid));
create table ld_version (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                         ld_deleted int not null, ld_tenantid bigint not null, ld_immutable int not null, ld_customid varchar(200),
                         ld_title varchar(255), ld_version varchar(255), ld_fileversion varchar(10), ld_date timestamp, ld_creation timestamp, ld_publisher varchar(255),
                         ld_publisherid bigint not null,  ld_creator varchar(255), ld_creatorid bigint not null, ld_status int, ld_type varchar(255),
                         ld_lockuserid bigint, ld_lockuser varchar(255), ld_source varchar(4000), ld_sourceauthor varchar(255), ld_sourceid varchar(1000),
                         ld_object varchar(1000), ld_coverage varchar(255), ld_language varchar(10), ld_filename varchar(255), 
                         ld_filesize bigint, ld_indexed int not null, ld_barcoded int not null, ld_signed int not null, ld_stamped int not null,
                         ld_digest varchar(255), ld_recipient varchar(1000), ld_folderid bigint, ld_foldername varchar(1000), ld_templateid bigint, 
                         ld_templatename varchar(1000), ld_tgs varchar(1000), ld_username varchar(255), ld_userid bigint, ld_versiondate timestamp, 
                         ld_comment varchar(1000),ld_event varchar(255), ld_documentid bigint not null, ld_exportstatus int not null, 
                         ld_exportid bigint, ld_exportname varchar(255), ld_exportversion varchar(10), ld_deleteuserid bigint, 
                         ld_workflowstatus varchar(1000), ld_published int not null, ld_startpublishing timestamp, ld_stoppublishing timestamp null, 
                         ld_transactionid varchar(255), ld_extresid varchar(255), ld_pages int not null, ld_nature int not null,
                         ld_formid bigint, primary key (ld_id));
create table ld_version_ext (ld_versionid bigint not null, ld_mandatory int not null, ld_type int not null, ld_editor int not null, 
                             ld_position int not null, ld_stringvalue varchar(4000), ld_intvalue bigint, ld_doublevalue float, 
                             ld_datevalue timestamp null, ld_name varchar(255) not null, ld_label varchar(255), ld_setid bigint, 
                             primary key (ld_versionid, ld_name));
create table ld_folder (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                        ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255), 
                        ld_parentid bigint not null, ld_securityref bigint, ld_description varchar(4000), 
                        ld_type int not null, ld_creation timestamp, ld_creator varchar(255), ld_creatorid bigint, 
                        ld_templateid bigint, ld_templocked int not null, ld_deleteuserid bigint, ld_position int not null,
                        ld_quotadocs bigint, ld_quotasize bigint, ld_hidden int not null, ld_foldref bigint, 
                        ld_level int, primary key (ld_id));
create table ld_folder_ext (ld_folderid bigint not null, ld_mandatory int not null, ld_type int not null, ld_editor int not null,
                            ld_position int not null, ld_stringvalue varchar(4000), ld_intvalue bigint, ld_doublevalue float, 
                            ld_datevalue timestamp null, ld_name varchar(255) not null, ld_label varchar(255), ld_setid bigint, 
                            primary key (ld_folderid, ld_name));
create table ld_folder_history (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                                ld_deleted int not null, ld_tenantid bigint not null, ld_docid bigint, 
                                ld_folderid bigint not null, ld_userid bigint, ld_date timestamp, ld_username varchar(255), 
                                ld_event varchar(255), ld_comment varchar(4000), ld_version varchar(255), ld_title varchar(255), ld_titleold varchar(255), 
                                ld_path varchar(4000), ld_pathold varchar(4000), ld_notified int not null, ld_sessionid varchar(255),
                                ld_new int, ld_filename varchar(255), ld_filenameold varchar(255), primary key (ld_id));
create table ld_foldergroup (ld_folderid bigint not null, ld_groupid bigint not null, ld_write int not null, 
                             ld_add int not null, ld_security int not null, ld_immutable int not null, ld_delete int not null, 
                             ld_rename int not null, ld_import int not null, ld_export int not null, ld_sign int not null, 
                             ld_archive int not null, ld_workflow int not null, ld_download int not null, ld_calendar int not null,
                             ld_subscription int not null, ld_print int not null,  
                             constraint PK_LD_FOLDERGROUP primary key (ld_folderid, ld_groupid));
create table ld_feedmessage (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                             ld_deleted int not null, ld_tenantid bigint not null, ld_guid varchar(512) null, ld_title varchar(512) null, ld_description  varchar(4000) null, ld_link varchar(512) null, ld_pubdate timestamp, ld_read int not null, primary key (ld_id));
create table ld_rating (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                        ld_deleted int not null, ld_tenantid bigint not null, ld_docid bigint not null, 
                        ld_userid bigint not null, ld_vote int not null, primary key (ld_id));
create table ld_note (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                      ld_deleted int not null, ld_tenantid bigint not null, ld_docid bigint not null, 
                      ld_username varchar(255), ld_userid bigint, ld_date timestamp, 
                      ld_message varchar(4000), ld_snippet varchar(4000), ld_page int not null, primary key (ld_id));
create table ld_messagetemplate (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                                 ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255) not null, ld_language varchar(10) not null,
                                 ld_description varchar(1000), ld_body varchar(4000),
                                 ld_subject varchar(1000), primary key (ld_id));
create table ld_contact (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null, 
                         ld_deleted int not null, ld_tenantid bigint not null, ld_userid bigint null,
                         ld_firstname varchar(255), ld_lastname varchar(255), ld_email varchar(512),
                         ld_company varchar(255), ld_address varchar(512), ld_phone varchar(255),
                         ld_mobile varchar(255), primary key (ld_id));                                 
create table ld_tenant (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null, 
                        ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255),
                        ld_displayname varchar(4000), ld_enabled int not null, ld_expire timestamp null, 
                        ld_street varchar(255), ld_postalcode varchar(255),
                        ld_city varchar(255), ld_country varchar(255), ld_state varchar(255),
                        ld_email varchar(255), ld_telephone varchar(255),
                        ld_maxusers int, ld_maxsessions int, ld_maxrepodocs bigint,
                        ld_maxreposize bigint, ld_type int not null, primary key (ld_id));   
create table ld_sequence (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                          ld_deleted int not null, ld_tenantid bigint not null, ld_name varchar(255) not null,
                          ld_objectid bigint not null, ld_lastreset timestamp null, ld_value bigint not null,
                          primary key (ld_id));
create table ld_extoption (ld_id bigint not null, ld_lastmodified timestamp not null, ld_recordversion bigint not null,
                          ld_deleted int not null, ld_tenantid bigint not null, ld_setid bigint not null,
                          ld_attribute varchar(255) not null, ld_value varchar(255) not null, 
                          ld_label varchar(1000), ld_position int not null,
                          primary key (ld_id));
create table ld_temp (ld_int bigint, ld_date timestamp, ld_string varchar(4000));
create table ld_uniquetag(ld_tag varchar(255), ld_tenantid bigint, ld_count bigint, primary key (ld_tag, ld_tenantid));
create table ld_update (ld_update varchar(255), ld_date timestamp, ld_version varchar(255));


create table hibernate_unique_key (tablename varchar(40) NOT NULL, next_hi bigint NOT NULL);

alter table ld_document add constraint FK75ED9C0276C86307 foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_document add constraint FK75ED9C027C565C60 foreign key (ld_folderid) references ld_folder(ld_id);
alter table ld_document_ext add constraint FK4E0884647C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_generic_ext add constraint FK913AF772CF8376C7 foreign key (ld_genid) references ld_generic(ld_id);
alter table ld_tag add constraint FK55BBDA227C693DFD foreign key (ld_docid) references ld_document(ld_id);
alter table ld_link add constraint FK1330661CADD6217 foreign key (ld_docid2) references ld_document(ld_id);
alter table ld_link add constraint FK1330661CADD6216 foreign key (ld_docid1) references ld_document(ld_id);
alter table ld_usergroup add constraint FK2435438DB8B12CA9 foreign key (ld_userid) references ld_user(ld_id);
alter table ld_usergroup add constraint FK2435438D76F11EA1 foreign key (ld_groupid) references ld_group(ld_id);
alter table ld_version add constraint FK9B3BD9118A053CE foreign key (ld_documentid) references ld_document(ld_id);
alter table ld_version_ext add constraint FK78C3A1F3B90495EE foreign key (ld_versionid) references ld_version(ld_id);
alter table ld_template_ext add constraint FK6BABB84376C86307 foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_recipient add constraint FK406A04126621DEBE foreign key (ld_messageid) references ld_systemmessage(ld_id);

alter table ld_attributeset_ext add constraint FK_ATT_ATTSET foreign key (ld_attsetid) references ld_attributeset(ld_id);
alter table ld_ticket add constraint FK_TICKET_USER foreign key (ld_userid) references ld_user(ld_id) on delete cascade;
alter table ld_menugroup add constraint FK_MENUGROUP_GROUP foreign key (ld_groupid) references ld_group(ld_id) on delete cascade;
alter table ld_menu add constraint FK_MENU_PARENT foreign key (ld_parentid) references ld_menu(ld_id);
alter table ld_folder add constraint FK_FOLDER_PARENT foreign key (ld_parentid) references ld_folder(ld_id);
alter table ld_folder add constraint FK_FOLDER_TEMPLATE foreign key (ld_templateid) references ld_template(ld_id);
alter table ld_folder_ext add constraint FK_FOLDEREXT_FOLDER foreign key (ld_folderid) references ld_folder(ld_id);
alter table ld_foldergroup add constraint FK_FGROUP_FOLDER foreign key (ld_folderid) references ld_folder(ld_id) on delete cascade;
alter table ld_menugroup add constraint FK_MGROUP_MENU foreign key (ld_menuid) references ld_menu(ld_id) on delete cascade;

create unique index AK_DOCUMENT on ld_document (ld_customid, ld_tenantid);
create unique index AK_USER on ld_user (ld_username);
create unique index AK_GROUP on ld_group (ld_name, ld_tenantid);  
create unique index AK_TICKET on ld_ticket (ld_ticketid);
create unique index AK_LINK on ld_link (ld_docid1, ld_docid2, ld_type);
create unique index AK_TEMPLATE on ld_template (ld_name, ld_tenantid);
create unique index AK_GENERIC on ld_generic (ld_type, ld_subtype, ld_qualifier, ld_tenantid);
create unique index AK_VERSION on ld_version (ld_documentid, ld_version);
create unique index AK_RATING on ld_rating (ld_docid, ld_userid);
create unique index AK_MSGTEMPL on ld_messagetemplate (ld_name, ld_language, ld_tenantid);
create unique index AK_TENANT on ld_tenant (ld_name);
create unique index AK_SEQUENCE on ld_sequence (ld_name, ld_objectid, ld_tenantid);
create unique index AK_EXTOPTION on ld_extoption (ld_setid, ld_attribute, ld_value);
create unique index AK_ATTRIBUTESET on ld_attributeset (ld_name, ld_tenantid);

--Prepare some indexes
create index LD_DOC_LUID on ld_document (ld_lockuserid);
create index LD_DOC_FID on ld_document (ld_folderid);
create index LD_DOC_RID on ld_document (ld_docref);
create index LD_DOC_STATUS on ld_document (ld_status);
create index LD_HIST_DOCID on ld_history (ld_docid);
create index LD_HIST_UID on ld_history (ld_userid);
create index LD_HIST_NOT on ld_history (ld_notified);
create index LD_HIST_EVENT on ld_history (ld_event);
create index LD_FHIST_FID on ld_folder_history (ld_folderid);
create index LD_FHIST_NOT on ld_folder_history (ld_notified);
create index LD_TAG_TAG on ld_tag (ld_tag);
create index DOC_EXT_NAME on ld_document_ext (ld_name);

insert into ld_tenant(ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_displayname,ld_type,ld_enabled,ld_expire,ld_recordversion)
values     (1,CURRENT_TIMESTAMP,0,1,'default','Default',0,1,null,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1,CURRENT_TIMESTAMP,0,'/',1,'menu.png',1,1,1,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (2,CURRENT_TIMESTAMP,0,'administration',1,'menu.png',1,1,1,20);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (9,CURRENT_TIMESTAMP,0,'security',2,'menu.png',1,1,1,20);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (8,CURRENT_TIMESTAMP,0,'impex',2,'menu.png',1,1,1,40);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (25,CURRENT_TIMESTAMP,0,'documentmetadata',2,'menu.png',1,1,1,30);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (7,CURRENT_TIMESTAMP,0,'settings',2,'menu.png',1,1,1,60);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (5,CURRENT_TIMESTAMP,0,'frontend',1,'menu.png',1,1,1,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1500,CURRENT_TIMESTAMP,0,'documents',5,'menu.png',1,1,1,50);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1600,CURRENT_TIMESTAMP,0,'history',1500,'menu.png',1,1,1,10);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1605,CURRENT_TIMESTAMP,0,'aliases',1500,'menu.png',1,1,1,10);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1510,CURRENT_TIMESTAMP,0,'search',5,'menu.png',1,1,1,60);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1520,CURRENT_TIMESTAMP,0,'dashboard',5,'menu.png',1,1,1,40);

insert into ld_menu 
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (80,CURRENT_TIMESTAMP,0,'system',2,'system.png',1,1,1,10);

insert into ld_menu 
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (90,CURRENT_TIMESTAMP,0,'reports',2,'reports.png',1,1,1,50);

insert into ld_menu 
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (100,CURRENT_TIMESTAMP,0,'parameters',7,'settings.png',1,1,1,60);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (14,CURRENT_TIMESTAMP,0,'scheduledtasks',80,'menu.png',1,1,1,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (-2,CURRENT_TIMESTAMP,0,'lastchanges',90,'menu.png',1,1,1,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (-3,CURRENT_TIMESTAMP,0,'lockeddocs',90,'menu.png',1,1,1,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (-5,CURRENT_TIMESTAMP,0,'deleteddocs',90,'menu.png',1,1,1,1);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (3,CURRENT_TIMESTAMP,0,'clienandextapps',7,'menu.png',1,1,1,1);

insert into ld_menu 
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (110,CURRENT_TIMESTAMP,0,'mainmenu',5,'menu.png',1,1,1,10);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (16,CURRENT_TIMESTAMP,0,'tools',110,'menu.png',1,1,1,20);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (40,CURRENT_TIMESTAMP,0,'personal',110,'menu.png',1,1,1,10);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_icon,ld_type,ld_tenantid,ld_recordversion,ld_position)
values     (1530,CURRENT_TIMESTAMP,0,'contacts',40,'menu.png',1,1,1,1);

insert into ld_group
           (ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_description,ld_type,ld_recordversion)
values     (1,CURRENT_TIMESTAMP,0,1,'admin','Group of admins',0,1);

insert into ld_group
           (ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_description,ld_type,ld_recordversion)
values     (2,CURRENT_TIMESTAMP,0,1,'author','Group of authors',0,1);

insert into ld_group
           (ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_description,ld_type,ld_recordversion)
values     (3,CURRENT_TIMESTAMP,0,1,'guest','Group of guests',0,1);

insert into ld_group
           (ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_description,ld_type,ld_recordversion)
values     (4,CURRENT_TIMESTAMP,0,1,'poweruser','Group of power users',0,1);

insert into ld_group
           (ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_description,ld_type,ld_recordversion)
values     (-10000,CURRENT_TIMESTAMP,0,1,'publisher','Group of publishers',0,1);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_passwordmd4,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_telephone2,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source,ld_quota,ld_welcomescreen,ld_passwordexpired,ld_tenantid,ld_recordversion)
values     (1,CURRENT_TIMESTAMP,0,1,'admin','d033e22ae348aeb566fc214aec3585c4da997','U8FeEPvxYRhKNCBsLa0K+1rD1tTtR6yctJIwxje2QMwEOlEQx9HuiA==','Admin','Admin','','','','','en','admin@admin.net','','',0,null,0,0,-1,1520,0,1,1);
insert into ld_group
           (ld_id,ld_lastmodified,ld_deleted,ld_tenantid,ld_name,ld_type,ld_recordversion)
values     (-1,CURRENT_TIMESTAMP,0,1,'_user_1',1,1);
insert into ld_usergroup
values (1,1);
insert into ld_usergroup
values (-1,1);
insert into ld_generic (ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_qualifier, ld_string1, ld_integer1, ld_integer2, ld_integer3,ld_tenantid,ld_recordversion)
values (-50, CURRENT_TIMESTAMP, 0, 'usersetting', 'dashlet-1', 1, 0, 1, 0, 0, 1, 1);
insert into ld_generic (ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_qualifier, ld_string1, ld_integer1, ld_integer2, ld_integer3,ld_tenantid,ld_recordversion)
values (-51, CURRENT_TIMESTAMP, 0, 'usersetting', 'dashlet-3', 1, 0, 3, 0, 1, 1, 1);
insert into ld_generic (ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_qualifier, ld_string1, ld_integer1, ld_integer2, ld_integer3,ld_tenantid,ld_recordversion)
values (-52, CURRENT_TIMESTAMP, 0, 'usersetting', 'dashlet-6', 1, 0, 6, 1, 0, 1, 1);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (2,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (14,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (25,4,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (5,2,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (5,3,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (5,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (5,-10000,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1500,2,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1500,3,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1500,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1500,-10000,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1510,2,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1510,3,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1510,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1510,-10000,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1520,2,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1520,3,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1520,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1520,-10000,0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1530,2,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1530,3,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1530,4,0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write) values (1530,-10000,0);

insert into ld_folder (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type,ld_creation,ld_templocked,ld_tenantid,ld_recordversion,ld_position,ld_hidden)
values (5,CURRENT_TIMESTAMP,0,'/',5,1,CURRENT_TIMESTAMP,0,1,1,1,0);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (5,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (5,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (5,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (5,-10000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1);

insert into ld_folder (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type,ld_creation, ld_templocked,ld_tenantid,ld_recordversion,ld_position,ld_hidden)
values (4,CURRENT_TIMESTAMP,0,'Default',5,1,CURRENT_TIMESTAMP,0,1,1,1,0);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (4,2,1,1,0,0,1,1,0,0,0,0,0,1,1,0,1);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (4,3,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (4,4,1,1,0,0,1,1,0,0,0,0,0,1,1,0,1);
insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar, ld_subscription, ld_print)
values (4,-10000,1,1,0,0,1,1,0,0,0,0,0,1,1,0,1);

insert into ld_messagetemplate (ld_id, ld_lastmodified, ld_deleted, ld_name, ld_language, ld_subject, ld_body,ld_tenantid,ld_recordversion)
values(1, CURRENT_TIMESTAMP,0,'task.report','en', '$product - $task',
'$task<br/>
$I18N.get(''startedon''): <b>$DateTool.format($started, true)</b><br/>
$I18N.get(''finishedon''): <b>$DateTool.format($ended, true)</b><br/>
<hr/>
#if( $error )
$I18N.get(''error''): <b>$error</b>
<hr />
#end
$report',1,1);

insert into ld_messagetemplate (ld_id, ld_lastmodified, ld_deleted, ld_name, ld_language, ld_subject, ld_body,ld_tenantid,ld_recordversion)
values(2, CURRENT_TIMESTAMP,0,'psw.rec1','en', '$product - $I18N.get(''emailnotifyaccountobject'')', 
'$I18N.format(''emailnotifyaccount'', $user.fullName) <br/>
$I18N.get(''username''): <b>$user.username</b> <br/>
$I18N.get(''password''): <b>$password</b> <br/>
$I18N.get(''clickhere''): <a href="$url">$url</a>',1,1);

insert into ld_messagetemplate (ld_id, ld_lastmodified, ld_deleted, ld_name, ld_language, ld_subject, ld_body,ld_tenantid,ld_recordversion)
values(3, CURRENT_TIMESTAMP,0,'psw.rec2','en', '$product - $I18N.get(''passwordrequest'')',
'$product - $I18N.get(''passwordrequest'') <br/>
$I18N.get(''clickhere''): <a href="$url">$url</a>',1,1);


insert into ld_attributeset
			(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description, ld_readonly, ld_type, ld_tenantid, ld_recordversion)
values (-1,CURRENT_TIMESTAMP,0,'default','default',1,0,1,1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,0,'source', 'Source', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,1,'sourceAuthor', 'Author', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,2,'sourceId', 'Original ID', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,3,'sourceType', 'Type', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,4,'object', 'Object', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,5,'coverage', 'Coverage', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,0,6,'recipient', 'Recipient', 0, -1);
insert into ld_attributeset_ext(ld_attsetid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
values (-1,0,3,7,'sourceDate', 'Date', 0, -1);

insert into ld_template
			(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description, ld_readonly, ld_type, ld_tenantid, ld_recordversion)
values (-1,CURRENT_TIMESTAMP,0,'default','default',0,0,1,1);

insert into ld_template_ext(ld_templateid, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid)
select -1, ld_mandatory, ld_type, ld_position, ld_name, ld_label, ld_editor, ld_setid from ld_attributeset_ext where ld_setid=-1;


insert into hibernate_unique_key(tablename, next_hi) values ('ld_document', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_bookmark', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_generic', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_group', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_history', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_link', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_menu', 5000);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_systemmessage', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_template', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_ticket', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_user', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_user_history', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_version', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_folder', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_folder_history', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_feedmessage', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_rating', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_note', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_messagetemplate', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_tenant', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_sequence', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_extoption', 100);
insert into hibernate_unique_key(tablename, next_hi) values ('ld_attributeset', 100);
insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source,ld_quota,ld_quotacount)
values     (2,'2008-10-22',0,1,'boss','d033e22ae348aeb566fc214aec3585c4da997','Meschieri','Marco','','','','','it','m.meschieri@logicalobjects.it','',0,null,0,0,-1,0);
insert into ld_group
values     (-2,'2008-10-22',0,'_user_2','',1);
insert into ld_usergroup
values (-2,2);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source,ld_quota,ld_quotacount)
values     (3,'2008-10-22',0,1,'sebastian','d033e22ae348aeb566fc214aec3585c4da997','Sebastian','Stein','','','','','de','seb_stein@gmx.de','',0,null,0,0,-1,0);
insert into ld_group
values     (-3,'2008-10-22',0,'_user_3','',1);
insert into ld_usergroup
values (-3,3);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source,ld_quota,ld_quotacount)
values     (4,'2008-10-22',0,1,'author','d033e22ae348aeb566fc214aec3585c4da997','Author','Author','','','','','de','author@acme.com','',0,null,0,0,-1,0);
insert into ld_group
values     (-4,'2008-10-22',0,'_user_4','',1);
insert into ld_usergroup
values (-4,4);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source,ld_quota,ld_quotacount)
values     (5,'2008-10-22',0,0,'test','d033e22ae348aeb566fc214aec3585c4da997','test','Test','','','','','de','test@acme.com','',0,null,0,0,-1,0);
insert into ld_group
values     (-5,'2008-10-22',0,'_user_5','',1);
insert into ld_usergroup
values (-5,5);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (99,'2008-10-22',0,'menu.admin',5,5);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (100,'2008-10-22',0,'menu.adminxxx',5,3);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (101,'2008-10-22',0,'text',100,3);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (102,'2008-10-22',0,'menu.admin',101,5);
insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type,ld_description)
values     (103,'2008-10-22',0,'menu.admin',101,3,'description');

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (1000,'2008-10-22',1,'menu.admin.1000',5,5);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (1100,'2008-10-22',1,'menu.admin.1100',1000,5);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (1200,'2009-10-19',0,'test',5,3);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (1201,'2009-10-19',0,'abc',1200,3);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (1202,'2009-10-19',0,'xyz',1201,3);

insert into ld_folder
           (ld_id,ld_lastmodified,ld_deleted,ld_name,ld_parentid,ld_type)
values     (1203,'2009-10-19',0,'qqqq',1201,3);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (100,1,1,0,0,0,0,0,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (100,3,1,1,0,0,0,0,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (100,-3,1,1,0,0,0,0,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (100,-4,1,1,0,0,0,0,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (103,1,1,1,0,0,1,1,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (103,2,1,0,0,0,0,0,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (99,1,0,0,0,0,0,0,0,0,0,0,0,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (1200,1,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (1201,1,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (1202,1,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_foldergroup
			   (ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download)
values     (1203,1,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_usergroup
           (ld_userid,ld_groupid)
values     (3,1);

insert into ld_usergroup
           (ld_userid,ld_groupid)
values     (3,2);

insert into ld_usergroup
           (ld_userid,ld_groupid)
values     (4,2);

insert into ld_usergroup
           (ld_userid,ld_groupid)
values     (5,3);

insert into ld_group
values     (10,'2008-10-22',0,'testGroup','Group for tests',0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_signed,ld_creator,ld_creatorid,ld_exportstatus,ld_barcoded)
values     (1,'2008-10-22',0,0,'a','testDocname','1.0','2006-12-19','2006-12-19','myself',1,0,'PDF',1,'source','sourceauthor1','2008-12-19','sourcetype1','coverage1','en','pippo',1356,1,103,0,'',1,0,0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_signed,ld_creator,ld_creatorid,ld_exportstatus,ld_docref,ld_barcoded)
values     (2,'2008-10-22',0,0,'b','testDocname2','2.0','2006-12-19','2006-12-19','myself',1,1,'PDF',3,'source1','sourceauthor2','2008-12-19','sourcetype2','coverage2','en','pluto',122345,0,103,0,'',1,0,1,0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_signed,ld_creator,ld_creatorid,ld_exportstatus,ld_deleteuserid,ld_barcoded)
values     (3,'2010-04-02',1,0,'c','DELETED 1','testDocVer1','2006-12-19','2006-12-19','myself',1,1,'PDF',3,'source2','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,1,99,0,'',1,0,1,0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_signed,ld_creator,ld_creatorid,ld_exportstatus,ld_deleteuserid,ld_barcoded)
values     (4,'2010-04-04',1,0,'d','DELETED 2','testDocVer2','2006-12-19','2006-12-19','myself',1,1,'TXT',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',122345,1,1100,0,'',1,0,1,0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_signed,ld_creator,ld_creatorid,ld_exportstatus,ld_deleteuserid,ld_barcoded)
values     (5,'2010-04-01',1,0,'f','DELETED 3','testDocVer3','2006-12-19','2006-12-19','myself',1,1,'DOC',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','paperino',122345,1,99,0,'',1,0,1,0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_signed,ld_creator,ld_creatorid,ld_exportstatus,ld_deleteuserid,ld_barcoded)
values     (6,'2010-04-01',1,0,'g','DELETED 4','testDocVer4','2006-12-19','2006-12-19','myself',1,1,'TIFF',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','topolino',122345,1,1100,0,'',1,0,2,0);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_deleted,ld_ticketid,ld_docid,ld_userid,ld_type,ld_creation,ld_expired,ld_count)
values     (1,'2008-10-22',0,'1',1,1,0,'2011-01-01','2011-01-02',0);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_deleted,ld_ticketid,ld_docid,ld_userid,ld_type,ld_creation,ld_expired,ld_count)
values     (2,'2008-10-22',0,'2',2,3,0,'2011-01-01','2011-01-02',0);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_deleted,ld_ticketid,ld_docid,ld_userid,ld_type,ld_creation,ld_expired,ld_count)
values     (3,'2008-12-22',0,'3',1,3,0,'2011-01-01','2011-01-02',0);

insert into ld_userdoc
           (ld_id,ld_lastmodified,ld_deleted,ld_docid,ld_userid,ld_date)
values     (1,'2008-12-22',0,1,1,'2006-12-17');

insert into ld_userdoc
           (ld_id,ld_lastmodified,ld_deleted,ld_docid,ld_userid,ld_date)
values     (2,'2008-10-22',0,2,1,'2006-12-22');

insert into ld_version(ld_id, ld_documentid, ld_version, ld_fileversion, ld_username, ld_userid, ld_versiondate, ld_comment, ld_lastmodified, ld_deleted, ld_immutable, ld_creation, ld_publisherid, ld_indexed, ld_signed, ld_status, ld_filesize, ld_folderid,ld_creator,ld_creatorid,ld_exportstatus,ld_barcoded)
values     (1,1,'testVersion','testVersion','testUser',1,'2006-12-19','testComment','2009-02-09',0,0,'2009-02-09',1,0,0,0,0,5,'',1,0,0);

insert into ld_version(ld_id, ld_documentid, ld_version, ld_fileversion, ld_username, ld_userid, ld_versiondate, ld_comment, ld_lastmodified, ld_deleted, ld_immutable, ld_creation, ld_publisherid, ld_indexed, ld_signed, ld_status, ld_filesize, ld_folderid,ld_creator,ld_creatorid,ld_exportstatus,ld_barcoded)
values     (2,1,'testVersion2','testVersion2','testUser',1,'2006-12-20','testComment','2009-02-09',0,0,'2009-02-09',1,0,0,0,0,5,'',1,0,0);

insert into ld_tag
values     (1,'abc');

insert into ld_tag
values     (1,'def');

insert into ld_tag
values     (1,'ghi');

insert into ld_tag
values     (2,'ask');

insert into ld_tag
values     (3,'ask');

insert into ld_history 
				(ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_folderid, ld_userid, ld_date, ld_username, ld_event, ld_comment, ld_version, ld_notified)
values     (1,'2008-10-22',0,1,5,1,'2006-12-20','author','data test 01','reason test 01','1.0',0);

insert into ld_history 
			    (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_folderid, ld_userid, ld_date, ld_username, ld_event, ld_comment, ld_version, ld_notified)
values     (2,'2008-10-22',0,2,5,1,'2006-12-25','author','data test 02','reason test 02','1.0',0);

insert into ld_history 
			   (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_folderid, ld_userid, ld_date, ld_username, ld_event, ld_comment, ld_version, ld_notified)
values     (3,'2008-10-22',0,null,5,3,'2006-12-27','sebastian','data test 03','reason test 03','1.0',1);

insert into ld_systemmessage
				(ld_id, ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type)
values     (1,'2008-10-22',0,'admin','message text1','subject1','2008-10-22',5,3,1,0,'2009-10-29',0,3,1);

insert into ld_systemmessage
				(ld_id, ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type)
values     (2,'2009-10-29',0,'admin','message text2','subject2','2009-10-29',5,3,1,0,'2009-10-29',0,3,1);

insert into ld_systemmessage
				(ld_id, ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type)
values     (3,'2009-10-29',0,'admin','message text3','subject3','2009-10-29',5,3,1,0,'2009-10-29',0,3,0);

insert into ld_recipient
			(ld_messageid, ld_name, ld_address, ld_mode, ld_type)
values     (1,'sebastian','sebastian','pippo',0);

insert into ld_recipient
			(ld_messageid, ld_name, ld_address, ld_mode, ld_type)
values     (3,'sebastian','sebastian','CC',0);

insert into ld_recipient
			(ld_messageid, ld_name, ld_address, ld_mode, ld_type)
values     (1,'marco','marco@acme.com','sms',1);

insert into ld_recipient
			(ld_messageid, ld_name, ld_address, ld_mode, ld_type)
values     (2,'marco','marco@acme.com','CCN',1);

insert into ld_recipient
			(ld_messageid, ld_name, ld_address, ld_mode, ld_type)
values     (3,'paperino','topolino','sms',2);

insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (1,'2008-10-22',0,1,2,'test');
insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (2,'2008-10-22',0,2,1,'xyz');
insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (3,'2008-10-22',0,1,2,'xxx');
insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (4,'2008-10-22',0,2,1,'');

insert into ld_template (ld_id, ld_lastmodified,ld_deleted, ld_name, ld_description, ld_readonly)
values (1, '2008-11-07',0,'test1','test1_desc',0);
insert into ld_template_ext (ld_templateid, ld_mandatory, ld_position, ld_type, ld_stringvalue, ld_name)
values (1, 0, 0, 0, 'val1', 'attr1');

insert into ld_template (ld_id, ld_lastmodified,ld_deleted, ld_name, ld_description, ld_readonly)
values (2, '2008-11-07',0,'test2','test2_desc',0);

insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(1, '2008-11-19',0,'a','a1','str1','str2',0,1,1.5,1.6,'2008-11-20','2008-11-20');
insert into ld_generic_ext(ld_genid, ld_mandatory, ld_type, ld_position, ld_stringvalue, ld_name)
values(1, 0, 0, 0, 'val1','att1');
insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(2, '2008-11-19',0,'a','a2','str1','str2',10,11,1.5,1.6,'2008-11-20','2008-11-20');
insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(3, '2008-11-19',1,'a.3','a2.3','str1','str2',10,11,1.5,1.6,'2008-11-20','2008-11-20');
insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(4, '2010-04-23',0,'sequence','customid-year_seq','str1','str2',10,11,1.5,1.6,'2008-11-20','2008-11-20');
insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(5, '2010-04-23',0,'sequence','customid-month_seq','str1','str2',10,11,1.5,1.6,'2008-11-20','2008-11-20');

insert into ld_dthread (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_creation, ld_creatorid, ld_creatorname, ld_lastpost, ld_subject, ld_replies, ld_views)
values(1, '2009-04-01',0,1,'2009-04-01',1,'Admin','2009-04-03','subject',2,1);

insert into ld_dcomment (ld_threadid, ld_id, ld_replyto, ld_userid, ld_username, ld_date, ld_subject, ld_body, ld_deleted, ld_replypath)
values(1,0,null,1,'Admin','2009-04-01','subject','body1',0,'/');
insert into ld_dcomment (ld_threadid, ld_id, ld_replyto, ld_userid, ld_username, ld_date, ld_subject, ld_body, ld_deleted, ld_replypath)
values(1,1,null,1,'Admin','2009-04-02','RE: subject','body2',0,'/');

insert into ld_dthread (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_creation, ld_creatorid, ld_creatorname, ld_lastpost, ld_subject, ld_replies, ld_views)
values(2, '2009-05-01',0,1,'2009-04-01',1,'Admin','2009-04-03','subject',2,1);

insert into ld_dthread (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_creation, ld_creatorid, ld_creatorname, ld_lastpost, ld_subject, ld_replies, ld_views)
values(3, '2009-05-01',1,1,'2009-04-01',1,'Admin','2009-04-03','subject',2,1);

insert into ld_user_history 
				(ld_id, ld_lastmodified, ld_deleted, ld_userid, ld_date, ld_username, ld_event, ld_comment, ld_notified)
values     (1,'2008-10-22',0,1,'2006-12-20','author','data test 01','reason test 01',0);

insert into ld_user_history 
			    (ld_id, ld_lastmodified, ld_deleted, ld_userid, ld_date, ld_username, ld_event, ld_comment, ld_notified)
values     (2,'2008-10-22',0,1,'2006-12-25','author','data test 02','reason test 02',0);

insert into ld_user_history 
			    (ld_id, ld_lastmodified, ld_deleted, ld_userid, ld_date, ld_username, ld_event, ld_comment, ld_notified)
values     (3,'2008-10-22',0,3,'2006-12-27','sebastian','data test 03','reason test 03',1);
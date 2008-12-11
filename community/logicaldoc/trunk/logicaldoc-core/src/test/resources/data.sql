insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type)
values     (2,'2008-10-22',0,'boss','d033e22ae348aeb566fc214aec3585c4da997','Meschieri','Marco','','','','','it','m.meschieri@logicalobjects.it','',0);
insert into ld_group
values     (-2,'2008-10-22',0,'_user_2','',1);
insert into ld_usergroup
values (-2,2);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type)
values     (3,'2008-10-22',0,'sebastian','d033e22ae348aeb566fc214aec3585c4da997','Sebastian','Stein','','','','','de','seb_stein@gmx.de','',0);
insert into ld_group
values     (-3,'2008-10-22',0,'_user_3','',1);
insert into ld_usergroup
values (-3,3);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type)
values     (4,'2008-10-22',0,'author','d033e22ae348aeb566fc214aec3585c4da997','Author','Author','','','','','de','author@acme.com','',0);
insert into ld_group
values     (-4,'2008-10-22',0,'_user_4','',1);
insert into ld_usergroup
values (-4,4);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type)
values     (5,'2008-10-22',0,'test','d033e22ae348aeb566fc214aec3585c4da997','test','Test','','','','','de','test@acme.com','',0);
insert into ld_group
values     (-5,'2008-10-22',0,'_user_5','',1);
insert into ld_usergroup
values (-5,5);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (99,'2008-10-22',0,'db.admin',1,1,'administration.gif','/',5,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (100,'2008-10-22',0,'db.admin',1,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (101,'2008-10-22',0,'text',100,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (102,'2008-10-22',0,'db.admin',101,1,'administration.gif','/',5,null,0);
insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (103,'2008-10-22',0,'db.admin',101,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (1000,'2008-10-22',1,'db.admin.1000',1,1,'administration.gif','/',5,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (1100,'2008-10-22',1,'db.admin.1100',1000,1,'administration.gif','/',5,null,0);

insert into ld_menugroup
			   (ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (100,1,1,0,0,0,0,0,0,0);

insert into ld_menugroup
			   (ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (100,3,1,0,0,0,0,0,0,0);

insert into ld_menugroup
			   (ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (103,1,1,0,0,0,0,0,0,0);

insert into ld_menugroup
			   (ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (103,2,1,0,0,0,0,0,0,0);

insert into ld_menugroup
			   (ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport)
values     (99,1,0,0,0,0,0,0,0,0);

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
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid)
values     (1,'2008-10-22',0,0,'a','testDocname','testDocVer','2006-12-19','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,1,103);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid)
values     (2,'2008-10-22',0,0,'b','testDocname2','testDocVer','2006-12-19','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,0,103);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid)
values     (3,'2008-10-22',1,0,'c','DELETED','testDocVer','2006-12-19','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,1,103);

insert into ld_document
           (ld_id,ld_lastmodified,ld_deleted,ld_immutable,ld_customid,ld_title,ld_version,ld_date,ld_creation,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid)
values     (4,'2008-10-22',1,0,'d','DELETED','testDocVer','2006-12-19','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,1,1100);


insert into ld_ticket
           (ld_id,ld_lastmodified,ld_deleted,ld_ticketid,ld_docid,ld_userid)
values     (1,'2008-10-22',0,'1',1,1);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_deleted,ld_ticketid,ld_docid,ld_userid)
values     (2,'2008-10-22',0,'2',2,3);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_deleted,ld_ticketid,ld_docid,ld_userid)
values     (3,'2008-10-22',0,'3',1,3);

insert into ld_userdoc
           (ld_id,ld_lastmodified,ld_deleted,ld_docid,ld_userid,ld_date)
values     (1,'2008-10-22',0,1,1,'2006-12-17');

insert into ld_userdoc
           (ld_id,ld_lastmodified,ld_deleted,ld_docid,ld_userid,ld_date)
values     (2,'2008-10-22',0,2,1,'2006-12-22');

insert into ld_version
values     (1,'testVersion','testUser',1,'2006-12-19','testComment');

insert into ld_version
values     (1,'testVersion2','testUser',1,'2006-12-20','testComment');

insert into ld_keyword
values     (1,'abc');

insert into ld_keyword
values     (1,'def');

insert into ld_keyword
values     (1,'ghi');

insert into ld_keyword
values     (2,'ask');

insert into ld_history 
				(ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_userid, ld_date, ld_username, ld_event, ld_comment)
values     (1,'2008-10-22',0,1,1,'2006-12-20','author','data test 01','reason test 01');

insert into ld_history 
			    (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_userid, ld_date, ld_username, ld_event, ld_comment)
values     (2,'2008-10-22',0,2,1,'2006-12-25','author','data test 02','reason test 02');

insert into ld_history 
			   (ld_id, ld_lastmodified, ld_deleted, ld_docid, ld_userid, ld_date, ld_username, ld_event, ld_comment)
values     (3,'2008-10-22',0,1,3,'2006-12-27','sebastian','data test 03','reason test 03');

insert into ld_article
values     (1,'2008-10-22',0,1,'subject','message','2008-10-09','admin');

insert into ld_article
values     (2,'2008-10-22',0,1,'subject2','message2','2008-10-10','admin');

insert into ld_article
values     (3,'2008-10-22',0,1,'subject3','message3','2008-10-11','sebastian');


insert into ld_systemmessage
values     (1,'2008-10-22',0,'admin','sebastian','message text1','subject1','1111999999999999999',5,3,1,0);


insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (1,'2008-10-22',0,1,2,'test');
insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (2,'2008-10-22',0,2,1,'xyz');
insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (3,'2008-10-22',0,1,2,'xxx');
insert into ld_link(ld_id, ld_lastmodified,ld_deleted, ld_docid1, ld_docid2,ld_type)
values   (4,'2008-10-22',0,2,1,'');

insert into ld_template (ld_id, ld_lastmodified,ld_deleted, ld_name, ld_description)
values (1, '2008-11-07',0,'test1','test1_desc');
insert into ld_attributes (ld_templateid, ld_attribute)
values(1, 'attr1');

insert into ld_template (ld_id, ld_lastmodified,ld_deleted, ld_name, ld_description)
values (2, '2008-11-07',0,'test2','test2_desc');


insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(1, '2008-11-19',0,'a','a1','str1','str2',0,1,1.5,1.6,'2008-11-20','2008-11-20');
insert into ld_generic_ext(ld_genid, ld_value, ld_name)
values(1,'val1','att1');
insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(2, '2008-11-19',0,'a','a2','str1','str2',10,11,1.5,1.6,'2008-11-20','2008-11-20');
insert into ld_generic(ld_id, ld_lastmodified, ld_deleted, ld_type, ld_subtype, ld_string1, ld_string2, ld_integer1, ld_integer2, ld_double1, ld_double2, ld_date1, ld_date2)
values(3, '2008-11-19',1,'a.3','a2.3','str1','str2',10,11,1.5,1.6,'2008-11-20','2008-11-20');
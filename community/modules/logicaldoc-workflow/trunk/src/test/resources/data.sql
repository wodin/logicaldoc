insert into ld_user
		   (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source)
values     (2,'2008-11-20',0,1,'boss','d033e22ae348aeb566fc214aec3585c4da997','Meschieri','Marco','','','','','it','m.meschieri@logicalobjects.it','',0,null,0,0);

insert into ld_user
		   (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source)
values     (3,'2008-11-20',0,1,'sebastian','d033e22ae348aeb566fc214aec3585c4da997','Sebastian','Stein','','','','','de','seb_stein@gmx.de','',0,null,0,0);

insert into ld_user
		   (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source)
values     (4,'2008-11-20',0,1,'author','d033e22ae348aeb566fc214aec3585c4da997','Author','Author','','','','','de','author@acme.com','',0,null,0,0);

insert into ld_user
		   (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires,ld_source)
values     (5,'2008-11-20',0,1,'test','d033e22ae348aeb566fc214aec3585c4da997','test','Test','','','','','de','test@acme.com','',0,null,0,0);

insert into ld_menu
		   (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (99,'2008-11-20',0,'menu.admin',1,1,'administration.gif','/',5,null,0);

insert into ld_menu
		   (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (100,'2008-11-20',0,'menu.admin',1,1,'administration.gif','/',3,null,0);

insert into ld_menu
		   (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (101,'2008-11-20',0,'text',100,1,'administration.gif','/',3,null,0);

insert into ld_menu
		   (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (102,'2008-11-20',0,'menu.admin',101,1,'administration.gif','/',5,null,0);

insert into ld_menu
	       (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (103,'2008-11-20',1,'menu.admin',101,1,'administration.gif','/',3,null,0);

insert into ld_menugroup
values     (100,1,1,0,0,0,0,0,0,0,0,0,0);

insert into ld_menugroup
values     (100,3,1,0,0,0,0,0,0,0,0,0,0);

insert into ld_menugroup
values     (103,1,1,0,0,0,0,0,0,0,0,0,0);

insert into ld_menugroup
values     (103,2,1,0,0,0,0,0,0,0,0,0,0);

insert into ld_menugroup
values     (99,1,0,0,0,0,0,0,0,0,0,0,0);

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
values     (10,'2008-11-20',0,'testGroup','Group for tests',0);

insert into ld_template 
			(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description)
values		(7,'2008-11-20',0,'pippo','pippo');

insert into ld_document
		   (ld_id,ld_lastmodified,ld_deleted,ld_customid,ld_title,ld_version,ld_creation,ld_date,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_templateid,ld_immutable,ld_signed,ld_creator,ld_creatorid,ld_exportstatus)
values     (1,'2008-11-20',0,null,'testDocname','testDocVer','2006-12-19','2006-12-19','myself',1,1,'PDF',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,1,103,7,0,0,'creator',1,0);

insert into ld_document
		   (ld_id,ld_lastmodified,ld_deleted,ld_customid,ld_title,ld_version,ld_creation,ld_date,ld_publisher,ld_publisherid,ld_status,ld_type,ld_lockuserid,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_indexed,ld_folderid,ld_templateid,ld_immutable,ld_signed,ld_creator,ld_creatorid,ld_exportstatus)
values     (2,'2008-11-20',0,null,'testDocname2','testDocVer','2006-12-19','2006-12-19','myself',1,1,'PDF',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,1,103,7,0,0,'creator',1,0);

insert into ld_workflowtemplate 
				(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description, ld_deployed, ld_startstate)
values		(1,'2009-11-25',0,'workflow1','this is the workflow1',0,'pippo');

insert into ld_workflowtemplate 
				(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description, ld_deployed, ld_startstate)
values		(2,'2009-11-25',0,'workflow2','this is the workflow2',0,'pluto');

insert into ld_workflowtemplate 
				(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description, ld_deployed, ld_startstate)
values		(3,'2009-11-25',0,'workflow3','this is the workflow3',1,'paperino');

insert into ld_workflowtemplate 
				(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description, ld_deployed, ld_startstate)
values		(4,'2009-11-25',1,'workflow4','this is the workflow4',1,'minnie');

insert into ld_workflowhistory 
				(ld_id, ld_lastmodified, ld_deleted, ld_date, ld_event, ld_comment, ld_docid, ld_userid, ld_username, ld_templateid, ld_instanceid)
values      (1,'2008-10-22',0,'2006-12-27','start workflow','test',1,2,'matteo',1,'1');

insert into ld_workflowhistory 
				(ld_id, ld_lastmodified, ld_deleted, ld_date, ld_event, ld_comment, ld_docid, ld_userid, ld_username, ld_templateid, ld_instanceid)
values      (2,'2008-10-22',0,'2006-12-27','stop workflow','test',1,2,'matteo',1,'1');

insert into ld_workflowhistory 
				(ld_id, ld_lastmodified, ld_deleted, ld_date, ld_event, ld_comment, ld_docid, ld_userid, ld_username, ld_templateid, ld_instanceid)
values      (3,'2008-10-22',1,'2006-12-27','DELETED','test',1,2,'matteo',1,'2');
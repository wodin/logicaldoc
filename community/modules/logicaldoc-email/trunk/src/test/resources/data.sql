insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (99,'2008-10-22',0,'menu.admin',1,1,'administration.gif','/',5,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (100,'2008-10-22',0,'menu.admin',1,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (101,'2008-10-22',0,'text',100,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (102,'2008-10-22',0,'menu.admin',101,1,'administration.gif','/',5,null,0);
insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (103,'2008-10-22',0,'menu.admin',101,1,'administration.gif','/',3,null,0);

insert into ld_template 
			(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description)
values		(5,'2008-11-19',0,'pippo','pippo');

insert into ld_document
			(ld_id, ld_lastmodified, ld_deleted, ld_title, ld_version, ld_date, ld_publisher,ld_publisherid, ld_status, ld_type, ld_lockuserid, ld_source, ld_sourceauthor, ld_sourcedate, ld_sourcetype, ld_coverage, ld_language, ld_filename, ld_filesize, ld_indexed, ld_folderid, ld_templateid, ld_creation,ld_immutable,ld_signed,ld_creator,ld_creatorid,ld_exportstatus)
values     (1,'2008-10-22',0,'testDocname','testDocVer','2006-12-19','myself',1,1,'PDF',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,1,103,5,'2008-12-19',0,0,'',1,0);

insert into ld_document
			(ld_id, ld_lastmodified, ld_deleted, ld_title, ld_version, ld_date, ld_publisher,ld_publisherid, ld_status, ld_type, ld_lockuserid, ld_source, ld_sourceauthor, ld_sourcedate, ld_sourcetype, ld_coverage, ld_language, ld_filename, ld_filesize, ld_indexed, ld_folderid, ld_templateid, ld_creation,ld_immutable,ld_signed,ld_creator,ld_creatorid,ld_exportstatus)
values     (2,'2008-10-22',0,'testDocname2','testDocVer','2006-12-19','myself',1,1,'PDF',3,'source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,1,103,5,'2008-12-19',0,0,'',1,0);

insert into ld_emailaccount 
			(ld_id, ld_lastmodified, ld_deleted, ld_mailaddress, ld_provider, ld_host, ld_port, ld_username, ld_password, ld_allowedtypes, ld_deletefrommailbox, ld_language, ld_enabled, ld_targetfolder, ld_sslmodel, ld_extracttags)
values     (1,'2008-10-23',0, 'author@logicaldoc.sf.net','Aruba','pcalle','22','author@logicaldoc.sf.net','authorPSWD','pdf,doc',0,'it',1,1,0,0);

insert into ld_emailaccount
			(ld_id, ld_lastmodified, ld_deleted, ld_mailaddress, ld_provider, ld_host, ld_port, ld_username, ld_password, ld_allowedtypes, ld_deletefrommailbox, ld_language, ld_enabled, ld_targetfolder, ld_sslmodel, ld_extracttags)
values     (2,'2008-10-23',0, 'admin@logicaldoc.sf.net','Aruba','pcalle','22','admin@logicaldoc.sf.net','adminPSWD','doc,txt',1,'en',0,1,0,0);




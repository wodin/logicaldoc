insert into ld_menu
           (ld_id,ld_lastmodified,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (99,'2008-10-22','db.admin',1,1,'administration.gif','/',5,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (100,'2008-10-22','db.admin',1,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (101,'2008-10-22','text',100,1,'administration.gif','/',3,null,0);

insert into ld_menu
           (ld_id,ld_lastmodified,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (102,'2008-10-22','db.admin',101,1,'administration.gif','/',5,null,0);
insert into ld_menu
           (ld_id,ld_lastmodified,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (103,'2008-10-22','db.admin',101,1,'administration.gif','/',3,null,0);


insert into ld_document
           (ld_id,ld_lastmodified,ld_title,ld_version,ld_date,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_folderid)
values     (1,'2008-10-22','testDocname','testDocVer','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,103);

insert into ld_document
           (ld_id,ld_lastmodified,ld_title,ld_version,ld_date,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_folderid)
values     (2,'2008-10-22','testDocname2','testDocVer','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,103);



insert into ld_emailaccount
values     (1,'2008-10-23','author@logicaldoc.sf.net','Aruba','pcalle','22','author@logicaldoc.sf.net','authorPSWD','pdf,doc',0,'it',1,1);

insert into ld_emailaccount
values     (2,'2008-10-23','admin@logicaldoc.sf.net','Aruba','pcalle','22','admin@logicaldoc.sf.net','adminPSWD','doc,txt',1,'en',0,1);

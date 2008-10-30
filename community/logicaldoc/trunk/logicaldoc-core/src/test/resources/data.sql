insert into ld_user
           (ld_id,ld_lastmodified,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_hidden)
values     (2,'2008-10-22','boss','d033e22ae348aeb566fc214aec3585c4da997','Meschieri','Marco','','','','','it','m.meschieri@logicalobjects.it','',0);

insert into ld_user
           (ld_id,ld_lastmodified,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_hidden)
values     (3,'2008-10-22','sebastian','d033e22ae348aeb566fc214aec3585c4da997','Sebastian','Stein','','','','','de','seb_stein@gmx.de','',0);

insert into ld_user
           (ld_id,ld_lastmodified,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_hidden)
values     (4,'2008-10-22','author','d033e22ae348aeb566fc214aec3585c4da997','Author','Author','','','','','de','author@acme.com','',0);

insert into ld_user
           (ld_id,ld_lastmodified,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_hidden)
values     (5,'2008-10-22','test','d033e22ae348aeb566fc214aec3585c4da997','test','Test','','','','','de','test@acme.com','',0);

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

insert into ld_menugroup
values     (100,1,1);

insert into ld_menugroup
values     (100,3,1);

insert into ld_menugroup
values     (103,1,1);

insert into ld_menugroup
values     (103,2,1);

insert into ld_menugroup
values     (99,1,0);

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
values     (10,'2008-10-22','testGroup','Group for tests',0);

insert into ld_document
           (ld_id,ld_lastmodified,ld_title,ld_version,ld_date,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_folderid)
values     (1,'2008-10-22','testDocname','testDocVer','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,103);

insert into ld_document
           (ld_id,ld_lastmodified,ld_title,ld_version,ld_date,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_folderid)
values     (2,'2008-10-22','testDocname2','testDocVer','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,103);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_ticketid,ld_docid,ld_userid)
values     (1,'2008-10-22','1',1,1);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_ticketid,ld_docid,ld_userid)
values     (2,'2008-10-22','2',2,3);

insert into ld_ticket
           (ld_id,ld_lastmodified,ld_ticketid,ld_docid,ld_userid)
values     (3,'2008-10-22','3',1,3);

insert into ld_userdoc
           (ld_id,ld_lastmodified,ld_docid,ld_userid,ld_date)
values     (1,'2008-10-22',1,1,'2006-12-17');

insert into ld_userdoc
           (ld_id,ld_lastmodified,ld_docid,ld_userid,ld_date)
values     (2,'2008-10-22',2,1,'2006-12-22');

insert into ld_version
values     (1,'testVersion','testUser','2006-12-19','testComment');

insert into ld_version
values     (1,'testVersion2','testUser','2006-12-20','testComment');

insert into ld_keyword
values     (1,'abc');

insert into ld_keyword
values     (1,'def');

insert into ld_keyword
values     (1,'ghi');

insert into ld_history
values     (1,'2008-10-22',1,1,'2006-12-20','author','data test 01');

insert into ld_history
values     (2,'2008-10-22',2,1,'2006-12-25','author','data test 02');

insert into ld_history
values     (3,'2008-10-22',1,3,'2006-12-27','sebastian','data test 03');

insert into ld_term
values     (1,'2008-10-22',1,'a',2.5,50,'test');

insert into ld_term
values     (2,'2008-10-22',2,'a',0.7,12,'test2');

insert into ld_term
values     (3,'2008-10-22',2,'a',0.3,12,'test3');

insert into ld_term
values     (4,'2008-10-22',2,'a',0.14,20,'test4');

insert into ld_term
values     (5,'2008-10-22',2,'z',0.14,20,'test5');

insert into ld_article
values     (1,'2008-10-22',1,'subject','message','2008-10-09','admin');

insert into ld_article
values     (2,'2008-10-22',1,'subject2','message2','2008-10-10','admin');

insert into ld_article
values     (3,'2008-10-22',1,'subject3','message3','2008-10-11','sebastian');


insert into ld_systemmessage
values     (1,'2008-10-22','admin','sebastian','message text1','subject1','1111999999999999999',5,3,1,0);


insert into ld_link(ld_id, ld_lastmodified, ld_docid1, ld_docid2,ld_type)
values   (1,'2008-10-22',1,2,'test');
insert into ld_link(ld_id, ld_lastmodified, ld_docid1, ld_docid2,ld_type)
values   (2,'2008-10-22',2,1,'xyz');
insert into ld_link(ld_id, ld_lastmodified, ld_docid1, ld_docid2,ld_type)
values   (3,'2008-10-22',1,2,'xxx');
insert into ld_link(ld_id, ld_lastmodified, ld_docid1, ld_docid2,ld_type)
values   (4,'2008-10-22',2,1,'');
insert into ld_user
           (ld_id,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone)
values     (2,'boss','d033e22ae348aeb566fc214aec3585c4da997','Meschieri','Marco','','','','','it','m.meschieri@logicalobjects.it','');

insert into ld_user
           (ld_id,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone)
values     (3,'sebastian','d033e22ae348aeb566fc214aec3585c4da997','Sebastian','Stein','','','','','de','seb_stein@gmx.de','');

insert into ld_user
           (ld_id,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone)
values     (4,'author','d033e22ae348aeb566fc214aec3585c4da997','Author','Author','','','','','de','author@acme.com','');

insert into ld_user
           (ld_id,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone)
values     (5,'test','d033e22ae348aeb566fc214aec3585c4da997','test','Test','','','','','de','test@acme.com','');

insert into ld_menu
values     (99,'db.admin',1,1,'administration.gif','/',5,null,0);

insert into ld_menu
values     (100,'db.admin',1,1,'administration.gif','/',3,null,0);

insert into ld_menu
values     (101,'text',100,1,'administration.gif','/',3,null,0);

insert into ld_menu
values     (102,'db.admin',101,1,'administration.gif','/',5,null,0);

insert into ld_menu
values     (103,'db.admin',101,1,'administration.gif','/',3,null,0);

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
values     (10,'testGroup','Group for tests');

insert into ld_document
           (ld_id,ld_title,ld_version,ld_date,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_folderid)
values     (1,'testDocname','testDocVer','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,103);

insert into ld_document
           (ld_id,ld_title,ld_version,ld_date,ld_publisher,ld_status,ld_type,ld_checkoutuser,ld_source,ld_sourceauthor,ld_sourcedate,ld_sourcetype,ld_coverage,ld_language,ld_filename,ld_filesize,ld_folderid)
values     (2,'testDocname2','testDocVer','2006-12-19','myself',1,'PDF','sebastian','source','sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,103);

insert into ld_ticket
           (ld_id,ld_ticketid,ld_docid,ld_userid)
values     (1,'1',1,1);

insert into ld_ticket
           (ld_id,ld_ticketid,ld_docid,ld_userid)
values     (2,'2',2,3);

insert into ld_ticket
           (ld_id,ld_ticketid,ld_docid,ld_userid)
values     (3,'3',1,3);

insert into ld_userdoc
           (ld_id,ld_docid,ld_userid,ld_date)
values     (1,1,1,'2006-12-17');

insert into ld_userdoc
           (ld_id,ld_docid,ld_userid,ld_date)
values     (2,2,1,'2006-12-22');

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
values     (1,1,4,'2006-12-20','author','data test 01');

insert into ld_history
values     (2,2,4,'2006-12-25','author','data test 02');

insert into ld_history
values     (3,1,3,'2006-12-27','sebastian','data test 03');

insert into ld_term
values     (1,1,'a',2.5,50,'test');

insert into ld_term
values     (2,2,'a',0.7,12,'test2');

insert into ld_term
values     (3,2,'a',0.3,12,'test3');

insert into ld_term
values     (4,2,'a',0.14,20,'test4');

insert into ld_term
values     (5,2,'z',0.14,20,'test5');

insert into ld_article
values     (1,1,'subject','message','2008-10-09','admin');

insert into ld_article
values     (2,1,'subject2','message2','2008-10-10','admin');

insert into ld_article
values     (3,1,'subject3','message3','2008-10-11','sebastian');

insert into ld_emailaccount
values     (1,1,'author@logicaldoc.sf.net','Aruba','pcalle','port','author@logicaldoc.sf.net','authorPSWD','pdf,doc',0,'it',1,1);

insert into ld_emailaccount
values     (2,4,'admin@logicaldoc.sf.net','Aruba','pcalle','port','admin@logicaldoc.sf.net','adminPSWD','doc,txt',1,'en',0,1);

insert into ld_systemmessage
values     (1,'admin','sebastian','message text1','subject1','1111999999999999999',5,3,1,0);
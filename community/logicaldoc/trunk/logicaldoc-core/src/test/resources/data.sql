INSERT INTO co_users (co_username,co_password,co_name,co_firstname,co_street,co_postalcode,co_city,co_country,co_language,co_email,co_telephone)
VALUES ('boss','d033e22ae348aeb566fc214aec3585c4da997','Meschieri','Marco','','','','','it','m.meschieri@logicalobjects.it','');

INSERT INTO co_users (co_username,co_password,co_name,co_firstname,co_street,co_postalcode,co_city,co_country,co_language,co_email,co_telephone)
VALUES ('sebastian','d033e22ae348aeb566fc214aec3585c4da997','Sebastian','Stein','','','','','de','seb_stein@gmx.de','');

INSERT INTO co_users (co_username,co_password,co_name,co_firstname,co_street,co_postalcode,co_city,co_country,co_language,co_email,co_telephone)
VALUES ('author','d033e22ae348aeb566fc214aec3585c4da997','Author','Author','','','','','de','author@acme.com','');

INSERT INTO co_users (co_username,co_password,co_name,co_firstname,co_street,co_postalcode,co_city,co_country,co_language,co_email,co_telephone)
VALUES ('test','d033e22ae348aeb566fc214aec3585c4da997','test','Test','','','','','de','test@acme.com','');

INSERT INTO ld_menu VALUES(99,'db.admin',1,1,'administration.gif','/',5,NULL,0);
INSERT INTO ld_menu VALUES(100,'db.admin',1,1,'administration.gif','/',3,NULL,0);
INSERT INTO ld_menu VALUES(101,'text',100,1,'administration.gif','/',3,NULL,0);
INSERT INTO ld_menu VALUES(102,'db.admin',101,1,'administration.gif','/',5,NULL,0);
INSERT INTO ld_menu VALUES(103,'db.admin',101,1,'administration.gif','/',3,NULL,0);

INSERT INTO ld_menugroup VALUES(100,'admin',1);
INSERT INTO ld_menugroup VALUES(100,'guest',1);
INSERT INTO ld_menugroup VALUES(103,'admin',1);
INSERT INTO ld_menugroup VALUES(103,'author',1);
INSERT INTO ld_menugroup VALUES(99,'admin',0);

INSERT INTO co_usergroup (co_username,co_groupname) 
VALUES ('sebastian','admin');
INSERT INTO co_usergroup (co_username,co_groupname) 
VALUES ('sebastian','author');
INSERT INTO co_usergroup (co_username,co_groupname) 
VALUES ('author','author');
INSERT INTO co_usergroup (co_username,co_groupname) 
VALUES ('test','guest');


INSERT INTO co_groups VALUES('testGroup','Group for tests');

INSERT INTO ld_ticket VALUES(1,'1',1,'admin');
INSERT INTO ld_ticket VALUES(2,'2',2,'sebastian');
INSERT INTO ld_ticket VALUES(3,'3',1,'sebastian');

INSERT INTO ld_document(
  LD_ID,
  LD_TITLE,
  LD_VERSION,
  LD_DATE,
  LD_PUBLISHER,
  LD_STATUS,
  LD_TYPE,
  LD_CHECKOUTUSER,
  LD_SOURCE,
  LD_SOURCEAUTHOR,
  LD_SOURCEDATE,
  LD_SOURCETYPE,
  LD_COVERAGE,
  LD_LANGUAGE,
  LD_FILENAME,
  LD_FILESIZE,
  LD_FOLDERID ) 
VALUES(1,'testDocname','testDocVer','2006-12-19','myself', 1, 'PDF','sebastian', 'source', 'sourceauthor','2008-12-19','sourcetype','coverage','en','pippo',1356,103);
INSERT INTO ld_document(
  LD_ID,
  LD_TITLE,
  LD_VERSION,
  LD_DATE,
  LD_PUBLISHER,
  LD_STATUS,
  LD_TYPE,
  LD_CHECKOUTUSER,
  LD_SOURCE,
  LD_SOURCEAUTHOR,
  LD_SOURCEDATE,
  LD_SOURCETYPE,
  LD_COVERAGE,
  LD_LANGUAGE,
  LD_FILENAME,
  LD_FILESIZE,
  LD_FOLDERID ) 
VALUES(2,'testDocname2','testDocVer','2006-12-19','myself', 1, 'PDF','sebastian', 'source', 'sourceauthor','2008-12-19','sourcetype','coverage','en','pluto',122345,103);


INSERT INTO ld_userdoc (ld_docid,ld_username,ld_date) 
VALUES (1,'admin','2006-12-17');
INSERT INTO ld_userdoc (ld_docid,ld_username,ld_date)
VALUES (2,'admin','2006-12-22');

INSERT INTO LD_VERSION 
VALUES(1,'testVersion','testUser','2006-12-19','testComment');
INSERT INTO LD_VERSION 
VALUES(1,'testVersion2','testUser','2006-12-20','testComment');

INSERT INTO ld_keyword VALUES(1,'abc');
INSERT INTO ld_keyword VALUES(1,'def');
INSERT INTO ld_keyword VALUES(1,'ghi');

INSERT INTO ld_history VALUES(1,1,'2006-12-20','author','data test 01');
INSERT INTO ld_history VALUES(2,2,'2006-12-25','author','data test 02');
INSERT INTO ld_history VALUES(3,1,'2006-12-27','sebastian','data test 03');

INSERT INTO LD_TERM values (1,'a',2.5,50,'test');
INSERT INTO LD_TERM values (1,'b',0.7,12,'test2');
INSERT INTO LD_TERM values (2,'a',0.3,12,'test3');
INSERT INTO LD_TERM values (2,'x',0.14,20,'test4');
INSERT INTO LD_TERM values (2,'z',0.14,20,'test5');

INSERT INTO ld_article values(1,1,'subject','message','2008-10-09','admin');
INSERT INTO ld_article values(2,1,'subject2','message2','2008-10-10','admin');
INSERT INTO ld_article values(3,1,'subject3','message3','2008-10-11','sebastian');

INSERT INTO co_account values(1,'admin','author@logicaldoc.sf.net','Aruba','pcalle','port','author@logicaldoc.sf.net','authorPSWD','pdf,doc',0,'it',1,1);
INSERT INTO co_account values(2,'author','admin@logicaldoc.sf.net','Aruba','pcalle','port','admin@logicaldoc.sf.net','adminPSWD','doc,txt',1,'en',0,1);

INSERT INTO co_email values(17,1,'id1','messageText','Morven Macauley','Re: maintenanc','12/14/2006 04:49 AM',1,'sprou@l2r9f8.varberg.net','admin','Junk');
INSERT INTO co_email values(18,1,'id2','messageText','Nels Keough','Re: overgil','12/18/2006 10:23 PM',1,'woodsoy@obacom.com','author','Junk');
INSERT INTO co_email values(19,2,'id1','messageText','Nels Keough','Re: overgil','12/18/2006 10:23 PM',1,'woodsoy@obacom.com','author','Junk');

    
INSERT INTO co_attachment values(17,'holiday06_1.gif','gif.png','image/gif',54);
INSERT INTO co_attachment values(18,'hibernate.log','log.png','application/octet-stream',1);

INSERT INTO co_systemmessage values(1,'admin','sebastian','message text1','subject1','1111999999999999999',5,3,1,0);
INSERT INTO co_systemmessage values(2,'admin','sebastian','message text2','subject2','11119999',5,3,1,1);
INSERT INTO co_systemmessage values(3,'sebastian','admin','message text3','subject3','1111999999999999999',5,3,1,1);
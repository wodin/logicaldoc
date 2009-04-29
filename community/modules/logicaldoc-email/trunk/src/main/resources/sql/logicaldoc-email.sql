create table ld_emailaccount (ld_id bigint not null, ld_lastmodified timestamp not null,  ld_deleted int not null, ld_mailaddress varchar(255), ld_provider varchar(255), ld_host varchar(255), ld_port int, ld_username varchar(255), ld_password varchar(255), ld_allowedtypes varchar(255), ld_deletefrommailbox int, ld_language varchar(255), ld_enabled int, ld_targetfolder bigint, ld_sslmodel int not null, ld_extracttags int not null, primary key (ld_id));
alter table ld_emailaccount add constraint FK_EMAILACCOUNT_MENU  foreign key (ld_targetfolder) references ld_menu;

insert into ld_menu (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (24,'2008-10-22',0,'logicaldoc-email.accounts',2,10,'mailbox.png','/2',1,'email/accounts',0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_manageimmutability, ld_delete, ld_rename, ld_bulkimport, ld_bulkexport, ld_sign, ld_archive)
values     (24,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_user
           (ld_id,ld_lastmodified,ld_deleted,ld_enabled,ld_username,ld_password,ld_name,ld_firstname,ld_street,ld_postalcode,ld_city,ld_country,ld_language,ld_email,ld_telephone,ld_type,ld_passwordchanged,ld_passwordexpires)
values     (-5,'2008-11-18',0,1,'_email','','email','email','','','','','en','email@acme.com','',1,null,0);
insert into ld_group
values     (-5,'2008-11-18',0,'_user_-5','',1);
insert into ld_usergroup
values (-5,-5);

insert into ld_template
			(ld_id, ld_lastmodified, ld_deleted, ld_name, ld_description)
values (1,'2008-11-19',0,'email','email');

insert into ld_attributes 
			(ld_templateid, ld_attribute)
values (1,'from');

insert into ld_attributes 
			(ld_templateid, ld_attribute)
values (1,'to');

insert into ld_attributes 
			(ld_templateid, ld_attribute)
values (1,'subject');
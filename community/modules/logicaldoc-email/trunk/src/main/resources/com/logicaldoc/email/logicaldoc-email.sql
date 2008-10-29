create table ld_emailaccount (ld_id bigint not null, ld_lastmodified timestamp not null,  ld_mailaddress varchar(255), ld_provider varchar(255), ld_host varchar(255), ld_port int, ld_username varchar(255), ld_password varchar(255), ld_allowedtypes varchar(255), ld_deletefrommailbox int NOT NULL, ld_language varchar(255) NOT NULL, ld_enabled int NOT NULL, ld_extractkeywords int NOT NULL, ld_targetfolder bigint, primary key (ld_id));
alter table ld_emailaccount add constraint FK_EMAILACCOUNT_MENU  foreign key (ld_targetfolder) references ld_menu;

insert into ld_menu (ld_id,ld_lastmodified,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values     (24,'2008-10-22','db.emailaccounts',20,4,'mailbox.png','/2/20',1,'email/accounts',0);

insert into ld_menugroup
values     (24,1,0);

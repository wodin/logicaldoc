--This script migrates a 4.5.x database to a 4.6 (Community Edition)
drop constraint FKF9B7567E76C86307;
drop table ld_attributes;

alter table ld_document_ext add column ld_mandatory int default 0 not null;
alter table ld_document_ext add column ld_type int default 0 not null;
alter table ld_document_ext add column ld_stringvalue varchar(4000);
alter table ld_document_ext add column ld_intvalue int;
alter table ld_document_ext add column ld_doublevalue float;
alter table ld_document_ext add column ld_datevalue timestamp;
update ld_document_ext set ld_stringvalue=ld_value;
alter table ld_document_ext drop column ld_value;

alter table ld_generic_ext add column ld_mandatory int default 0 not null;
alter table ld_generic_ext add column ld_type int default 0  not null;
alter table ld_generic_ext add column ld_stringvalue varchar(4000);
alter table ld_generic_ext add column ld_intvalue int;
alter table ld_generic_ext add column ld_doublevalue float;
alter table ld_generic_ext add column ld_datevalue timestamp;
update ld_generic_ext set ld_stringvalue=ld_value;
alter table ld_generic_ext drop column ld_value;

alter table ld_group_ext add column ld_mandatory int default 0  not null;
alter table ld_group_ext add column ld_type int default 0 not nul;
alter table ld_group_ext add column ld_stringvalue varchar(4000);
alter table ld_group_ext add column ld_intvalue int;
alter table ld_group_ext add column ld_doublevalue float;
alter table ld_group_ext add column ld_datevalue timestamp;
update ld_group_ext set ld_stringvalue=ld_value;
alter table ld_group_ext drop column ld_value;

alter table ld_menugroup add column ld_workflow int default 0 not null;
alter table ld_menugroup drop constraint ld_menugroup_pkey;
alter table ld_menugroup add primary key ( ld_menuid,ld_groupid,ld_write,ld_addchild,ld_managesecurity,ld_manageimmutability,ld_delete,ld_rename,ld_bulkimport,ld_bulkexport,ld_sign,ld_archive,ld_workflow );

alter table ld_template_ext add column ld_mandatory int default 0  not null;
alter table ld_template_ext add column ld_type int default 0 not nul;
alter table ld_template_ext add column ld_stringvalue varchar(4000);
alter table ld_template_ext add column ld_intvalue int;
alter table ld_template_ext add column ld_doublevalue float;
alter table ld_template_ext add column ld_datevalue timestamp;
update ld_template_ext set ld_stringvalue=ld_value;
alter table ld_template_ext drop column ld_value;

alter table ld_version_ext add column ld_mandatory int default 0  not null;
alter table ld_version_ext add column ld_type int default 0 not nul;
alter table ld_version_ext add column ld_stringvalue varchar(4000);
alter table ld_version_ext add column ld_intvalue int;
alter table ld_version_ext add column ld_doublevalue float;
alter table ld_version_ext add column ld_datevalue timestamp;
update ld_version_ext set ld_stringvalue=ld_value;
alter table ld_version_ext drop column ld_value;

alter table ld_emailaccount add column ld_mailfolder varchar(255);

create table ld_emailaccount_rule (ld_accountid bigint not null, ld_field int not null, ld_policy int not null, ld_expression varchar(4000), ld_targetfolder bigint, ld_position int not null, primary key (ld_accountid, ld_position));
alter table ld_emailaccount_rule add constraint FK_EMAILACCOUNT_RULE_MENU foreign key (ld_targetfolder) references ld_menu;
alter table ld_emailaccount_rule add constraint FK_EMAILACCOUNT_RULE_ACCOUNT foreign key (ld_accountid) references ld_emailaccount;

alter table ld_version_ext add column ld_mandatory int default 0  not null;
alter table ld_version_ext add column ld_type int default 0 not nul;
alter table ld_version_ext add column ld_stringvalue varchar(4000);
alter table ld_version_ext add column ld_intvalue int;
alter table ld_version_ext add column ld_doublevalue float;
alter table ld_version_ext add column ld_datevalue timestamp;
update ld_version_ext set ld_stringvalue=ld_value;
alter table ld_version_ext drop column ld_value;


insert into ld_menu
(ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values  (-1320,'2009-08-16',0,'localization',2,35,'world.png','/2',1,'localization/localization',0);
INSERT INTO ld_menugroup VALUES(-1320,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_menu
(ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values (-1330,'2009-08-16',0,'restart',-2,100,'shutdown.png','/-2',1,'admin/restart',0);
INSERT INTO ld_menugroup VALUES(-1330,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_menu (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size) values (-1340,'2009-08-16',0,'sessions',-2,90,'group.png','/-2',1,'admin/sessions',0);
INSERT INTO ld_menugroup VALUES(-1340,1,1,1,1,1,1,1,1,1,1,1,1);

insert into ld_menu       (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size) values     (-21,'2009-02-23',0,'activedir',9,11,'ad.png','/9',3,'external-authentication/ad',0);
INSERT INTO ld_menugroup VALUES(-21,1,1,1,1,1,1,1,1,1,1,1,1);


insert into ld_menu
(ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values(-2050,'2009-07-28',0,'sost',-2020,2,'sost.png','/-2020',1,'impex/sost',0);
insert into ld_menu
(ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_path,ld_type,ld_ref,ld_size)
values (-2060,'2009-08-06',0,'logicaldoc-impex.incrementalarch',-2020,2,'incrementalArch.png','/-2020',1,'impex/incrementalArchs',0);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_delete, ld_rename,ld_bulkimport,ld_bulkexport,ld_manageimmutability, ld_sign, ld_archive, ld_workflow)
values     (-2050,1,1,1,1,1,1,1,1,1,1,1,1);
insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_addchild, ld_managesecurity, ld_delete, ld_rename,ld_bulkimport,ld_bulkexport,ld_manageimmutability, ld_sign, ld_archive, ld_workflow)
values     (-2060,1,1,1,1,1,1,1,1,1,1,1,1);


--Templates and bindings for Conservazione Sostitutiva
INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-98,'2009-09-04',0,'Fattura vendita','Fattura di vendita');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-97,'2009-09-04',0,'Fattura acquisto','Fattura di acquisto');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-95,'2009-09-04',0,'Documento Trasporto','Documento di Trasporto');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-94,'2009-09-04',0,'Contratto','Contratto');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-93,'2009-09-04',0,'Libro Lavoro','Libro del Lavoro');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-92,'2009-09-04',0,'Libro Giornale','Libro Giornale');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-91,'2009-09-04',0,'Libro Inventario','Libro Inventario');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-90,'2009-09-04',0,'Mastro Contabile','Mastro Contabile');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-89,'2009-09-04',0,'Registro IVA Vendita','Registro IVA Vendita');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-88,'2009-09-04',0,'Registro IVA Acquisto','Registro IVA Acquisto');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-87,'2009-09-04',0,'Registro Beni Ammortizzabili','Registro Beni Ammortizzabili');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-86,'2009-09-04',0,'Libro Socio','Libro Socio');

INSERT INTO LD_TEMPLATE (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_NAME,LD_DESCRIPTION) VALUES (-85,'2009-09-04',0,'Libro Assemblea','Libro Assemblea');

-- Template Attributes  for Conservazione Sostitutiva
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-98,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-98,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-98,1,2,null,null,null,null,'importo');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-98,1,1,null,null,null,null,'numero');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-98,1,0,null,null,null,null,'codfisc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-98,1,3,null,null,null,null,'data');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-97,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-97,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-97,1,2,null,null,null,null,'importo');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-97,1,1,null,null,null,null,'numero');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-97,1,0,null,null,null,null,'codfisc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-97,1,3,null,null,null,null,'data');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,0,0,null,null,null,null,'vettore');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,0,3,null,null,null,null,'data trasporto');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,1,1,null,null,null,null,'numero');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,1,1,null,null,null,null,'colli');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,1,3,null,null,null,null,'data');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,0,0,null,null,null,null,'causale');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-95,1,0,null,null,null,null,'codfisc');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-94,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-94,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-94,1,1,null,null,null,null,'numero');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-94,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-94,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-93,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-93,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-93,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-93,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-92,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-92,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-92,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-92,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-91,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-91,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-91,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-91,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-90,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-90,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-90,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-90,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-89,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-89,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-89,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-89,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-88,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-88,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-88,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-88,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-87,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-87,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-87,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-87,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-86,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-86,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-86,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-86,1,1,null,null,null,null,'anno');

INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-85,1,0,null,null,null,null,'ragsoc');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-85,1,0,null,null,null,null,'piva');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-85,1,1,null,null,null,null,'mese');
INSERT INTO LD_TEMPLATE_EXT (LD_TEMPLATEID,LD_MANDATORY,LD_TYPE,LD_STRINGVALUE,LD_INTVALUE,LD_DOUBLEVALUE,LD_DATEVALUE,LD_NAME) VALUES (-85,1,1,null,null,null,null,'anno');

-- Template Generics  for Conservazione Sostitutiva
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-998,'2009-09-04',0,'sost','template.-98',null,null,1,1,-98.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-997,'2009-09-04',0,'sost','attribute_vat.-98','vat','piva',null,null,-98.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-996,'2009-09-04',0,'sost','attribute_num.-98','num','numero',null,null,-98.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-995,'2009-09-04',0,'sost','attribute_date.-98','date','data',null,null,-98.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-994,'2009-09-04',0,'sost','attribute_company.-98','company','ragsoc',null,null,-98.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-993,'2009-09-04',0,'sost','attribute_fiscalcode.-98','fiscalcode','piva',null,null,-98.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-992,'2009-09-04',0,'sost','attribute_amount.-98','amount','importo',null,null,-98.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-991,'2009-09-04',0,'sost','template.-97',null,null,1,2,-97.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-990,'2009-09-04',0,'sost','attribute_vat.-97','vat','ragsoc',null,null,-97.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-989,'2009-09-04',0,'sost','attribute_num.-97','num','numero',null,null,-97.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-988,'2009-09-04',0,'sost','attribute_date.-97','date','data',null,null,-97.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-987,'2009-09-04',0,'sost','attribute_company.-97','company','codfisc',null,null,-97.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-986,'2009-09-04',0,'sost','attribute_fiscalcode.-97','fiscalcode','codfisc',null,null,-97.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-985,'2009-09-04',0,'sost','attribute_amount.-97','amount','importo',null,null,-97.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-938,'2009-09-04',0,'sost','template.-95',null,null,1,3,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-937,'2009-09-04',0,'sost','attribute_vat.-95','vat','piva',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-936,'2009-09-04',0,'sost','attribute_fiscalcode.-95','fiscalcode','codfisc',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-935,'2009-09-04',0,'sost','attribute_num.-95','num','numero',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-934,'2009-09-04',0,'sost','attribute_date.-95','date','data',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-933,'2009-09-04',0,'sost','attribute_company.-95','company','ragsoc',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-932,'2009-09-04',0,'sost','attribute_pieces.-95','pieces','colli',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-931,'2009-09-04',0,'sost','attribute_vector.-95','vector','vettore',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-930,'2009-09-04',0,'sost','attribute_transportdate.-95','transportdate','data trasporto',null,null,-95.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-929,'2009-09-04',0,'sost','attribute_reason.-95','reason','causale',null,null,-95.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-928,'2009-09-04',0,'sost','template.-94',null,null,1,4,-94.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-927,'2009-09-04',0,'sost','attribute_vat.-94','vat','piva',null,null,-94.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-926,'2009-09-04',0,'sost','attribute_company.-94','company','ragsoc',null,null,-94.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-925,'2009-09-04',0,'sost','attribute_num.-94','num','numero',null,null,-94.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-924,'2009-09-04',0,'sost','attribute_year.-94','year','anno',null,null,-94.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-923,'2009-09-04',0,'sost','attribute_month.-94','month','mese',null,null,-94.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-922,'2009-09-04',0,'sost','template.-93',null,null,1,5,-93.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-921,'2009-09-04',0,'sost','attribute_vat.-93','vat','piva',null,null,-93.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-920,'2009-09-04',0,'sost','attribute_company.-93','company','ragsoc',null,null,-93.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-919,'2009-09-04',0,'sost','attribute_year.-93','year','anno',null,null,-93.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-918,'2009-09-04',0,'sost','attribute_month.-93','month','mese',null,null,-93.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-917,'2009-09-04',0,'sost','template.-92',null,null,1,6,-92.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-916,'2009-09-04',0,'sost','attribute_vat.-92','vat','piva',null,null,-92.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-915,'2009-09-04',0,'sost','attribute_company.-92','company','ragsoc',null,null,-92.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-914,'2009-09-04',0,'sost','attribute_year.-92','year','anno',null,null,-92.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-913,'2009-09-04',0,'sost','attribute_month.-92','month','mese',null,null,-92.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-910,'2009-09-04',0,'sost','template.-91',null,null,1,7,-91.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-909,'2009-09-04',0,'sost','attribute_vat.-91','vat','piva',null,null,-91.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-908,'2009-09-04',0,'sost','attribute_company.-91','company','ragsoc',null,null,-91.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-907,'2009-09-04',0,'sost','attribute_year.-91','year','anno',null,null,-91.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-906,'2009-09-04',0,'sost','attribute_month.-91','month','mese',null,null,-91.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-905,'2009-09-04',0,'sost','template.-90',null,null,1,8,-90.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-904,'2009-09-04',0,'sost','attribute_vat.-90','vat','piva',null,null,-90.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-903,'2009-09-04',0,'sost','attribute_company.-90','company','ragsoc',null,null,-90.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-902,'2009-09-04',0,'sost','attribute_year.-90','year','anno',null,null,-90.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-901,'2009-09-04',0,'sost','attribute_month.-90','month','mese',null,null,-90.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-900,'2009-09-04',0,'sost','template.-89',null,null,1,9,-89.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-899,'2009-09-04',0,'sost','attribute_vat.-89','vat','piva',null,null,-89.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-898,'2009-09-04',0,'sost','attribute_company.-89','company','ragsoc',null,null,-89.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-897,'2009-09-04',0,'sost','attribute_year.-89','year','anno',null,null,-89.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-896,'2009-09-04',0,'sost','attribute_month.-89','month','mese',null,null,-89.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-895,'2009-09-04',0,'sost','template.-88',null,null,1,10,-88.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-894,'2009-09-04',0,'sost','attribute_vat.-88','vat','piva',null,null,-88.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-893,'2009-09-04',0,'sost','attribute_company.-88','company','ragsoc',null,null,-88.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-892,'2009-09-04',0,'sost','attribute_year.-88','year','anno',null,null,-88.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-891,'2009-09-04',0,'sost','attribute_month.-88','month','mese',null,null,-88.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-890,'2009-09-04',0,'sost','template.-87',null,null,1,11,-87.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-889,'2009-09-04',0,'sost','attribute_vat.-87','vat','piva',null,null,-87.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-888,'2009-09-04',0,'sost','attribute_company.-87','company','ragsoc',null,null,-87.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-887,'2009-09-04',0,'sost','attribute_year.-87','year','anno',null,null,-87.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-886,'2009-09-04',0,'sost','attribute_month.-87','month','mese',null,null,-87.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-885,'2009-09-04',0,'sost','template.-86',null,null,1,12,-86.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-884,'2009-09-04',0,'sost','attribute_vat.-86','vat','piva',null,null,-86.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-883,'2009-09-04',0,'sost','attribute_company.-86','company','ragsoc',null,null,-86.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-882,'2009-09-04',0,'sost','attribute_year.-86','year','anno',null,null,-86.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-881,'2009-09-04',0,'sost','attribute_month.-86','month','mese',null,null,-86.0,null,null,null);

INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-880,'2009-09-04',0,'sost','template.-85',null,null,1,13,-85.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-879,'2009-09-04',0,'sost','attribute_vat.-85','vat','piva',null,null,-85.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-878,'2009-09-04',0,'sost','attribute_company.-85','company','ragsoc',null,null,-85.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-877,'2009-09-04',0,'sost','attribute_year.-85','year','anno',null,null,-85.0,null,null,null);
INSERT INTO LD_GENERIC (LD_ID,LD_LASTMODIFIED,LD_DELETED,LD_TYPE,LD_SUBTYPE,LD_STRING1,LD_STRING2,LD_INTEGER1,LD_INTEGER2,LD_DOUBLE1,LD_DOUBLE2,LD_DATE1,LD_DATE2) VALUES (-876,'2009-09-04',0,'sost','attribute_month.-85','month','mese',null,null,-85.0,null,null,null);
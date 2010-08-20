insert into ld_menu
           (ld_id,ld_lastmodified,ld_deleted,ld_text,ld_parentid,ld_sort,ld_icon,ld_type,ld_ref,ld_size)
values     (-30,CURRENT_TIMESTAMP,0,'webservice',3,1,'connect.png',0,'admin/webservice',0);

insert into ld_menugroup(ld_menuid, ld_groupid, ld_write , ld_add, ld_security, ld_delete, ld_rename,ld_import,ld_export,ld_immutable, ld_sign, ld_archive,ld_workflow)
values     (-30,1,1,1,1,1,1,1,1,1,1,1,1);
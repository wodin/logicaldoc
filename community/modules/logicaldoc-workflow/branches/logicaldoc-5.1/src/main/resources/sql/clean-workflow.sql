delete from ld_workflowhistory  A
where A.ld_templateid in (select ld_id from ld_workflowtemplate B where B.ld_deleted=1);
delete from ld_workflowtemplate  A where A.ld_deleted=1;

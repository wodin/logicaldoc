<?php 
try { 
$username = "admin";
$password = "admin";
$folderName = "PHP created folder";
$parentFolder = 5.0;

$cfp = array('username' => $username,'password' => $password,'name' => $folderName,'parent' => $parentFolder);
 
$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');

/*****************
 * CREATE
 * ***************/ 
$result = $sclient->createFolder($cfp);
print_r("CREATE\r\n");
print_r($result);

$folder_id = $result->return;

/*****************
 * READ
 * ***************/ 
$cfp = array('username' => $username,'password' => $password,'folder' => $folder_id); 
$result = $sclient->downloadFolderContent($cfp);
print_r("READ\r\n");
print_r($result);

/*****************
 * UPDATE
 * ***************/ 
$cfp = array('username' => $username,'password' => $password,'folder' => $folder_id, 'name' => "PHP renamed folder");
$result = $sclient->renameFolder($cfp);
print_r("UPDATE\r\n");
print_r($result);

/*****************
 * DELETE
 * ***************/ 
$cfp = array('username' => $username,'password' => $password,'folder' => $folder_id);
$result = $sclient->deleteFolder($cfp);
print_r("DELETE\r\n");
print_r($result);

unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

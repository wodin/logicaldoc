<?php 
try { 
$username = "admin";
$password = "admin";
$folderName = "PHP created folder";
$parentFolder = 5.0;

$createFolderParams = array('username' => $username,'password' => $password,'name' => $folderName,'parent' => $parentFolder);
 
$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');

$result = $sclient->createFolder($createFolderParams);
print_r($result);
unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

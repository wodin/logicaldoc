<?php 
try { 
$username = "admin";
$password = "admin";
$folder = 28.0;

$deleteFolderParams = array('username' => $username,'password' => $password,'folder' => $folder);
 
$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');

$result = $sclient->deleteFolder($deleteFolderParams);
print_r($result);
unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

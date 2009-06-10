<?php 
try { 
$username = "admin";
$password = "admin";
$id = 2.0;
$version = "1.0";

$ddp = array('username' => $username,'password' => $password,'id' => $id,'version' => $version);

$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');

$result = $sclient->downloadDocument($ddp);
print_r($result);

// return contains the binary content
$content = $result->return;

$myFile = "C:\\tmp\\testFile.txt";
$fh = fopen($myFile, 'w') or die("can't open file");
fwrite($fh, $content);
fclose($fh);

unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

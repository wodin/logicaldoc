<?php 
try { 
$username = "admin";
$password = "admin";
$id = 3.0;
$version = "1.0";

$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');

$ddi = array('username' => $username,'password' => $password,'id' => $id);
// 1) First invoke the method downloadDocumentInfo to get the filename
$result = $sclient->downloadDocumentInfo($ddi);
print_r($result);

$myFileName = $result->return->filename;
print_r($myFileName);

// 2) Then download the document (binary content)
$ddp = array('username' => $username,'password' => $password,'id' => $id,'version' => $version);

$result = $sclient->downloadDocument($ddp);

$content = $result->return;

// Finally write the document on disk
$myFile = "C:\\tmp\\" . $myFileName;
$fh = fopen($myFile, 'w') or die("can't open file");
fwrite($fh, $content);
fclose($fh);

unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

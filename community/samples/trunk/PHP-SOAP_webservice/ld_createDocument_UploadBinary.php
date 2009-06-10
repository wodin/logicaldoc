<?php 
try { 
$username = "admin";
$password = "admin";
$folder = 5.0;

$cdp = array('username' => $username,'password' => $password,'folder' => $folder);
$cdp['docTitle'] = 'docTitle';
$cdp['source'] = 'source'; 
$cdp['sourceDate'] = '2009-04-06'; 
$cdp['author'] = 'author'; 
$cdp['sourceType'] = 'sourceType'; 
$cdp['coverage'] = 'coverage'; 
$cdp['language'] = 'en';
// Setting some useful Tags for the document created (tags must be separated by commas)
$cdp['tags'] = 'logicaldoc,installation,guide,manual'; 
$cdp['versionDesc'] = 'versionDesc'; 

// read the binary content of a file
$myFile = "logicaldoc-installation_guide-4.5.pdf";
$myFilePath = "C:\\tmp\\" . $myFile;
$fh = fopen($myFilePath, 'r');
$theData = fread($fh, filesize($myFilePath));
fclose($fh);
//echo $theData;

$cdp['filename'] = $myFile; 
$cdp['content'] = $theData; // bytes?? or base64 encoded value? Don't Know BUT IT wORKS!
$cdp['templateName'] = null; 
$cdp['templateFields'] = null; 
$cdp['sourceId'] = 'sourceId';
$cdp['object'] = 'object'; 
$cdp['recipient'] = 'recipient'; 

$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');

// the resurl is the id of the new created document or the string "error"
$result = $sclient->createDocument($cdp);
print_r($result);
unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

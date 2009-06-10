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
$cdp['tags'] = 'tags'; 
$cdp['versionDesc'] = 'versionDesc'; 
$cdp['filename'] = 'filename.txt'; 
$cdp['content'] = 'content'; // bytes?? or base64 encoded value? Don't Know BUT IT wORKS!
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

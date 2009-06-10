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
$document_id = 0.0;

/*****************
 * CREATE
 * ***************/ 
$result = $sclient->createDocument($cdp);
print_r($result);

$document_id = $result->return;
print_r("\nSdocument_id: " . $document_id ."\n\n");

/*****************
 * READ
 * ***************/ 
$cdp = array('username' => $username,'password' => $password,'id' => $document_id);
// 1) First invoke the method downloadDocumentInfo to get the filename
$result = $sclient->downloadDocumentInfo($cdp);
print_r($result);

$myFileName = $result->return->filename;
print_r("\n\r".$myFileName ."\n\r\n\r");

// 2) Then download the document (binary content)
$version = "1.0";
$cdp = array('username' => $username,'password' => $password,'id' => $document_id,'version' => $version);

$result = $sclient->downloadDocument($cdp);

$content = $result->return;

// Finally write the document on disk
$myFile = "C:\\tmp\\" . $myFileName;
$fh = fopen($myFile, 'w') or die("can't open file");
fwrite($fh, $content);
fclose($fh);

/*****************
 * UPDATE
 * ***************/ 
// Maybe we want to change some metadata informations
$cdp = array('username' => $username,'password' => $password,'id' => $document_id);
$cdp['title'] = 'PHP Changed Title';
$cdp['source'] = 'PHP-SOAP'; 
$cdp['sourceAuthor'] = 'myself'; 
$cdp['sourceDate'] = '2009-06-10'; 
$cdp['sourceType'] = 'sourceType'; 
$cdp['coverage'] = 'coverage'; 
$cdp['language'] = 'en'; 
$cdp['tags'] = array('PHP','SOAP','webservice');
$cdp['sourceId'] = 'sourceId';
$cdp['object'] = 'object'; 
$cdp['recipient'] = 'recipient';
//$cdp['templateId'] = null; 
//$cdp['extendedAttribute'] = null; 

$result = $sclient->update($cdp);
print_r($result);


/*****************
 * DELETE
 * ***************/ 
$cdp = array('username' => $username,'password' => $password,'id' => $document_id);
$result = $sclient->deleteDocument($cdp);
print_r($result);


unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?> 

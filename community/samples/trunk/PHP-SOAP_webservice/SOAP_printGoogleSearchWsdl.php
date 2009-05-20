<?php 
try {  
$gsearch = new SoapClient('http://api.google.com/GoogleSearch.wsdl');  
print_r($gsearch->__getFunctions());  
unset($gsearch);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?>
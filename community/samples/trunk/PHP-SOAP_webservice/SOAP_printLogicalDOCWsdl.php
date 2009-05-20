<?php 
try {  
$sclient = new SoapClient('http://localhost:8080/logicaldoc/services/Dms?wsdl');  
print_r($sclient->__getFunctions());  
unset($sclient);  
} catch (SoapFault $e) {  
print_r($e);  
}  
?>
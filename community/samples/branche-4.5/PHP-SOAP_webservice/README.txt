These examples have been created and tested with:
 Windows Vista Business Ed. SP1, PHP 5.2.8 and LogicalDOC 4.5.0

1) Preliminary operations:
   a) ensure that you have the soap extension enabled (to verify try to execute from the shell: php SOAP_printGoogleSearchWsdl.php)
      uncomment the property: extension=php_soap.dll in your php.ini
      
   b) set to false the mtom-enabled property of the LogicalDOC 4.5.0 webservice plugin.
      This configuration property is located in file context-webservice.xml of your LogicalDOC 4.5 web application.

The file context-webservice.xml is the configuration of the LogicalDOC's webservice plugin.
You can find it in folder:  tomcat-6.0.20\webapps\logicaldoc\WEB-INF\.plugins\logicaldoc-webservice@4.5.0\classes

After changing the mtom-enabled to false restart LogicalDOC. 
This can be done simply by turning off and restarting Tomcat.

Note: the default Folder for upload/download of these scripts is C:\tmp anyway you can change this in the scripts.

Final Note: the scripts refer to a LogicalDOC 4.5 Webservice running on port 8080 and accessible with
the path http://localhost:8080/logicaldoc/services/Dms

if your LogicalDOC runs on a different port or with a different path
you have to update the scripts with your LD web-service path.

Have Fun
    A. Gasparini


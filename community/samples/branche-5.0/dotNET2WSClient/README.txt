This example is done in C# for .NET 2.0

To get it to work you must set the mtom-enabled property of the CXF configuration file to false.
This configuration property is located in file context-webservice.xml of your LogicalDOC 5.0 web application.

The file context-webservice.xml is the configuration of the LogicalDOC's webservice plugin.
You can find it in folder:  tomcat-6.0.20\webapps\logicaldoc\WEB-INF\.plugins\logicaldoc-webservice@5.0.0\classes

After changing the mtom-enabled to false restart LogicalDOC. This can be done simply by turning off and restarting Tomcat.

Note: the Default Folder for the downloads is C:\tmp anyway you can change this from the GUI.

To try the the search ensure that there are documents indexed by the system and then you can perform queries in english language.
Each document found by a query will be automatically downloaded into the "Download Folder"

Final Note: the webservice is configured for a LogicalDOC 5.0 running on port 8080 and accessible with
the path http://localhost:8080/logicaldoc/services/Dms

if your logicaldoc runs on a different port or in a different path (this is the case of a LD running as ROOT webapp)
you have to update the Logicaldoc Web Reference with your LD web-service path and rebuild the application.
otherwise change the app.config manually and rebuild the application.

Have Fun
    A. Gasparini


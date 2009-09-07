This is the migration tool to upgrade LogicalDOC 4.5.x to 4.6

Unpack and make sure to have installed Java6 and Ant 1.7
edit the config file classes/conf.properties setting a proper value for 'logicaldoc.contextDir'
than type the command 'ant'

1) Update the property file conf.properties inside the classes folder
   classes/conf.properties
    
   specifying the name of logicaldoc webapp folder (inside the "webapps" folder of tomcat):
   
   logicaldoc.contextDir
   
2) Shutdown tomcat

3) Open a shell to the logicaldoc-migration-4.6-tool folder

4) launch the command:
   ANT

The migration tool will change the filesystem structure of the document repository of logicaldoc,
it will also delete the indexes of LogicalDOC.

5) Move your previous logicaldoc webapp to a new location (outside the "webapps" folder of tomcat)

6) Rename the logicaldoc 4.6 war file in logicaldoc.war

7) Drop logicaldoc.war inside the "webapps" folder of tomcat

8) restart tomcat and perform a new logicaldoc setup
   http://localhost:8080/logicaldoc/setup
   specifying as the Working folder the old LogicalDOC 4.0.x Working folder
   
Note:
Maybe you can view some errors on the database update procedure.
The most frequent is linked to the presence of the logicaldoc-email plugin.
The migration tool attempts to update the tables of this plugin, but if there 
aren't tables the SQL DDLs scripts can't upgrade something that does not 
exists and display the related errors to the console.

Please reports errors using the Help forum or the bug-tracker of LogicalDOC.

Best regards
        The LogicalDOC development Team
         




   
   
Upgrade from LogicalDOC CE 4.0.x to LogicalDOC CE 4.5
(tested with LD 4.0.2 HSQLDB 1.8.x     2009/07/01 blucecio - Windows Vista)
(tested with LD 4.0.2 PostgreSQL 8.3   2009/07/02 blucecio - Windows Vista)

This is the migration tool to upgrade LogicalDOC 4.0.x to 4.5

Unpack and make sure to have installed Java6 and Ant 1.7
edit the config file classes/conf.properties setting a proper value for 'logicaldoc.contextDir'
than type the command 'ant'


1) Update the property file conf.properties inside the classes folder
   classes/conf.properties
    
   specifying the name of logicaldoc webapp folder (inside the "webapps" folder of tomcat):
   
   logicaldoc.contextDir
   
2) Shutdown tomcat

3) Open a shell to the logicaldoc-migration-4.5-tool folder

4) launch the command:
   ANT

The migration tool will change the filesystem structure of the document repository of logicaldoc,
it will also delete the indexes of LogicalDOC.

5) Move your previous logicaldoc webapp to a new location (outside the "webapps" folder of tomcat)

6) Rename the logicaldoc 4.5 war file in logicaldoc.war

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

Note 2:  
There is a good chance that this tool can be used successfully to migrate from version RC1 and RC2 
of LogicalDOC 4.5 to the final LogicalDOC 4.5, but has not been tested yet.
So before you do the migration make sure you have done an exhaustive backup of the database 
and of the Working folder of LogicalDOC.


Please reports errors using the Help forum or the bug-tracker of LogicalDOC.

Best regards
        The LogicalDOC development Team
         




   
   
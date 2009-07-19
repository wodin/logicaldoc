set REPO_DIR="G:\Dokumente und Einstellungen\Sebastian Wenzky\.m2\repository"

 echo + Generate and Deploy new JBPM-Library
 echo + Accessing Folder jbpm 
 cd jbpm
 echo ******************************
 echo + Processing jpdl...
 echo ******************************
call mvn deploy:deploy-file -Durl=file://%REPO_DIR% -Dpackaging=jar -Dfile=jbpm-identity.jar -DgroupId=jboss -DartifactId=jbpm-identity -Dversion=3.2.3
call mvn deploy:deploy-file -Durl=file://%REPO_DIR% -Dpackaging=jar -Dfile=jbpm-identity-sources.jar -DgroupId=jboss -DartifactId=jbpm-identity -Dversion=3.2.3 -Dclassifier=sources
 echo ******************************
 echo + Processing identity...
 echo ******************************
call mvn deploy:deploy-file -Durl=file://%REPO_DIR% -Dpackaging=jar -Dfile=jbpm-jdpl.jar -DgroupId=jboss -DartifactId=jbpm-jpdl -Dversion=3.2.3
call mvn deploy:deploy-file -Durl=file://%REPO_DIR% -Dpackaging=jar -Dfile=jbpm-jpdl-sources.jar -DgroupId=jboss -DartifactId=jbpm-jdpl -Dversion=3.2.3 -Dclassifier=sources

cd ..
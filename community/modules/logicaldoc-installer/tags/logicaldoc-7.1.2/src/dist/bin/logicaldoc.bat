set CATALINA_HOME=%{INSTALL_PATH}\tomcat
set JAVA_OPTS=-Xmx900m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true -Dorg.apache.el.parser.COERCE_TO_ZERO=false -Djava.awt.headless=true

if not "%1" == "restart" goto gotBase
(
%CATALINA_HOME%\bin\catalina.bat stop
%CATALINA_HOME%\bin\catalina.bat start
goto end
)

:gotBase
%CATALINA_HOME%\bin\catalina.bat %1

:end

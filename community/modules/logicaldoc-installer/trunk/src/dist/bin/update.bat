set ANT_HOME=%{INSTALL_PATH}\ant
cd  "%{INSTALL_PATH}\bin"
"%{INSTALL_PATH}\ant\bin\ant" -f .\build.xml update >..\updates\update.log
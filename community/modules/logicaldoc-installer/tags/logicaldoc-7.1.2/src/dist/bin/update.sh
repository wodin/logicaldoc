export ANT_HOME=%{INSTALL_PATH}/ant
cd  "%{INSTALL_PATH}/bin"
"$ANT_HOME/bin/ant" -f ./build.xml update > ../updates/update.log
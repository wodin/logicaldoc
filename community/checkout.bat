svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/documentation/ documentation
svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/logicaldoc/trunk logicaldoc
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/modules/ modules
cd modules 
.\checkout.bat
cd..
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/build/ build
cd build
.\checkout.bat
cd..
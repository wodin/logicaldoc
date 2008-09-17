svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/documentation/ documentation
svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/logicaldoc/trunk logicaldoc
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/modules/ modules
cd modules 
.\checkout.bat
cd..
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/build/ build
cd build
.\checkout.bat
cd..
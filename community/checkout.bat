svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/logicaldoc/trunk logicaldoc
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/modules/ modules
cd modules 
call checkout.bat
cd..
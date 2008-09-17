svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/logicaldoc/trunk logicaldoc
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/community/logicaldoc/modules/ modules
cd modules 
./checkout.sh
cd..

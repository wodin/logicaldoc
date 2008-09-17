svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/documentation/ documentation
svn checkout https://logicaldoc.svn.sourceforge.net/svnroot/logicaldoc/community/logicaldoc/trunk logicaldoc
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/community/logicaldoc/modules/ modules
cd modules 
./checkout.sh
cd..
svn checkout --depth=files https://logicaldoc.svn.sourceforge.net/svnroot/community/logicaldoc/build/ build
cd build
./checkout.sh
cd..


export ANT_HOME=~/apache-ant-1.8.1/
echo $ANT_HOME
export PATH=$PATH:$ANT_HOME
echo $PATH
rpmbuild -ba ~/rpm/SPECS/jpanoramamaker.spec
exit


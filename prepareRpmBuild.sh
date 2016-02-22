#!/bin/bash
#be root by su and ....its going faster
#echo 0 > /selinux/enforce
releasein=1
releaseout=2
rm -rf jpanoramamaker-5
rm -f jpanoramamaker-5.$releasein.src.tar.gz
mkdir ./jpanoramamaker-5

cp jpanoramamaker.spec  /mnt/scratch/jvanek/


cp -r ./src jpanoramamaker-5
cp -r ./nbproject jpanoramamaker-5  
cp ./build.xml jpanoramamaker-5
cp ./manifest.mf jpanoramamaker-5
cp ./license.txt jpanoramamaker-5
cp ./jpanoramamaker.png jpanoramamaker-5
cp ./jpanoramamaker jpanoramamaker-5
cp ./jpanoramamaker.desktop jpanoramamaker-5

chmod -R 777 jpanoramamaker-5
chmod  666 jpanoramamaker-5/license.txt
chmod  666 jpanoramamaker-5/manifest.mf
chmod  666 jpanoramamaker-5/build.xml
chmod  666 jpanoramamaker-5/jpanoramamaker.png
chmod  666 jpanoramamaker-5/jpanoramamaker
chmod  666 jpanoramamaker-5/jpanoramamaker.desktop

rm -f jpanoramamaker-5.$releasein.src.tar.gz
tar -czf jpanoramamaker-5.$releasein.src.tar.gz jpanoramamaker-5
cp -f jpanoramamaker-5.$releasein.src.tar.gz  ~/../rpmbuild/rpm/SOURCES
#upload to wz :)
cp -f jpanoramamaker.spec  ~/../rpmbuild/rpm/SPECS
chmod 644 ~/../rpmbuild/rpm/SOURCES/jpanoramamaker-5.$releasein.src.tar.gz
chmod 644 ~/../rpmbuild/rpm/SPECS/jpanoramamaker.spec	


rm -f jpanoramamaker-5.$releasein.src.tar.gz
rm -rf jpanoramamaker-5
echo "pass rpmbuild"

su rpmbuild ./runRpmBuild.sh

B=echo pwd
cd ~/../rpmbuild/rpm/RPMS/noarch
rpmlint *
cd ~/../rpmbuild/rpm/SRPMS
rpmlint *
cd ~/../rpmbuild/rpm/SPECS
rpmlint *
cd $B

read -p "upload? [y/n] " a
if [ $a == "y" ]; then
tar -cvzf /mnt/scratch/jvanek/jpanoramamaker5.tar.gz ~/../rpmbuild/rpm/*RPMS
echo "uploaded"
else
echo "ok"
fi


read -p "install? [y/n] " a
if [ $a == "y" ]; then
cd ~/../rpmbuild/rpm/RPMS/noarch
sudo yum --nogpgcheck install ./jpanoramamaker-5-$releaseout.fc13.noarch.rpm
sudo yum --nogpgcheck reinstall ./jpanoramamaker-5-$releaseout.fc13.noarch.rpm
cd $B
echo "installed"
else
echo "ok"
fi


read -p "files? [y/n] " a
if [ $a == "y" ]; then
cd ~/../rpmbuild/rpm/RPMS/noarch
rpm -qlp ./jpanoramamaker-javadoc-5-$releaseout.fc13.noarch.rpm
rpm -qlp ./jpanoramamaker-5-$releaseout.fc13.noarch.rpm
cd $B
echo "installed"
else
echo "ok"
fi


read -p "lunch? [y/n] " a
if [ $a == "y" ]; then
jpanoramamaker
jpanoramamaker -deformer
jpanoramamaker -panoramat
echo "lunched?"
else
echo "ok"
fi


echo "done"



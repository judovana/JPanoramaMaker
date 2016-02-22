Name:           jpanoramamaker
Version:        5
Release:        2%{?dist}
Summary:        Tool for stitching photos to panorama in linear curved space
BuildArch:      noarch

#Group:          Applications/Graphics
Group:          Amusements/Graphics
License:        BSD
URL:            http://jpanoramamaker.wz.cz
Source0:        http://jpanoramamaker.wz.cz/fedora/%{name}-5.1.src.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildRequires:  jpackage-utils
BuildRequires:  java-devel
BuildRequires:  ant
BuildRequires:  ant-nodeps
BuildRequires:  swing-layout
BuildRequires:  desktop-file-utils

Requires:       jpackage-utils
Requires:       java
Requires:       swing-layout

%description
Tool for stitching photos to panorama in linear curved space

%package javadoc
Summary:        Javadocs for %{name}
Group:          Documentation
Requires:       %{name} = %{version}-%{release}
Requires:       jpackage-utils

%description javadoc
This package contains the API documentation for %{name}.



%prep
%setup -q


find -name '*.class' -exec rm -f '{}' \;
find -name '*.jar' -exec rm -f '{}' \;

%build
ant

#at this time the only existing test is executing and killing whole app.
#DISPLAY=:0.0
#export DISPLAY
#ant run-test-with-main


%install
rm -rf $RPM_BUILD_ROOT

#desktop
mkdir -p $RPM_BUILD_ROOT%{_datadir}/pixmaps
desktop-file-install --dir=${RPM_BUILD_ROOT}%{_datadir}/applications  jpanoramamaker.desktop
cp -p ./jpanoramamaker.png  $RPM_BUILD_ROOT%{_datadir}/pixmaps/jpanoramamaker.png
#end desktop

#launcher
mkdir -p $RPM_BUILD_ROOT%{_bindir}/
cp -p ./jpanoramamaker $RPM_BUILD_ROOT%{_bindir}/jpanoramamaker
#end launcher



# we are in /BUILD/jpanoramamaker-5/
mkdir -p $RPM_BUILD_ROOT%{_javadir}
cp -p ./dist/%{name}.jar  $RPM_BUILD_ROOT%{_javadir}/%{name}-%{version}.jar


mkdir -p $RPM_BUILD_ROOT%{_javadocdir}/%{name}
cp -rp ./dist/javadoc/  $RPM_BUILD_ROOT%{_javadocdir}/%{name}
ln -s %{_javadocdir}/%{name} $RPM_BUILD_ROOT%{_javadocdir}/%{name}-%{version}




#####################################

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
%{_datadir}/pixmaps/jpanoramamaker.png
%{_datadir}/applications/jpanoramamaker.desktop
%attr(755,root,root) %{_bindir}/jpanoramamaker


%defattr(-,root,root,-)
%{_javadir}/*
%doc license.txt


%files javadoc
%defattr(-,root,root,-)
%{_javadocdir}/%{name}
%{_javadocdir}/%{name}-%{version}


%changelog
* Thu Sep 30 2010 Jiri Vanek <jvanek@redhat.com> - 5-2
-added desktop integration
-launcher extracted to separated file


* Wed Sep 29 2010 Jiri Vanek <jvanek@redhat.com> - 5-1
-first release of version 5


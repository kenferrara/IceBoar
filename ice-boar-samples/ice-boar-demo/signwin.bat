@echo off

REM Define your variables here
set KEY_STORE_JKS=mykeystore.jks
set KEY_STORE_PASSWORD=password
set KEY_STORE_ALIAS=myalias
set TOMCAT_WEBAPPS_DIR=c:\apache-tomcat-8.0.28\webapps
set MAVEN_REPO=C:\Users\XXXXXXXX\.m2\repository
set ICE_BOAR_VERSION=0.7


REM Copy Ice Boar JARs to libs
mkdir libs
copy %MAVEN_REPO%\com\roche\ice-boar\%ICE_BOAR_VERSION%\ice-boar-%ICE_BOAR_VERSION%.jar libs\
copy %MAVEN_REPO%\com\roche\ice-boar\%ICE_BOAR_VERSION%\ice-boar-%ICE_BOAR_VERSION%-jar-with-dependencies.jar libs\
copy %MAVEN_REPO%\com\roche\ice-boar-hello-world-swing\%ICE_BOAR_VERSION%\ice-boar-hello-world-swing-%ICE_BOAR_VERSION%.jar libs\
copy %MAVEN_REPO%\com\roche\ice-boar-hello-world-swing\%ICE_BOAR_VERSION%\ice-boar-hello-world-swing-%ICE_BOAR_VERSION%-jar-with-dependencies.jar libs\

REM sign JARs
jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\ice-boar-%ICE_BOAR_VERSION%.jar %KEY_STORE_ALIAS%

jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\ice-boar-%ICE_BOAR_VERSION%-jar-with-dependencies.jar %KEY_STORE_ALIAS%

REM You need to sing this when you would run demo without IceBoar
jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\ice-boar-hello-world-swing-%ICE_BOAR_VERSION%-jar-with-dependencies.jar %KEY_STORE_ALIAS%

jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\ice-boar-hello-world-swing-%ICE_BOAR_VERSION%.jar %KEY_STORE_ALIAS%

mkdir %TOMCAT_WEBAPPS_DIR%\apps
copy libs\ice-boar-hello-world-swing-%ICE_BOAR_VERSION%.jar %TOMCAT_WEBAPPS_DIR%\apps
copy libs\ice-boar-hello-world-swing-%ICE_BOAR_VERSION%-jar-with-dependencies.jar %TOMCAT_WEBAPPS_DIR%\apps\
copy libs\ice-boar-%ICE_BOAR_VERSION%.jar %TOMCAT_WEBAPPS_DIR%\apps\
copy libs\ice-boar-%ICE_BOAR_VERSION%-jar-with-dependencies.jar %TOMCAT_WEBAPPS_DIR%\apps\

REM Copy dependencies JAR's to local folder
copy %MAVEN_REPO%\commons-io\commons-io\2.2\commons-io-2.2.jar libs\
copy %MAVEN_REPO%\org\apache\commons\commons-lang3\3.1\commons-lang3-3.1.jar libs\
copy %MAVEN_REPO%\com\google\guava\guava\11.0.2\guava-11.0.2.jar libs\
copy %MAVEN_REPO%\net\lingala\zip4j\zip4j\1.3.2\zip4j-1.3.2.jar libs\

REM Sign dependencies
jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\commons-io-2.2.jar %KEY_STORE_ALIAS%

jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\commons-lang3-3.1.jar %KEY_STORE_ALIAS%

jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\guava-11.0.2.jar %KEY_STORE_ALIAS%

jarsigner -keystore %KEY_STORE_JKS% -storepass %KEY_STORE_PASSWORD% -tsa http://timestamp.digicert.com libs\zip4j-1.3.2.jar %KEY_STORE_ALIAS%

REM Copy dependencies to tomcat webapps dir
copy libs\commons-io-2.2.jar %TOMCAT_WEBAPPS_DIR%\apps\
copy libs\commons-lang3-3.1.jar %TOMCAT_WEBAPPS_DIR%\apps\
copy libs\guava-11.0.2.jar %TOMCAT_WEBAPPS_DIR%\apps\
copy libs\zip4j-1.3.2.jar %TOMCAT_WEBAPPS_DIR%\apps\

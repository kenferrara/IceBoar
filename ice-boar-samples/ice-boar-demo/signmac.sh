#!/usr/bin/env bash

# Define your variables here
KEY_STORE_JKS=mykeystore.jks
KEY_STORE_PASSWORD=password
KEY_STORE_ALIAS=myalias
TOMCAT_WEBAPPS_DIR=/usr/local/Cellar/tomcat/8.0.27/libexec/webapps
MAVEN_REPO=~/.m2/repository
ICE_BOAR_VERSION = 0.5

# Copy Ice Boar JARs to libs
mkdir libs
cp $MAVEN_REPO/com/roche/ice-boar/$ICE_BOAR_VERSION/ice-boar-$ICE_BOAR_VERSION.jar libs/
cp $MAVEN_REPO/com/roche/ice-boar/$ICE_BOAR_VERSION/ice-boar-$ICE_BOAR_VERSION-jar-with-dependencies.jar libs/
cp $MAVEN_REPO/com/roche/ice-boar-hello-world-swing/$ICE_BOAR_VERSION/ice-boar-hello-world-swing-$ICE_BOAR_VERSION.jar libs/
cp $MAVEN_REPO/com/roche/ice-boar-hello-world-swing/$ICE_BOAR_VERSION/ice-boar-hello-world-swing-$ICE_BOAR_VERSION-jar-with-dependencies.jar libs/

# Sign JARs
jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/ice-boar-$ICE_BOAR_VERSION.jar $KEY_STORE_ALIAS

jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/ice-boar-$ICE_BOAR_VERSION-jar-with-dependencies.jar $KEY_STORE_ALIAS

# You need to sign this when you would run demo witchout IceBoar
jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/ice-boar-hello-world-swing-$ICE_BOAR_VERSION-jar-with-dependencies.jar $KEY_STORE_ALIAS

mkdir $TOMCAT_WEBAPPS_DIR/apps
cp libs/ice-boar-hello-world-swing-$ICE_BOAR_VERSION.jar $TOMCAT_WEBAPPS_DIR/apps/
cp libs/ice-boar-hello-world-swing-$ICE_BOAR_VERSION-jar-with-dependencies.jar $TOMCAT_WEBAPPS_DIR/apps/
cp libs/ice-boar-$ICE_BOAR_VERSION.jar $TOMCAT_WEBAPPS_DIR/apps/
cp libs/ice-boar-$ICE_BOAR_VERSION-jar-with-dependencies.jar $TOMCAT_WEBAPPS_DIR/apps/

# Copy dependencies JAR's to local folder
mkdir libs
cp $MAVEN_REPO/commons-io/commons-io/2.2/commons-io-2.2.jar libs/
cp $MAVEN_REPO/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar libs/
cp $MAVEN_REPO/com/google/guava/guava/11.0.2/guava-11.0.2.jar libs/
cp $MAVEN_REPO/net/lingala/zip4j/zip4j/1.3.2/zip4j-1.3.2.jar libs/

# Sign dependencies
jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/commons-io-2.2.jar $KEY_STORE_ALIAS

jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/commons-lang3-3.1.jar $KEY_STORE_ALIAS

jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/guava-11.0.2.jar $KEY_STORE_ALIAS

jarsigner -keystore $KEY_STORE_JKS -storepass $KEY_STORE_PASSWORD -tsa http://timestamp.digicert.com libs/zip4j-1.3.2.jar $KEY_STORE_ALIAS

# Copy dependencies to tomcat webapps dir
mkdir $TOMCAT_WEBAPPS_DIR/apps/libs/
cp libs/commons-io-2.2.jar $TOMCAT_WEBAPPS_DIR/apps/libs/
cp libs/commons-lang3-3.1.jar $TOMCAT_WEBAPPS_DIR/apps/libs/
cp libs/guava-11.0.2.jar $TOMCAT_WEBAPPS_DIR/apps/libs/
cp libs/zip4j-1.3.2.jar $TOMCAT_WEBAPPS_DIR/apps/libs/

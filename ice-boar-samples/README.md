# How to build a project?
 
IceBoar samples contains 2 maven modules:

* ice-boar-hello-world-swing - Contains a simple standalone Java Swing application which displays JFrame with a text 
box. This application demonstrates that ice-boar can start another Java application on different JVM and deal with 
its properties. 
* ice-boar-demo - Contains a sample web application to show how ice-boar might be used in other projects.

# How to run a demo project?
Running a demo is a little bit complicated, because there are no JRE zip files nor signed JARs available in the 
source code repository.

1) Build ice-boar and ice-boar-samples projects using maven: `mvn clean install`. 

2) Generate (if you don't have) a certificate to sign your JAR files in next step. Easiest way:
`keytool -keystore keyStore.jks -genkey -alias myalias`

More info: [Creating a KeyStore in JKS Format](https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6er/index.html).

3) You need to install tomcat or other servlet container on your machine. 

4) Prepare a ZIP file with JRE that will be used to execute the target application and place it under 
TOMCAT_DIR/webapps/jre. ZIP archives to be prepared for the demo (they are specified in ice-boar-demo module):

- jre-1.7.0_04-win-x64.zip
- jre-1.7.0_04-win-x32.zip
- jre-1.7.0_04-macosx-x64.zip

5) Open and adjust paths in [ice-boar-demo/signwin.bat](ice-boar-demo/signwin.bat) or 
[ice-boar-demo/signmac.sh](ice-boar-demo/signmac.sh) depend on your OS and run it.

6) Start your servlet container and deploy ice-boar-demo.war.

7) Go to localhost:8080/ice-boar-demo and click links. 

## What do signwin.bat and signmac.sh scripts?
 
This scripts make following steps:

1) Sign ice-boar artifacts (regular jar and jar with dependencies).

2) Sign ice-boar-hello-world-swing. In some cases you can skip this step.

3) Create subdirectory `apps` in tomcat webapps directory.

4) Copy ice-boar-hello-world-swing and ice-boar jars to webapps directory.

5) Create directory `libs`

6) Copy from Maven repository necessary dependencies to `libs` directory.

7) Sign this dependencies.

8) Create `/apps/libs/` directory in tomcat webapps.

8) Copy signed dependencies to tomcat webapps `/apps/libs/` dir.

## Remarks

1) Run servlet container with ice-boar-demo direct from your IDE will doesn't work. A JNLP demo files need to be 
filtered by maven during build.

2) Why I don't automated this steps with maven? 
Because I won't have zip files with JRE in source code repository. The second reason is that ice-boar-demo is changed 
very slowly. Most of development is in ice-boar module. Develop with this help scripts is faster than build a WAR 
and restarting embedded tomcat. 

# How to run ice-boar-demo-gh-pages project?

Running this demo is straightforward (instead of ice-boar-demo). If you would to run it locally:

* `mvn clean install`
* `mvn tomcat7:run-war`
* Go to: http://localhost:8080/ice-boar-demo-gh-pages

If you need to build a project for [gh-pages](http://roche.github.io/IceBoar/) please use ghpages profile:

* `mvn clean install -Pghpages`

## FAQ

Q: Why ice-boar-demo and ice-boar-demo-gh-pages exist if the second project is straightforward?
A: ice-boar-demo-gh-pages is a static content and works only on http://localhost:8080/ice-boar-demo-gh-pages or 
http://roche.github.io/IceBoar/ This values are replaced during maven build chosen by maven profile (default or 
ghpages). ice-boar-demo use servlet for downloading JNLP files and modify a content on the fly. It works on every 
context path, port, hostname etc. and it's easier for prototyping. 
    
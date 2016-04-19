#!/bin/bash
set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	cd ice-boar && \ 
	mvn clean deploy --settings settings.xml && \
	cd .. && \
	mvn clean install -U && \
	cd ice-boar-samples/ice-boar-demo-gh-pages/ && \
	mvn clean install -P ghpages && \
	git clone https://github.com/Roche/IceBoar && \
	cd IceBoar && \
	git checkout gh-pages && \
	cp -R ../target/ice-boar-demo-gh-pages/* . && \
	git config user.name "Marcin Stachniuk" && \
	git config user.email marcin.stachniuk@contractors.roche.com && \
	git add -A && \
	git commit -m "update gh-pages by Travis-CI" && \
	git push ${GIT_HUB_REPO_SECRET}
else
	cd ice-boar && \
	mvn clean install && \
	cd .. && \
	mvn clean install -U
fi

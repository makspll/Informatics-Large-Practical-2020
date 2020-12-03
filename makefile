ifeq ($(IN),)
IN:=predictions.txt
endif

JARNAME_CW1="heatmap-0.0.1-SNAPSHOT.jar"
MVNROOT_CW1=./heatmap
MVNFLAGS_CW1=--f "${MVNROOT_CW1}/pom.xml"
buildcw1:
	mvn clean package -Dmaven.test.skip=true ${MVNFLAGS_CW1} ;
runcw1: buildcw1
	java -ea -jar $(MVNROOT_CW1)/target/$(JARNAME_CW1) $(IN)
testcw1:
	mvn test ${MVNFLAGS_CW1};
watchcw1:
	while inotifywait -e close_write test.txt; do make runcw1 IN="$(IN)" ; done
submitcw1:
	mvn clean ${MVNFLAGS_CW1};
	rm -rf bin;
	mkdir bin;
	zip -r bin/heatmap.zip ${MVNROOT_CW1};

JARNAME_CW2="aqmaps-0.0.1-SNAPSHOT.jar"
MVNROOT_CW2=./aqmaps
MVNFLAGS_CW2=--f "${MVNROOT_CW2}/pom.xml"
SERVER_JAR_DIR=./WebServer/WebServer
SERVER_JAR=WebServerLite.jar
SERVER_PORT=9898
startserver:
	echo "Running web server at http://localhost:${SERVER_PORT}";
	@cd ${SERVER_JAR_DIR} && java -jar ${SERVER_JAR} . ${SERVER_PORT};
buildcw2:
	mvn clean package -Dmaven.test.skip=true ${MVNFLAGS_CW2};
runcw2: buildcw2
	java -ea -jar $(MVNROOT_CW2)/target/$(JARNAME_CW2);
testcw2:
	mvn test ${MVNFLAGS_CW2};
watchcw2:
	while inotifywait -e close_write test.txt; do make runcw2"; done
submitcw2:
	@cd ${SERVER_JAR_DIR} && java -jar ${SERVER_JAR} . ${SERVER_PORT}&
	mvn clean package -Dmaven.test.skip=true ${MVNFLAGS_CW2};
	./preparecw2.sh;
makedocscw2:
	@mvn javadoc:javadoc -Dmaven.test.skip=true ${MVNFLAGS_CW2};
	python report/javadoc-extractor/classes_to_tex.py aqmaps/target/site/apidocs;
	mv doc.tex report/javadoc-extractor/doc.tex;
	rm paste-me-directly.tex


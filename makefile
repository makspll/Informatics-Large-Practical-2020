ifeq ($(IN),)
IN:=predictions.txt
endif

JARNAME="heatmap-0.0.1-SNAPSHOT.jar"
MVNROOT="./heatmap"
MVNFLAGS=-"f ${MVNROOT}/pom.xml"
build:
	mvn clean package -Dmaven.test.skip=true -f ${MVNFLAGS} ;
run: build
	java -ea -jar $(MVNROOT)/target/$(JARNAME) $(IN)
test:
	mvn test -f ${MVNFLAGS};
watch:
	while inotifywait -e close_write test.txt; do make run IN="$(IN)" ; done
submit:
	@mvn clean -f ${MVNFLAGS};
	@rm -rf bin;
	@mkdir bin;
	@zip -r bin/heatmap.zip ${MVNROOT};
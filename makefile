ifeq ($(IN),)
IN := predictions.txt
endif

JARNAME="heatmap-0.0.1-SNAPSHOT.jar"
MVNROOT="./heatmap"
build:
	mvn clean package -Dmaven.test.skip=true -f $(MVNROOT)/pom.xml ;
run: build
	java -ea -jar $(MVNROOT)/target/$(JARNAME) $(IN)
test:
	mvn test -f $(MVNROOT)/pom.xml ;
watch:
	while inotifywait -e close_write test.txt; do make run IN="$(IN)" ; done
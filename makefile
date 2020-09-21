ifeq ($(IN),)
IN := predictions.txt
endif

MVNROOT="./heatmap"
build:
	mvn clean package -Dmaven.test.skip=true -f $(MVNROOT)/pom.xml ;
run: build
	java -ea -jar `find $(MVNROOT)/target/ -name "*.jar"` $(IN)
test:
	mvn test -f $(MVNROOT)/pom.xml ;
watch:
	while inotifywait -e close_write test.txt; do make run IN="$(IN)" ; done
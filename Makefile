default:
	javac -classpath lib -d classes/ src/*.java

clean:
	rm -f classes/*
	rm -f leaphack.jar

run: test
test:
	java -classpath lib:classes Main

jar:
	jar cvfM leaphack.jar META-INF classes/*

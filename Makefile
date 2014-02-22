default:
	javac -classpath lib/leapJava.jar -d classes/ src/*.java

clean:
	rm -f classes/*
	rm -f leaphack.jar

run: test
test:
	java -Djava.library.path=lib -classpath classes:lib/leapJava.jar Main

jar:
	jar cvfM leaphack.jar META-INF classes/*

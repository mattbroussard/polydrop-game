default:
	javac -classpath lib/leapJava.jar:lib/jbox2d-library-2.2.1.1.jar -d classes/ src/*.java

clean:
	rm -f classes/*
	rm -f leaphack.jar

run: test
test:
	java -Djava.library.path=lib -classpath classes:lib/leapJava.jar:lib/jbox2d-library-2.2.1.1.jar Main

go: default test

jar:
	jar cvfM leaphack.jar META-INF classes/*

default:
	javac -classpath lib/leapJava.jar:lib/jbox2d-library-2.2.1.1.jar -d classes/ src/*.java

clean:
	rm -f classes/*
	rm -f leaphack.jar

run: test
test:
	java -Djava.library.path=lib -classpath res:classes:lib/leapJava.jar:lib/jbox2d-library-2.2.1.1.jar Main
windowed:
	java -Djava.library.path=lib -classpath res:classes:lib/leapJava.jar:lib/jbox2d-library-2.2.1.1.jar Main --windowed

go: default test

all: compile

compile:
	java -jar ../jtb132di.jar miniJ.jj
	java -jar ../javacc5.jar miniJ-jtb.jj
	javac Main.java

clean:
	rm -f *.class *~

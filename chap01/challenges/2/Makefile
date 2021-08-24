default: run

clean:
	@rm -f hello.jar

hello.jar: main.kt
	@kotlinc -nowarn *.kt -include-runtime -d hello.jar

run: hello.jar
	@java -jar hello.jar
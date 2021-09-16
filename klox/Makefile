default: build

clean:
	@rm -f klox.jar

build: Lox.kt
	@kotlinc -nowarn *.kt -include-runtime -d klox.jar

repl: build
	@java -jar klox.jar
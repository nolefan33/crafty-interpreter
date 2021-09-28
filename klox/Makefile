default: run_file

clean:
	@rm -f klox.jar

build: Lox.kt
	@kotlinc -nowarn *.kt -include-runtime -d klox.jar

repl: build
	@java -jar klox.jar

run_file: build
ifdef IN_FILE
	@java -jar klox.jar $(IN_FILE)
else
	@echo 'usage: make IN_FILE="<file>" run_file'
endif
default: run

clean:
	@rm -rf out

build: main.c
	@mkdir -p out
	@gcc main.c -o out/main

run: build
	@./out/main
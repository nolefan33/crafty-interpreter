default: run

clean:
	@rm -rf out/

build: main.kt
	@mkdir -p out
	@kotlinc-native main.kt -o out/main

run: build
	@./out/main.kexe

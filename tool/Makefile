default: build_ast

clean:
	@rm -f gen_ast.jar

build_ast: GenerateAst.kt
	@kotlinc -nowarn GenerateAst.kt -include-runtime -d gen_ast.jar

gen_ast: build_ast
ifdef OUT_DIR
	@java -jar gen_ast.jar $(OUT_DIR)
else
	@java -jar gen_ast.jar ../klox
endif
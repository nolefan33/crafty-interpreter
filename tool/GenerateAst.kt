package com.craftyinterpreter.tool

import java.io.PrintWriter

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        System.exit(64)
    }

    val outputDir = args[0]

    defineAst(outputDir, "Expr", listOf(
        "Binary   : Expr left, Token operator, Expr right",
        "Grouping : Expr expression",
        "Literal  : Any? value",
        "Unary    : Token operator, Expr right",
        "Variable : Token name"
    ))

    defineAst(outputDir, "Stmt", listOf(
        "Expression : Expr expression",
        "Print      : Expr expression",
        "Var        : Token name, Expr initializer"
    ))
}

private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
    val path = "$outputDir/$baseName.kt";
    PrintWriter(path, "UTF-8").use { writer ->
        writer.println("package com.craftyinterpreter.klox")
        writer.println()

        writer.println("sealed interface $baseName {")

        defineVisitor(writer, baseName, types)
        writer.println("\tfun <R> accept(visitor: Visitor<R>): R");
        
        writer.println("}")
        writer.println()

        val typeStrings = types.joinToString("\n\n") { type ->
            val className = type.split(":")[0].trim()
            val fields = type.split(":")[1].trim().split(", ").joinToString(",\n    ") { field ->
                val splitField = field.split(" ") // e.g. "Token operator"
                """
                |val ${splitField[1]}: ${splitField[0]}
                """.trimMargin("|")
            }

            """
            |data class $className(
            |    $fields
            |) : $baseName {
            |    override fun <R> accept(visitor: $baseName.Visitor<R>): R = visitor.visit${className}${baseName}(this)
            |}
            """.trimMargin("|")
        }
        writer.println(typeStrings)
    }
}

private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
    writer.println("\tinterface Visitor<R> {");

    types.forEach() { type ->
        val typeName = type.split(":")[0].trim()
        writer.println("\t\tfun visit${typeName}${baseName}(${baseName.toLowerCase()}: $typeName): R")
    }

    writer.println("\t}\n");
}

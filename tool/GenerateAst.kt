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
        "Literal  : Any value",
        "Unary    : Token operator, Expr right"
    ))
}

private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
    val path = "$outputDir/$baseName.kt";
    PrintWriter(path, "UTF-8").use { writer ->
        writer.println("package com.craftyinterpreter.klox")
        writer.println()
        writer.println("interface Expr { }")
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
            |) : $baseName
            """.trimMargin("|")
        }
        writer.println(typeStrings)
    }
}
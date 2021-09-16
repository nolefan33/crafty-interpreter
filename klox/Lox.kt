package com.craftyinterpreter.lox;

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.Charset

fun main(args: Array<String>) {
    when (args.size) {
        0 -> Lox.runPrompt()
        1 -> Lox.runFile(args[0])
        else -> {
            println("Usage: jlox [script]")
        }
    }
}

object Lox {
    var hadError = false
        private set

    fun runFile(path: String) {
        val fileBytes = Files.readAllBytes(Paths.get(path))
        runProgram(String(fileBytes, Charset.defaultCharset()))
        
        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
    }

    fun runPrompt() {
        while(true) {
            print("> ")
            val line = readLine() ?: break
            runProgram(line)
            hadError = false;
        }
    }

    fun error(line: Int, message: String) = report(line, "", message)

    private fun runProgram(source: String) {
        val tokens = source.scanTokens()
        println(tokens.joinToString("\n"))
    }

    private fun report(line: Int, where: String, message: String) {
        println("[line $line] Error$where: $message")
        hadError = true
    }

    private fun String.scanTokens(): List<Token> = Scanner(this).scanTokens()
}
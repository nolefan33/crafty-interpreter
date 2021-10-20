package com.craftyinterpreter.klox

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
    var hadRuntimeError = false
        private set

    private val interpreter = Interpreter()

    fun runFile(path: String) {
        val fileBytes = Files.readAllBytes(Paths.get(path))
        runProgram(String(fileBytes, Charset.defaultCharset()))
        
        // Indicate an error in the exit code.
        if (hadError) System.exit(65)
        if (hadRuntimeError) System.exit(70)
    }

    fun runPrompt() {
        while(true) {
            print("> ")
            val line = readLine() ?: break
            runProgram(line)
            hadError = false;
        }
    }

    fun runtimeError(error: RuntimeError) {
        System.err.println("${error.message}\n[line ${error.token.line}]")
        hadRuntimeError = true
      }

    fun error(line: Int, message: String) = report(line, "", message)

    fun error(token: Token, message: String) = when(token.type) {
        TokenType.EOF -> report(token.line, " at end", message)
        else -> report(token.line, " at '" + token.lexeme + "'", message)
    }

    private fun runProgram(source: String) {
        val tokens = source.scanTokens()
        val parser = Parser(tokens)
        val statements = parser.parse() ?: return

        // Stop if there was a syntax error.
        if (hadError) return

        interpreter.interpret(statements)
    }

    private fun report(line: Int, where: String, message: String) {
        println("[line $line] Error$where: $message")
        hadError = true
    }

    private fun String.scanTokens(): List<Token> = Scanner(this).scanTokens()
}
package com.craftyinterpreter.lox;

class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        return tokens.also { it.add(Token(TokenType.EOF, "", null, line)) }
    }

    private fun isAtEnd() = current >= source.length

    private fun scanToken() {
        TODO()
    }
}
package com.craftyinterpreter.klox

class Scanner(private val source: String) {
    companion object {
        private val keywords = mapOf(
            "and" to TokenType.AND,
            "class" to TokenType.CLASS,
            "else" to TokenType.ELSE,
            "false" to TokenType.FALSE,
            "for" to TokenType.FOR,
            "fun" to TokenType.FUN,
            "if" to TokenType.IF,
            "nil" to TokenType.NIL,
            "or" to TokenType.OR,
            "print" to TokenType.PRINT,
            "return" to TokenType.RETURN,
            "super" to TokenType.SUPER,
            "this" to TokenType.THIS,
            "true" to TokenType.TRUE,
            "var" to TokenType.VAR,
            "while" to TokenType.WHILE
        )
    }

    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }

        return tokens.also { it.add(Token(TokenType.EOF, "", null, line)) }
    }

    private fun isAtEnd() = current >= source.length

    private fun scanToken() {
        when (val character = advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (matchNext('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (matchNext('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (matchNext('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (matchNext('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> if (matchNext('/')) scanComment(blockComment = false)
                   else if (matchNext('*')) scanComment(blockComment = true)
                   else addToken(TokenType.SLASH)
            ' ', '\r', '\t' -> { /* ignore whitespace */ }
            '\n' -> line++
            '"' -> scanString()
            else -> if (isDigit(character)) scanNumber()
                    else if (isAlpha(character)) scanIdentifier()
                    else Lox.error(line, "Unexpected character '$character'.")
        }
    }

    private fun advance(): Char = source[current++]
    
    private fun addToken(type: TokenType) = addToken(type, null)
    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun matchNext(expected: Char): Boolean = (!isAtEnd() && source.get(current) == expected).also {
        if (it) current++
    }

    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]
    private fun peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private fun isDigit(c: Char) = c >= '0' && c <= '9'
    private fun isAlpha(c: Char) = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'
    private fun isAlphaNumeric(c: Char) = isDigit(c) || isAlpha(c)

    private fun scanComment(blockComment: Boolean) {
        if (!blockComment) {
            while (peek() != '\n' && !isAtEnd()) advance()
        } else {
            while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                if (peek() == '/' && peekNext() == '*') {
                    advance()
                    advance()
                    scanComment(blockComment = true)
                }
                val c = advance()
                if (c == '\n') line++
            }
            if (!isAtEnd()) advance()
            if (!isAtEnd()) advance()
        }
    }

    private fun scanString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        // skip the terminating "
        advance()
        
        // trim the quotes
        addToken(TokenType.STRING, source.substring(start + 1, current - 1))
    }

    private fun scanNumber() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance()
      
            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun scanIdentifier() {
        while (isAlphaNumeric(peek())) advance()

        val tokenType = keywords.get(source.substring(start, current)) ?: TokenType.IDENTIFIER
        addToken(tokenType)
    }
}
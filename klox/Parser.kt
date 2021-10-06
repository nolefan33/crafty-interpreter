package com.craftyinterpreter.klox

import com.craftyinterpreter.klox.TokenType.*

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Expr? = try {
        expression()
    } catch (_: ParseError) {
        null
    }

    private fun expression() = equality()
    private fun equality(): Expr = parseRecursiveBinaryExpr(::comparison, listOf(BANG_EQUAL, EQUAL_EQUAL))
    private fun comparison(): Expr = parseRecursiveBinaryExpr(::term, listOf(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
    private fun term(): Expr = parseRecursiveBinaryExpr(::factor, listOf(MINUS, PLUS))
    private fun factor(): Expr = parseRecursiveBinaryExpr(::unary, listOf(SLASH, STAR))

    private fun unary(): Expr = when {
        match(listOf(BANG, MINUS)) -> {
            val operator = previous()
            val rExpr = unary()

            Unary(operator, rExpr)
        }
        else -> primary()
    }

    private fun primary(): Expr = when {
        match(FALSE) -> Literal(false)
        match(TRUE) -> Literal(true)
        match(NIL) -> Literal(null)
        match(listOf(NUMBER, STRING)) -> Literal(previous().literal)
        match(LEFT_PAREN) -> {
            val expr = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")
            
            Grouping(expr)
        }
        else -> throw parseError(peek(), "Expected expression.")
    }

    private fun parseRecursiveBinaryExpr(nextPrecedence: () -> Expr, types: List<TokenType>): Expr {
        var expr = nextPrecedence()
        
        while (match(types)) {
            val operator = previous()
            val rExpr = nextPrecedence()
            expr = Binary(expr, operator, rExpr)
        }

        return expr
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance();
    
        throw parseError(peek(), message);
      }

    private fun match(type: TokenType) = match(listOf(type))
    private fun match(types: List<TokenType>): Boolean = types.any { 
        if (check(it)) {
            advance()
            true
        } else false
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun check(type: TokenType): Boolean = !isAtEnd() && peek().type == type
    private fun isAtEnd(): Boolean = peek().type == EOF
    private fun peek(): Token = tokens[current]
    private fun previous(): Token = tokens[current - 1]

    private fun parseError(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return
            when (peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
                else -> advance()
            }
        }
    }

    private class ParseError : RuntimeException() {}
}
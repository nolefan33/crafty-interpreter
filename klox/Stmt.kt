package com.craftyinterpreter.klox

sealed interface Stmt {
	interface Visitor<R> {
		fun visitExpressionStmt(stmt: Expression): R
		fun visitPrintStmt(stmt: Print): R
		fun visitVarStmt(stmt: Var): R
	}

	fun <R> accept(visitor: Visitor<R>): R
}

data class Expression(
    val expression: Expr
) : Stmt {
    override fun <R> accept(visitor: Stmt.Visitor<R>): R = visitor.visitExpressionStmt(this)
}

data class Print(
    val expression: Expr
) : Stmt {
    override fun <R> accept(visitor: Stmt.Visitor<R>): R = visitor.visitPrintStmt(this)
}

data class Var(
    val name: Token,
    val initializer: Expr
) : Stmt {
    override fun <R> accept(visitor: Stmt.Visitor<R>): R = visitor.visitVarStmt(this)
}

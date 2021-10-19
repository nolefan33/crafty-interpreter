package com.craftyinterpreter.klox

import com.craftyinterpreter.klox.TokenType.*

class Interpreter : Expr.Visitor<Any?> {
    fun interpret(expression: Expr) = try {
        val value = evaluate(expression)
        println(stringify(value))
    } catch (error: RuntimeError) {
        Lox.runtimeError(error)
    }

    override fun visitLiteralExpr(expr: Literal): Any? = expr.value
    override fun visitGroupingExpr(expr: Grouping): Any? = evaluate(expr.expression)

    override fun visitUnaryExpr(expr: Unary): Any? {
        val rVal = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> !isTruthy(rVal)
            MINUS -> -safeCastNumberOperand(expr.operator, rVal)
            else -> null
        }
    }

    override fun visitBinaryExpr(expr: Binary): Any? {
        val lVal = evaluate(expr.left)
        val rVal = evaluate(expr.right)
        
        return when (expr.operator.type) {
            GREATER -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::greaterThan)
            LESS -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::lessThan)
            GREATER_EQUAL -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::greaterThanOrEqual)
            LESS_EQUAL -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::lessThanOrEqual)
            MINUS -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::minus)
            PLUS -> when {
                lVal is String -> lVal + stringify(rVal)
                rVal is String -> stringify(lVal) + rVal
                lVal is Double -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::plus)
                else -> throw RuntimeError(expr.operator, "Operands must be two numbers or one value must be a string.")
            }
            SLASH -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::div)
            STAR -> safePerformNumberOperation(expr.operator, lVal, rVal, Double::times)
            BANG_EQUAL -> !isEqual(lVal, rVal)
            EQUAL_EQUAL -> isEqual(lVal, rVal)
            else -> throw RuntimeError(expr.operator, "Binary expression didn't have expected operator in the middle.")
        }
    }

    private fun isTruthy(value: Any?): Boolean = when (value) {
        null -> false
        is Boolean -> value
        else -> true
    }

    // TODO: numbers are weird because of doubles
    private fun isEqual(a: Any?, b: Any?): Boolean = a?.equals(b) ?: b == null

    private fun evaluate(expr: Expr): Any? = expr.accept(this)

    private fun safeCastNumberOperand(
        operator: Token,
        operand: Any?
    ): Double = when (operand) {
        is Double -> operand
        else -> throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun safePerformNumberOperation(
        operator: Token,
        leftOperand: Any?,
        rightOperand: Any?,
        operation: (Double, Double) -> Any?
    ) = when (leftOperand) {
        is Double -> when (rightOperand) {
            is Double -> operation(leftOperand, rightOperand)
            else -> throw RuntimeError(operator, "Right operand must be a number.")
        }
        else -> throw RuntimeError(operator, "Left operand must be a number.")
    }

    private fun stringify(obj: Any?): String = when (obj) {
        null -> "nil"
        is Double -> obj.toString().let { text ->
            if (text.endsWith(".0")) {
                text.substring(0, text.length - 2)
            } else text
        }
        else -> obj.toString()
    }
}

private fun Double.greaterThan(b: Double) = this > b
private fun Double.lessThan(b: Double) = this < b
private fun Double.greaterThanOrEqual(b: Double) = this >= b
private fun Double.lessThanOrEqual(b: Double) = this <= b

class RuntimeError(val token: Token, msg: String) : RuntimeException(msg) { }
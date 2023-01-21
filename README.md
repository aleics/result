# result

This library offers and experiments with a variant of the standard [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/) type for safer error handling, where the failure is strongly typed. Thus, it's easily understood by the compiler, which exception can be expected out of a given operation.

Inspired by the strongly typed [Result](https://doc.rust-lang.org/std/result/enum.Result.html) from the Rust programming language 🦀.

```kt
import me.aleics.result.Result

fun div(x: Double, y: Double): Result<Double, MathException> {
    ...
}

fun main() {
    val result = div(2.0, 3.0)

    when (result) {
        is Result.Success -> println("Division result is ${result.value}")
        is Result.Failure -> throw result.exception // I can't handle `MathException`, I give up
    }
}
```

Then, `Result` can be returned and be handled properly, without any unexpected exceptions being thrown. If a given failure shouldn't be handled, we can always throw such exception as a last resort.

`Result` can be chained together by using a handful of operators, which allow to handle successes and failures properly. It is also extremely helpful for libraries, which shouldn't have the responsibility to throw an exception, and therefore crashing the program that they are running in.

```kt
object Token
object Expression
object Execution

sealed class InterpreterException : Exception() {
    class Tokenization : InterpreterException()
    class ParsingException: InterpreterException()
    class ExecutionException : InterpreterException()
}

class Interpreter {
    fun run(input: String): Result<Execution, InterpreterException> =
        tokenize(input)
            .flatMap { parse(it) }
            .flatMap { execute(it) }

    private fun execute(expression: Expression): Result<Execution, InterpreterException> { ... }

    private fun parse(tokens: List<Token>): Result<Expression, InterpreterException> { ... }

    private fun tokenize(input: String): Result<List<Token>, InterpreterException> { ... }
}

fun main() {
    val input = "2 + 3"

    val interpreter = Interpreter()

    interpreter.run(input)
        .onSuccess { "Result: $it" }
        .onFailure { exception -> "Error while running interpreter: ${exception.message}"  } // InterpreterException
}
```

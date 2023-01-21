package me.aleics.result

/**
 * 
 */
sealed class Result<T, E : Throwable> {

    class Success<T, E : Throwable>(val value: T) : Result<T, E>() {
        override fun isSuccess(): Boolean = true

        override fun isFailure(): Boolean = false

        override fun success(): T = value

        override fun failure(): E? = null

        override fun <F : Throwable> or(other: Result<T, F>): Result<T, F> = Success(value)

        override fun <R> map(predicate: (T) -> R): Result<R, E> = Success(predicate(value))

        override fun <F : Throwable> mapError(predicate: (E) -> F): Result<T, F> = Success(value)

        override fun <R> mapOrElse(predicate: (T) -> R, fallback: (E) -> R): R = predicate(value)

        override fun <R> flatMap(predicate: (T) -> Result<R, E>): Result<R, E> = predicate(value)

        override fun throwOnFailure(): T = value

        override fun onFailure(handler: (E) -> Unit): Result<T, E> = this

        override fun onSuccess(handler: (T) -> Unit): Result<T, E> {
            handler(value)
            return this
        }

        override fun toString(): String = value.toString()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success<*, *>

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }
    }

    class Failure<T, E : Throwable>(val exception: E) : Result<T, E>() {
        override fun isSuccess(): Boolean = false

        override fun isFailure(): Boolean = true

        override fun success(): T? = null

        override fun failure(): E = exception

        override fun <F : Throwable> or(other: Result<T, F>): Result<T, F> = other

        override fun <R> map(predicate: (T) -> R): Result<R, E> = Failure(exception)

        override fun <F : Throwable> mapError(predicate: (E) -> F): Result<T, F> = Failure(predicate(exception))

        override fun <R> mapOrElse(predicate: (T) -> R, fallback: (E) -> R): R = fallback(exception)

        override fun <R> flatMap(predicate: (T) -> Result<R, E>): Result<R, E> = Failure(exception)

        override fun toString(): String = exception.toString()

        override fun throwOnFailure() = throw exception

        override fun onFailure(handler: (E) -> Unit): Result<T, E> {
            handler(exception)
            return this
        }

        override fun onSuccess(handler: (T) -> Unit): Result<T, E> = this

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Failure<*, *>

            if (exception != other.exception) return false

            return true
        }

        override fun hashCode(): Int {
            return exception.hashCode()
        }
    }

    abstract fun isSuccess(): Boolean

    abstract fun isFailure(): Boolean

    abstract fun success(): T?

    abstract fun failure(): E?

    abstract fun <F : Throwable> or(other: Result<T, F>): Result<T, F>

    abstract fun <R> map(predicate: (T) -> R): Result<R, E>

    abstract fun <F : Throwable> mapError(predicate: (E) -> F): Result<T, F>

    abstract fun <R> mapOrElse(predicate: (T) -> R, fallback: (E) -> R): R

    abstract fun <R> flatMap(predicate: (T) -> Result<R, E>): Result<R, E>

    abstract fun throwOnFailure(): T

    abstract fun onFailure(handler: (E) -> Unit): Result<T, E>

    abstract fun onSuccess(handler: (T) -> Unit): Result<T, E>
}

public fun <T, E: Exception> Result<Result<T, E>, E>.flatten(): Result<T, E> = when(this) {
    is Result.Failure -> Result.Failure(exception)
    is Result.Success -> value
}

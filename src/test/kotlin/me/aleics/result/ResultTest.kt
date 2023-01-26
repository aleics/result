package me.aleics.result

import kotlin.NumberFormatException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ResultTest {

    @Test
    fun isSuccess() {
        val success = Result.Success<String, Exception>("hello!")
        val failure = Result.Failure<String, Exception>(Exception("ups"))

        assertEquals(success.isSuccess(), true)
        assertEquals(failure.isSuccess(), false)
    }

    @Test
    fun isFailure() {
        val success = Result.Success<String, Exception>("hello!")
        val failure = Result.Failure<String, Exception>(Exception("ups"))

        assertEquals(success.isFailure(), false)
        assertEquals(failure.isFailure(), true)
    }

    @Test
    fun success() {
        val success = Result.Success<String, Exception>("hello!")
        val failure = Result.Failure<String, Exception>(Exception("ups"))

        assertEquals(success.success(), "hello!")
        assertEquals(failure.success(), null)
    }

    @Test
    fun failure() {
        val success = Result.Success<String, Exception>("hello!")
        val exception = Exception("ups")
        val failure = Result.Failure<String, Exception>(exception)

        assertEquals(success.failure(), null)
        assertEquals(failure.failure(), exception)
    }

    @Test
    fun or() {
        val success = Result.Success<String, Exception>("hello!")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))
        val anotherFailure = Result.Failure<String, Exception>(Exception("Something else went wrong"))

        assertEquals(success.or(anotherFailure), success)
        assertEquals(failure.or(anotherFailure), anotherFailure)
    }

    @Test
    fun map() {
        val success = Result.Success<String, Exception>("hello")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))

        val mappedSuccess = success.map { "$it world!" }
        val mappedFailure = failure.map { "$it world!" }

        assertEquals(mappedSuccess, Result.Success("hello world!"))
        assertEquals(mappedFailure, failure)
    }

    @Test
    fun mapError() {
        val success = Result.Success<String, Exception>("hello")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))

        val newException = NumberFormatException()

        val mappedSuccess = success.mapError { newException }
        val mappedFailure = failure.mapError { newException }

        assertEquals(mappedSuccess, Result.Success("hello"))
        assertEquals(mappedFailure, Result.Failure(newException))
    }

    @Test
    fun mapOrElse() {
        val success = Result.Success<String, Exception>("hello")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))

        val mappedSuccess = success.mapOrElse({ "$it world!"}, { "default" })
        val mappedFailure = failure.mapOrElse({ "$it world!"}, { "default" })

        assertEquals(mappedSuccess, "hello world!")
        assertEquals(mappedFailure, "default")
    }

    @Test
    fun flatMap() {
        val success = Result.Success<String, Exception>("hello")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))

        val mappedSuccess = success.flatMap { Result.Success("$it world!") }
        val mappedFailure = failure.flatMap { Result.Success("$it world!") }

        assertEquals(mappedSuccess, Result.Success("hello world!"))
        assertEquals(mappedFailure, failure)
    }

    @Test
    fun getOrElse() {
        val success = Result.Success<String, Exception>("hello")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))

        val mappedSuccess = success.getOrElse { "SHOULD NOT HAPPEN!" }
        val mappedFailure = failure.getOrElse { "hello" }

        assertEquals(mappedSuccess, "hello")
        assertEquals(mappedFailure, "hello")
    }

    @Test
    fun throwOnFailure() {
        val success = Result.Success<String, Exception>("hello")
        val failure = Result.Failure<String, Exception>(Exception("Something went wrong"))

        assertEquals(success.throwOnFailure(), "hello")
        assertFails { failure.throwOnFailure() }
    }

    @Test
    fun flatten() {
        val exception = Exception("Something went wrong")
        val success = Result.Success<Result<String, Exception>, Exception>(Result.Success("hello"))
        val failureInSuccess = Result.Success<Result<String, Exception>, Exception>(Result.Failure(exception))
        val failure = Result.Failure<Result<String, Exception>, Exception>(exception)

        assertEquals(success.flatten(), Result.Success("hello"))
        assertEquals(failureInSuccess.flatten(), Result.Failure(exception))
        assertEquals(failure.flatten(), Result.Failure(exception))
    }
}

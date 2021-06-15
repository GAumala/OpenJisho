package com.gaumala.openjisho.utils.error

sealed class Either<T, U> {
    class Left<T, U>(val value: T): Either<T, U>()
    class Right<T, U>(val value: U): Either<T, U>()

    fun <V> map(transform: (U) -> V): Either<T, V> {
        return when (this) {
            is Left ->
                Left(value)
            is Right ->
                Right(transform(value))
        }
    }
    fun <V> mapLeft(transform: (T) -> V): Either<V, U> {
        return when (this) {
            is Left ->
                Left(transform(value))
            is Right ->
                Right(value)
        }
    }
}
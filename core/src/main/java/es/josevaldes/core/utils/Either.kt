package es.josevaldes.core.utils

sealed class Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>()
    data class Right<out R>(val value: R) : Either<Nothing, R>()

    fun isLeft() = this is Left<L>
    fun isRight() = this is Right<R>
}

fun <L, R, T> Either<L, R>.fold(
    onLeft: (L) -> T,
    onRight: (R) -> T
): T = when (this) {
    is Either.Left -> onLeft(value)
    is Either.Right -> onRight(value)
}
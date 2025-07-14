package com.qapital.stocks.core.ui

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    
    data class Success<T>(
        val data: T,
        val metadata: Map<String, Any> = emptyMap()
    ) : UiState<T>()
    
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : UiState<Nothing>()
    
    data class Empty(
        val message: String? = null
    ) : UiState<Nothing>()
}

fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error
fun <T> UiState<T>.isEmpty(): Boolean = this is UiState.Empty

fun <T> UiState<T>.dataOrNull(): T? = when (this) {
    is UiState.Success -> data
    else -> null
}

fun <T> UiState<T>.errorOrNull(): String? = when (this) {
    is UiState.Error -> message
    else -> null
}

inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data), metadata)
    is UiState.Loading -> UiState.Loading
    is UiState.Error -> this
    is UiState.Empty -> this
} 
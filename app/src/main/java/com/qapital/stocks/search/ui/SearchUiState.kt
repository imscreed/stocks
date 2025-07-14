package com.qapital.stocks.search.ui

import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.core.ui.UiState

data class SearchData(
    val stocks: List<Stock>,
    val query: String = ""
)

typealias SearchUiState = UiState<SearchData>

fun SearchUiState.getStocks(): List<Stock> = when (this) {
    is UiState.Success -> data.stocks
    else -> emptyList()
}

fun SearchUiState.getQuery(): String = when (this) {
    is UiState.Success -> data.query
    else -> ""
}

object SearchUiStateFactory {
    fun loading(): SearchUiState = UiState.Loading
    
    fun success(stocks: List<Stock>, query: String = ""): SearchUiState = 
        UiState.Success(
            data = SearchData(stocks, query),
            metadata = mapOf(
                "query" to query,
                "resultCount" to stocks.size,
                "timestamp" to System.currentTimeMillis()
            )
        )
    
    fun error(message: String, cause: Throwable? = null): SearchUiState = 
        UiState.Error(message, cause)
    
    fun empty(message: String? = null): SearchUiState = 
        UiState.Empty(message)
} 
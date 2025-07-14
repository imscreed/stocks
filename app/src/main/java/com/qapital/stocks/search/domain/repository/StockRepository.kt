package com.qapital.stocks.search.domain.repository

import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.data.model.Stock

interface StockRepository {
    suspend fun getStocks(): Result<List<Stock>>
    suspend fun searchStocks(query: String): List<Stock>
    suspend fun hasFreshCache(): Boolean
} 
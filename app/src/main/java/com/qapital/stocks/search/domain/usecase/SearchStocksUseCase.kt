package com.qapital.stocks.search.domain.usecase

import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.search.domain.repository.StockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchStocksUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    suspend operator fun invoke(query: String): Result<List<Stock>> {
        return try {
            val hasFreshCache = stockRepository.hasFreshCache()
            
            if (hasFreshCache) {
                val searchResults = stockRepository.searchStocks(query)
                Result.Success(searchResults)
            } else {
                // Cache-first strategy: fetch from network then search locally
            val stocksResult = stockRepository.getStocks()
            
            when (stocksResult) {
                is Result.Success -> {
                    val searchResults = stockRepository.searchStocks(query)
                    Result.Success(searchResults)
                }
                is Result.Error -> stocksResult
                is Result.Loading -> Result.Loading
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 
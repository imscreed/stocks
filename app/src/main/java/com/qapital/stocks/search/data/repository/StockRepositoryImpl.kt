package com.qapital.stocks.search.data.repository

import android.content.Context
import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.core.data.model.toDomain
import com.qapital.stocks.core.database.DatabaseProvider
import com.qapital.stocks.core.database.toDomainModel
import com.qapital.stocks.core.network.StockApi
import com.qapital.stocks.core.util.Constants
import com.qapital.stocks.core.util.toUserFriendlyMessage
import com.qapital.stocks.search.domain.repository.StockRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val stockApi: StockApi,
    private val databaseProvider: DatabaseProvider,
    @ApplicationContext private val context: Context
) : StockRepository {
    
    private val database get() = databaseProvider.database
    
    override suspend fun getStocks(): Result<List<Stock>> = withContext(Dispatchers.IO) {
        try {
            val remoteStockDtos = stockApi.getStocks()
            val remoteStocks = remoteStockDtos.map { it.toDomain() }
            cacheStocksInBatches(remoteStocks)
            Result.Success(remoteStocks)
        } catch (e: Exception) {
            val cachedStocks = getCachedStocks()
            if (cachedStocks.isNotEmpty()) {
                Result.Success(cachedStocks)
            } else {
                Result.Error(Exception(e.toUserFriendlyMessage(context)))
            }
        }
    }
    
    override suspend fun searchStocks(query: String): List<Stock> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            getCachedStocks()
        } else {
            database.stockQueriesQueries.searchStocks(query)
                .executeAsList()
                .map { dbStock -> dbStock.toDomainModel() }
        }
    }
    
    override suspend fun hasFreshCache(): Boolean = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val expiryTime = currentTime - Constants.CACHE_EXPIRY_DURATION
        
        val freshStockCount = database.stockQueriesQueries.getFreshStockCount(expiryTime).executeAsOne()
        freshStockCount > 0
    }
    
    private suspend fun cacheStocksInBatches(stocks: List<Stock>) {
        val currentTime = System.currentTimeMillis()
        val batchSize = 100
        
        try {
            database.transaction {
                database.stockQueriesQueries.deleteOldEntries(currentTime - Constants.CACHE_EXPIRY_DURATION)
                
                stocks.chunked(batchSize).forEach { batch ->
                    batch.forEach { stock ->
                        try {
                            database.stockQueriesQueries.insertStock(
                                symbol = stock.symbol,
                                name = stock.name,
                                price = stock.price,
                                cached_at = currentTime
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }
    
    private fun getCachedStocks(): List<Stock> {
        return database.stockQueriesQueries.getAllStocks()
            .executeAsList()
            .map { dbStock -> dbStock.toDomainModel() }
    }
} 
package com.qapital.stocks.search.data.repository

import android.content.Context
import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.core.data.model.StockDto
import com.qapital.stocks.core.database.Database
import com.qapital.stocks.core.database.DatabaseProvider
import com.qapital.stocks.core.database.StockQueriesQueries
import com.qapital.stocks.core.network.StockApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StockRepositoryImplTest {
    
    private val stockApi = mockk<StockApi>()
    private val databaseProvider = mockk<DatabaseProvider>()
    private val database = mockk<Database>(relaxed = true)
    private val stockQueriesQueries = mockk<StockQueriesQueries>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private lateinit var repository: StockRepositoryImpl
    
    private val testStocks = listOf(
        Stock("AAPL", "Apple Inc.", 150.0),
        Stock("GOOGL", "Alphabet Inc.", 2500.0),
        Stock("MSFT", "Microsoft Corporation", 300.0)
    )
    
    private val testStockDtos = listOf(
        StockDto("AAPL", "Apple Inc.", 150.0),
        StockDto("GOOGL", "Alphabet Inc.", 2500.0),
        StockDto("MSFT", "Microsoft Corporation", 300.0)
    )
    
    @Before
    fun setup() {
        every { databaseProvider.database } returns database
        every { database.stockQueriesQueries } returns stockQueriesQueries
        every { context.getString(any()) } returns "Something went wrong. Please try again"
        repository = StockRepositoryImpl(stockApi, databaseProvider, context)
    }
    
    @Test
    fun `when api call succeeds, should return success`() = runTest {
        coEvery { stockApi.getStocks() } returns testStockDtos
        
        val result = repository.getStocks()
        
        assertTrue(result is Result.Success)
        val stocks = (result as Result.Success).data
        assertEquals(3, stocks.size)
        assertEquals("AAPL", stocks[0].symbol)
        assertEquals("Apple Inc.", stocks[0].name)
        assertEquals(150.0, stocks[0].price, 0.01)
    }
    
    @Test
    fun `when api call fails and cache is empty, should return error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { stockApi.getStocks() } throws exception
        every { stockQueriesQueries.getAllStocks() } returns mockk {
            every { executeAsList() } returns emptyList<com.qapital.stocks.core.database.Stock>()
        }
        
        val result = repository.getStocks()
        
        assertTrue("Expected Result.Error but got $result", result is Result.Error)
        assertTrue((result as Result.Error).exception.message?.contains("Something went wrong") == true)
    }
    
    @Test
    fun `when searching with empty query, should return all cached stocks`() = runTest {
        val cachedStocks = listOf(
            com.qapital.stocks.core.database.Stock("AAPL", "Apple Inc.", 150.0, System.currentTimeMillis())
        )
        every { stockQueriesQueries.getAllStocks() } returns mockk {
            every { executeAsList() } returns cachedStocks
        }
        
        val result = repository.searchStocks("")
        
        assertEquals(1, result.size)
        assertEquals("AAPL", result[0].symbol)
    }
    
    @Test
    fun `when searching with query, should return filtered stocks`() = runTest {
        val cachedStocks = listOf(
            com.qapital.stocks.core.database.Stock("AAPL", "Apple Inc.", 150.0, System.currentTimeMillis())
        )
        every { stockQueriesQueries.searchStocks("AAPL") } returns mockk {
            every { executeAsList() } returns cachedStocks
        }
        
        val result = repository.searchStocks("AAPL")
        
        assertEquals(1, result.size)
        assertEquals("AAPL", result[0].symbol)
    }
} 
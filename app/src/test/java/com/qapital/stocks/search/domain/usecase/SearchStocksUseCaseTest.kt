package com.qapital.stocks.search.domain.usecase

import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.search.domain.repository.StockRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchStocksUseCaseTest {
    
    private val stockRepository = mockk<StockRepository>()
    private lateinit var searchStocksUseCase: SearchStocksUseCase
    
    private val testStocks = listOf(
        Stock("AAPL", "Apple Inc.", 150.0),
        Stock("GOOGL", "Alphabet Inc.", 2500.0),
        Stock("MSFT", "Microsoft Corporation", 300.0)
    )
    
    @Before
    fun setup() {
        searchStocksUseCase = SearchStocksUseCase(stockRepository)
    }
    
    @Test
    fun `when cache is available, should return search results without network call`() = runTest {
        val query = "AAPL"
        val searchResults = listOf(testStocks[0])
        coEvery { stockRepository.hasFreshCache() } returns true
        coEvery { stockRepository.searchStocks(query) } returns searchResults
        
        val result = searchStocksUseCase(query)
        
        assertTrue(result is Result.Success)
        assertEquals(searchResults, (result as Result.Success).data)
        coVerify { stockRepository.hasFreshCache() }
        coVerify { stockRepository.searchStocks(query) }
        coVerify(exactly = 0) { stockRepository.getStocks() }
    }
    
    @Test
    fun `when no cache and repository returns error, should return error`() = runTest {
        val errorMessage = "Network error"
        coEvery { stockRepository.hasFreshCache() } returns false
        coEvery { stockRepository.getStocks() } returns Result.Error(Exception(errorMessage))
        
        val result = searchStocksUseCase("AAPL")
        
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).exception.message)
    }
    
    @Test
    fun `when query is empty and cache available, should get all cached stocks`() = runTest {
        coEvery { stockRepository.hasFreshCache() } returns true
        coEvery { stockRepository.searchStocks("") } returns testStocks
        
        val result = searchStocksUseCase("")
        
        assertTrue(result is Result.Success)
        assertEquals(testStocks, (result as Result.Success).data)
        coVerify(exactly = 0) { stockRepository.getStocks() }
    }
    
    @Test
    fun `when exception occurs, should return error`() = runTest {
        val exception = RuntimeException("Unexpected error")
        coEvery { stockRepository.hasFreshCache() } throws exception
        
        val result = searchStocksUseCase("AAPL")
        
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `when no cache available, should fetch from network then search`() = runTest {
        val query = "AAPL"
        val searchResults = listOf(testStocks[0])
        coEvery { stockRepository.hasFreshCache() } returns false
        coEvery { stockRepository.getStocks() } returns Result.Success(testStocks)
        coEvery { stockRepository.searchStocks(query) } returns searchResults
        
        val result = searchStocksUseCase(query)
        
        assertTrue(result is Result.Success)
        assertEquals(searchResults, (result as Result.Success).data)
        coVerify { stockRepository.hasFreshCache() }
        coVerify { stockRepository.getStocks() }
        coVerify { stockRepository.searchStocks(query) }
    }
} 
package com.qapital.stocks.search.ui

import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.core.ui.UiState
import com.qapital.stocks.core.util.NetworkConnectivityMonitor
import com.qapital.stocks.search.domain.usecase.SearchStocksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    
    private val searchStocksUseCase = mockk<SearchStocksUseCase>()
    private val networkConnectivityMonitor = mockk<NetworkConnectivityMonitor>()
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    private val testStocks = listOf(
        Stock("AAPL", "Apple Inc.", 150.0),
        Stock("GOOGL", "Alphabet Inc.", 2500.0),
        Stock("MSFT", "Microsoft Corporation", 300.0)
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { searchStocksUseCase(any()) } returns Result.Success(testStocks)
        coEvery { networkConnectivityMonitor.isConnected } returns MutableStateFlow(true)
    }
    
    @Test
    fun `initial state should be loading`() = runTest {
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        val initialState = viewModel.uiState.first()
        assertTrue(initialState is UiState.Loading)
    }
    
    @Test
    fun `when search query changes, should update search query state`() = runTest {
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        assertEquals("AAPL", viewModel.searchQuery.first())
    }
    
    @Test
    fun `when search succeeds, should update ui state to success`() = runTest {
        coEvery { searchStocksUseCase("AAPL") } returns Result.Success(listOf(testStocks[0]))
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Success)
        assertEquals(1, (uiState as UiState.Success).data.stocks.size)
        assertEquals("AAPL", uiState.data.stocks[0].symbol)
    }
    
    @Test
    fun `when search returns empty results, should update ui state to empty`() = runTest {
        coEvery { searchStocksUseCase("INVALID") } returns Result.Success(emptyList())
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("INVALID")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Empty)
    }
    
    @Test
    fun `when search fails, should update ui state to error`() = runTest {
        val errorMessage = "Network error"
        coEvery { searchStocksUseCase("AAPL") } returns Result.Error(Exception(errorMessage))
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Error)
        assertEquals(errorMessage, (uiState as UiState.Error).message)
    }
    
    @Test
    fun `when retry is clicked, should perform search again`() = runTest {
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        viewModel.onRetryClicked()
        advanceUntilIdle()
        
        coVerify(exactly = 3) { searchStocksUseCase(any()) }
    }
    
    @Test
    fun `search should be debounced`() = runTest {
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("A")
        viewModel.onSearchQueryChanged("AA")
        viewModel.onSearchQueryChanged("AAP")
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        coVerify(exactly = 2) { searchStocksUseCase(any()) }
    }
    
    @Test
    fun `when empty query is searched, should handle gracefully`() = runTest {
        coEvery { searchStocksUseCase("") } returns Result.Success(testStocks)
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Success)
        assertEquals(3, (uiState as UiState.Success).data.stocks.size)
    }
    
    @Test
    fun `when whitespace-only query is searched, should handle gracefully`() = runTest {
        coEvery { searchStocksUseCase("   ") } returns Result.Success(emptyList())
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("   ")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Empty)
    }
    
    @Test
    fun `when very long query is searched, should handle gracefully`() = runTest {
        val longQuery = "A".repeat(1000)
        coEvery { searchStocksUseCase(longQuery) } returns Result.Success(emptyList())
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged(longQuery)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Empty)
    }
    
    @Test
    fun `when special characters in query, should handle gracefully`() = runTest {
        val specialQuery = "!@#$%^&*()[]{}|\\:;\"'<>,.?/"
        coEvery { searchStocksUseCase(specialQuery) } returns Result.Success(emptyList())
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged(specialQuery)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Empty)
    }
    
    @Test
    fun `when unicode characters in query, should handle gracefully`() = runTest {
        val unicodeQuery = "🚀📈💰AAPL测试"
        coEvery { searchStocksUseCase(unicodeQuery) } returns Result.Success(emptyList())
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged(unicodeQuery)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Empty)
    }
    
    @Test
    fun `when multiple rapid search queries, should cancel previous searches`() = runTest {
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        // Rapid fire queries
        repeat(10) { index ->
            viewModel.onSearchQueryChanged("query$index")
        }
        
        advanceUntilIdle()
        
        // Should not call search for every rapid query due to debouncing
        coVerify(atMost = 3) { searchStocksUseCase(any()) }
    }
    
    @Test
    fun `when use case throws runtime exception, should handle gracefully`() = runTest {
        coEvery { searchStocksUseCase(any()) } throws RuntimeException("Unexpected error")
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Error)
    }
    
    @Test
    fun `when null exception message, should provide fallback error message`() = runTest {
        coEvery { searchStocksUseCase("AAPL") } returns Result.Error(Exception(null as String?))
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("AAPL")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Error)
        assertEquals("Something went wrong", (uiState as UiState.Error).message)
    }
    
    @Test
    fun `when search returns extremely large result set, should handle gracefully`() = runTest {
        val largeResultSet = (1..10000).map { index ->
            Stock("SYM$index", "Company $index", index.toDouble())
        }
        coEvery { searchStocksUseCase("") } returns Result.Success(largeResultSet)
        viewModel = SearchViewModel(searchStocksUseCase, networkConnectivityMonitor)
        
        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is UiState.Success)
        assertEquals(10000, (uiState as UiState.Success).data.stocks.size)
    }
} 
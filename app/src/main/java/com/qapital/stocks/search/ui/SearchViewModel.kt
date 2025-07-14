package com.qapital.stocks.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qapital.stocks.core.data.Result
import com.qapital.stocks.core.util.Constants
import com.qapital.stocks.core.util.NetworkConnectivityMonitor
import com.qapital.stocks.search.domain.usecase.SearchStocksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchStocksUseCase: SearchStocksUseCase,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiStateFactory.loading())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val isConnected: StateFlow<Boolean> = networkConnectivityMonitor.isConnected
    
    init {
        observeSearchQuery()
        loadInitialData()
    }
    
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(Constants.SEARCH_DEBOUNCE_DELAY)
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            performSearch("")
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun onRetryClicked() {
        performSearch(_searchQuery.value)
    }
    
    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiStateFactory.loading()
            
            try {
                when (val result = searchStocksUseCase(query)) {
                    is Result.Success -> {
                        _uiState.value = if (result.data.isEmpty()) {
                            SearchUiStateFactory.empty()
                        } else {
                            SearchUiStateFactory.success(result.data, query)
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = SearchUiStateFactory.error(
                            message = result.exception.message ?: "Something went wrong",
                            cause = result.exception
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = SearchUiStateFactory.loading()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiStateFactory.error("Something went wrong", e)
            }
        }
    }
} 
package com.qapital.stocks.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qapital.stocks.core.ui.EmptyState
import com.qapital.stocks.core.ui.ErrorState
import com.qapital.stocks.core.ui.LoadingIndicator
import com.qapital.stocks.core.ui.NetworkWarningBanner
import com.qapital.stocks.core.ui.UiState
import com.qapital.stocks.search.ui.components.SearchBar
import com.qapital.stocks.search.ui.components.StockItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState(initial = true)
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Qapital stocks",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
                .semantics {
                    contentDescription = "Stock search screen"
                }
        ) {
            if (!isConnected) {
                NetworkWarningBanner()
            }
            
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            when (val currentState = uiState) {
                is UiState.Loading -> {
                    LoadingIndicator()
                }
                is UiState.Success -> {
                    val searchData = currentState.data
                    val resultsAnnouncement = if (searchData.query.isBlank()) {
                        "Showing ${searchData.stocks.size} stocks"
                    } else {
                        "Found ${searchData.stocks.size} results for ${searchData.query}"
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics {
                                contentDescription = resultsAnnouncement
                                liveRegion = LiveRegionMode.Polite
                            }
                    ) {
                        items(
                            items = searchData.stocks,
                            key = { stock -> stock.symbol }
                        ) { stock ->
                            StockItem(stock = stock)
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorState(
                        message = currentState.message,
                        onRetryClick = viewModel::onRetryClicked
                    )
                }
                is UiState.Empty -> {
                    EmptyState()
                }
            }
        }
    }
} 
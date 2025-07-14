package com.qapital.stocks.search.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.core.ui.NetworkWarningBanner
import com.qapital.stocks.core.ui.PreviewAppScaffold
import com.qapital.stocks.ui.theme.StocksTheme

/**
 * Compose Previews for complex UI states in the Qapital Stocks app.
 * These serve as visual documentation and can be viewed in Android Studio's preview pane.
 * Individual component previews are located in their respective component files.
 */

@Preview(name = "Success State - Stock List", showBackground = true)
@Composable
fun PreviewSuccessState() {
    StocksTheme {
        PreviewAppScaffold {
            SearchBar(query = "", onQueryChange = {})
            PreviewStockList()
        }
    }
}

@Preview(name = "Search Results", showBackground = true)
@Composable
fun PreviewSearchResults() {
    StocksTheme {
        PreviewAppScaffold {
            SearchBar(query = "AAPL", onQueryChange = {})
            PreviewFilteredStockList()
        }
    }
}

@Preview(name = "Offline + Stock List", showBackground = true)
@Composable
fun PreviewOfflineWithStocks() {
    StocksTheme {
        PreviewAppScaffold {
            NetworkWarningBanner()
            SearchBar(query = "", onQueryChange = {})
            PreviewStockList()
        }
    }
}

@Composable
private fun PreviewStockList() {
    Column {
        val stocks = listOf(
            Stock(symbol = "AAPL", name = "Apple Inc.", price = 150.25),
            Stock(symbol = "GOOGL", name = "Alphabet Inc.", price = 2750.80),
            Stock(symbol = "MSFT", name = "Microsoft Corporation", price = 305.50),
            Stock(symbol = "AMZN", name = "Amazon.com Inc.", price = 3380.00),
            Stock(symbol = "TSLA", name = "Tesla Inc.", price = 850.75)
        )
        
        stocks.forEach { stock ->
            StockItem(stock = stock)
        }
    }
}

@Composable
private fun PreviewFilteredStockList() {
    Column {
        StockItem(
            stock = Stock(symbol = "AAPL", name = "Apple Inc.", price = 150.25)
        )
    }
} 
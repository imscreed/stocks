package com.qapital.stocks.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qapital.stocks.R
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.ui.theme.StocksTheme

@Composable
fun StockItem(
    stock: Stock,
    modifier: Modifier = Modifier
) {
    val priceText = stringResource(R.string.price_format, stock.price)
    val stockContentDescription = "Stock ${stock.symbol}, ${stock.name}, current price $priceText"
    val symbolContentDescription = "Stock symbol: ${stock.symbol}"
    val nameContentDescription = "Company name: ${stock.name}"
    val priceContentDescription = "Current price: $priceText"
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .semantics {
                contentDescription = stockContentDescription
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    modifier = Modifier.semantics {
                        contentDescription = symbolContentDescription
                    }
                )
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.semantics {
                        contentDescription = nameContentDescription
                    }
                )
            }
            
            Text(
                text = priceText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(percent = 50)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .semantics {
                        contentDescription = priceContentDescription
                    }
            )
        }
    }
} 

@Preview(name = "Stock Item - Short Name", showBackground = true)
@Composable
fun PreviewStockItemShort() {
    StocksTheme {
        StockItem(
            stock = Stock(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = 150.25
            )
        )
    }
}

@Preview(name = "Stock Item - Long Name", showBackground = true)
@Composable
fun PreviewStockItemLong() {
    StocksTheme {
        StockItem(
            stock = Stock(
                symbol = "BERKB",
                name = "Berkshire Hathaway Inc. Class B",
                price = 285.50
            )
        )
    }
}

@Preview(name = "Stock Item - High Price", showBackground = true)
@Composable
fun PreviewStockItemHighPrice() {
    StocksTheme {
        StockItem(
            stock = Stock(
                symbol = "BRK.A",
                name = "Berkshire Hathaway Inc. Class A",
                price = 445875.00
            )
        )
    }
}

@Preview(name = "Stock Item - Low Price", showBackground = true)
@Composable
fun PreviewStockItemLowPrice() {
    StocksTheme {
        StockItem(
            stock = Stock(
                symbol = "SIRI",
                name = "Sirius XM Holdings Inc.",
                price = 6.23
            )
        )
    }
}

@Preview(name = "Multiple Stock Items", showBackground = true)
@Composable
fun PreviewMultipleStockItems() {
    StocksTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            StockItem(
                stock = Stock(symbol = "AAPL", name = "Apple Inc.", price = 150.25)
            )
            StockItem(
                stock = Stock(symbol = "GOOGL", name = "Alphabet Inc.", price = 2750.80)
            )
            StockItem(
                stock = Stock(symbol = "MSFT", name = "Microsoft Corporation", price = 305.50)
            )
        }
    }
} 
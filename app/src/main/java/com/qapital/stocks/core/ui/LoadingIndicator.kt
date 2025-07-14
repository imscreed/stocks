package com.qapital.stocks.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qapital.stocks.R
import com.qapital.stocks.core.ui.PreviewAppScaffold
import com.qapital.stocks.search.ui.components.SearchBar
import com.qapital.stocks.ui.theme.StocksTheme

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.loading_stocks),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Loading Indicator", showBackground = true)
@Composable
fun PreviewLoadingIndicator() {
    StocksTheme {
        LoadingIndicator()
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun PreviewLoadingState() {
    StocksTheme {
        PreviewAppScaffold {
            SearchBar(query = "", onQueryChange = {})
            LoadingIndicator()
        }
    }
} 
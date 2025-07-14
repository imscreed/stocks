package com.qapital.stocks.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qapital.stocks.R
import com.qapital.stocks.core.ui.LoadingIndicator
import com.qapital.stocks.core.ui.PreviewAppScaffold
import com.qapital.stocks.search.ui.components.SearchBar
import com.qapital.stocks.ui.theme.StocksTheme
import com.qapital.stocks.ui.theme.WarningBackground
import com.qapital.stocks.ui.theme.WarningText

@Composable
fun NetworkWarningBanner(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(WarningBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = stringResource(R.string.cd_warning),
            modifier = Modifier.size(16.dp),
            tint = WarningText
        )
        
        Text(
            text = stringResource(R.string.network_warning_message),
            style = MaterialTheme.typography.bodySmall,
            color = WarningText,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
} 

@Preview(name = "Network Warning Banner", showBackground = true)
@Composable
fun PreviewNetworkWarningBanner() {
    StocksTheme {
        NetworkWarningBanner()
    }
} 

@Preview(name = "No Internet Warning", showBackground = true)
@Composable
fun PreviewNoInternetWarning() {
    StocksTheme {
        PreviewAppScaffold {
            NetworkWarningBanner()
            SearchBar(query = "", onQueryChange = {})
            LoadingIndicator()
        }
    }
} 
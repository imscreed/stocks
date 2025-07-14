package com.qapital.stocks.core.data.model

import com.google.gson.annotations.SerializedName

data class StockDto(
    @SerializedName("ticker")
    val ticker: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("currentPrice")
    val currentPrice: Double
)

data class Stock(
    val symbol: String,
    val name: String,
    val price: Double
)

fun StockDto.toDomain(): Stock {
    return Stock(
        symbol = ticker,
        name = name,
        price = currentPrice
    )
} 
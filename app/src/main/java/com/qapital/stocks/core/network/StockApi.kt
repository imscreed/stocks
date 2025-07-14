package com.qapital.stocks.core.network

import com.qapital.stocks.core.data.model.StockDto
import retrofit2.http.GET

interface StockApi {
    @GET("mock-stocks.json")
    suspend fun getStocks(): List<StockDto>
} 
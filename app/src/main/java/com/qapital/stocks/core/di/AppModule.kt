package com.qapital.stocks.core.di

import android.content.Context
import com.qapital.stocks.core.database.DatabaseProvider
import com.qapital.stocks.core.network.StockApi
import com.qapital.stocks.core.util.NetworkConnectivityMonitor
import com.qapital.stocks.search.data.repository.StockRepositoryImpl
import com.qapital.stocks.search.domain.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabaseProvider(@ApplicationContext context: Context): DatabaseProvider =
        DatabaseProvider(context)
    
    @Provides
    @Singleton
    fun provideStockRepository(
        stockApi: StockApi,
        databaseProvider: DatabaseProvider,
        @ApplicationContext context: Context
    ): StockRepository = StockRepositoryImpl(stockApi, databaseProvider, context)
    
    @Provides
    @Singleton
    fun provideNetworkConnectivityMonitor(
        @ApplicationContext context: Context
    ): NetworkConnectivityMonitor = NetworkConnectivityMonitor(context)
} 
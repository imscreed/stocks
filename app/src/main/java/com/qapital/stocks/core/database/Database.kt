package com.qapital.stocks.core.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.qapital.stocks.core.data.model.Stock
import com.qapital.stocks.core.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseProvider @Inject constructor(
    private val context: Context
) {
    @Volatile
    private var _database: Database? = null
    
    val database: Database
        get() = _database ?: synchronized(this) {
            _database ?: createDatabase().also { _database = it }
        }
    
    private fun createDatabase(): Database {
        return Database(
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = context,
                name = Constants.DATABASE_NAME
            )
        )
    }
}

fun com.qapital.stocks.core.database.Stock.toDomainModel(): Stock {
    return Stock(
        symbol = symbol,
        name = name,
        price = price
    )
}

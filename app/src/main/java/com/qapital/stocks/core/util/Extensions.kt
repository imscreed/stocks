package com.qapital.stocks.core.util

import android.content.Context
import com.qapital.stocks.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserFriendlyMessage(context: Context): String = when (this) {
    is UnknownHostException -> context.getString(R.string.error_no_internet)
    is SocketTimeoutException -> context.getString(R.string.error_connection_timeout)
    is IOException -> context.getString(R.string.error_network_error)
    is HttpException -> when (code()) {
        in 400..499 -> context.getString(R.string.error_request_failed)
        in 500..599 -> context.getString(R.string.error_server_error)
        else -> context.getString(R.string.error_something_wrong)
    }
    else -> context.getString(R.string.error_something_wrong)
}

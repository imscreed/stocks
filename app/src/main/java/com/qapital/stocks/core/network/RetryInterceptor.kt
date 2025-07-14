package com.qapital.stocks.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMillis: Long = 300L,
    private val backoffFactor: Double = 2.0
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var delayMillis = initialDelayMillis
        var lastException: IOException? = null

        while (attempt < maxRetries) {
            try {
                val request = chain.request()
                val response = chain.proceed(request)

                if (response.isSuccessful) {
                    return response
                } else if (response.code in listOf(500, 502, 503, 504)) {
                    response.close()
                    if (attempt < maxRetries - 1) {
                        Thread.sleep(delayMillis)
                        delayMillis = (delayMillis * backoffFactor).toLong()
                    }
                } else {
                    return response
                }

            } catch (e: IOException) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    Thread.sleep(delayMillis)
                    delayMillis = (delayMillis * backoffFactor).toLong()
                }
            }

            attempt++
        }

        throw lastException ?: IOException("RetryInterceptor failed after $maxRetries attempts")
    }
} 
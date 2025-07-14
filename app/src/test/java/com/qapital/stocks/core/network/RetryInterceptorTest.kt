package com.qapital.stocks.core.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException
import java.net.SocketTimeoutException

class RetryInterceptorTest {
    
    private val retryInterceptor = RetryInterceptor(maxRetries = 3, initialDelayMillis = 10L)
    
    @Test
    fun `should return successful response immediately`() {
        val mockChain = mockk<Interceptor.Chain>()
        val mockRequest = mockk<Request>()
        val successResponse = mockk<Response>()
        
        every { mockChain.request() } returns mockRequest
        every { successResponse.isSuccessful } returns true
        every { mockChain.proceed(mockRequest) } returns successResponse
        
        val result = retryInterceptor.intercept(mockChain)
        
        assertEquals(successResponse, result)
        verify(exactly = 1) { mockChain.proceed(mockRequest) }
    }
    
    @Test
    fun `should retry on server errors and eventually succeed`() {
        val mockChain = mockk<Interceptor.Chain>()
        val mockRequest = mockk<Request>()
        val serverErrorResponse = mockk<Response>()
        val successResponse = mockk<Response>()
        
        every { mockChain.request() } returns mockRequest
        every { serverErrorResponse.isSuccessful } returns false
        every { serverErrorResponse.code } returns 503
        every { serverErrorResponse.close() } returns Unit
        every { successResponse.isSuccessful } returns true
        
        every { mockChain.proceed(mockRequest) } returnsMany listOf(serverErrorResponse, successResponse)
        
        val result = retryInterceptor.intercept(mockChain)
        
        assertEquals(successResponse, result)
        verify(exactly = 2) { mockChain.proceed(mockRequest) }
    }
    
    @Test
    fun `should not retry on client errors`() {
        val mockChain = mockk<Interceptor.Chain>()
        val mockRequest = mockk<Request>()
        val clientErrorResponse = mockk<Response>()
        
        every { mockChain.request() } returns mockRequest
        every { clientErrorResponse.isSuccessful } returns false
        every { clientErrorResponse.code } returns 404
        every { mockChain.proceed(mockRequest) } returns clientErrorResponse
        
        val result = retryInterceptor.intercept(mockChain)
        
        assertEquals(clientErrorResponse, result)
        verify(exactly = 1) { mockChain.proceed(mockRequest) }
    }
    
    @Test
    fun `should retry on IOException and eventually fail`() {
        val mockChain = mockk<Interceptor.Chain>()
        val mockRequest = mockk<Request>()
        val networkError = IOException("Network error")
        
        every { mockChain.request() } returns mockRequest
        every { mockChain.proceed(mockRequest) } throws networkError
        
        try {
            retryInterceptor.intercept(mockChain)
            fail("Expected IOException to be thrown")
        } catch (e: IOException) {
            assertEquals("Network error", e.message)
        }
        
        verify(exactly = 3) { mockChain.proceed(mockRequest) }
    }
    
    @Test
    fun `should retry on network timeout and eventually succeed`() {
        val mockChain = mockk<Interceptor.Chain>()
        val mockRequest = mockk<Request>()
        val timeoutException = SocketTimeoutException("Connection timeout")
        val successResponse = mockk<Response>()
        
        every { mockChain.request() } returns mockRequest
        every { successResponse.isSuccessful } returns true
        
        every { mockChain.proceed(mockRequest) } throws timeoutException andThen successResponse
        
        val result = retryInterceptor.intercept(mockChain)
        
        assertEquals(successResponse, result)
        verify(exactly = 2) { mockChain.proceed(mockRequest) }
    }
} 
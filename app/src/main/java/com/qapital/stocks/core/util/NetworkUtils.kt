package com.qapital.stocks.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivityMonitor @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _isConnected = MutableStateFlow(getCurrentConnectivityState())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private var monitoringJob: kotlinx.coroutines.Job? = null
    
    init {
        startMonitoring()
    }
    
    private fun startMonitoring() {
        monitoringJob = scope.launch {
            try {
                while (isActive) {
                    _isConnected.value = getCurrentConnectivityState()
                    delay(1000)
                }
                         } catch (e: Exception) {
             }
        }
    }
    
    private fun getCurrentConnectivityState(): Boolean {
        return try {
            val activeNetwork = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } catch (e: Exception) {
            false
        }
    }
    
    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }
} 
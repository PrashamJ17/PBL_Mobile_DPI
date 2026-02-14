package com.example.dpi.ui

import android.app.Application
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dpi.capture.PacketCaptureManager
import com.example.dpi.database.DpiDatabase
import com.example.dpi.models.*
import com.example.dpi.service.DpiVpnService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for the app
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val database: DpiDatabase,
    private val captureManager: PacketCaptureManager
) : AndroidViewModel(application) {
    
    // Capture state
    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()
    
    // Statistics
    val statistics: StateFlow<CaptureStatistics> = captureManager.statistics
    
    // Recent packets
    val recentPackets: StateFlow<List<Packet>> = database.packetDao()
        .observeRecentPackets(1000)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Recent threats
    val recentThreats: StateFlow<List<ThreatAlert>> = database.threatDao()
        .observeRecentThreats(500)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Active flows
    val activeFlows: StateFlow<List<PacketFlow>> = database.flowDao()
        .observeActiveFlows()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Packet filter
    private val _packetFilter = MutableStateFlow(PacketFilter())
    val packetFilter: StateFlow<PacketFilter> = _packetFilter.asStateFlow()
    
    // Filtered packets
    val filteredPackets: StateFlow<List<Packet>> = combine(
        recentPackets,
        packetFilter
    ) { packets, filter ->
        packets.filter { filter.matches(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    /**
     * Prepare VPN (request permission if needed)
     */
    fun prepareVpn(): Intent? {
        return VpnService.prepare(getApplication())
    }
    
    /**
     * Start packet capture
     */
    fun startCapture() {
        val intent = Intent(getApplication(), DpiVpnService::class.java).apply {
            action = DpiVpnService.ACTION_START
        }
        getApplication<Application>().startForegroundService(intent)
        _isCapturing.value = true
    }
    
    /**
     * Stop packet capture
     */
    fun stopCapture() {
        val intent = Intent(getApplication(), DpiVpnService::class.java).apply {
            action = DpiVpnService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
        _isCapturing.value = false
    }
    
    /**
     * Update packet filter
     */
    fun updateFilter(filter: PacketFilter) {
        _packetFilter.value = filter
    }
    
    /**
     * Clear filter
     */
    fun clearFilter() {
        _packetFilter.value = PacketFilter()
    }
    
    /**
     * Clear all captured data
     */
    fun clearAllData() {
        viewModelScope.launch {
            captureManager.clearAllData()
        }
    }
    
    /**
     * Get threats for a specific packet
     */
    suspend fun getThreatsForPacket(packetId: Long): List<ThreatAlert> {
        return database.threatDao().getThreatsForPacket(packetId)
    }
}

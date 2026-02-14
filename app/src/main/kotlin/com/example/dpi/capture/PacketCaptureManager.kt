package com.example.dpi.capture

import com.example.dpi.database.DpiDatabase
import com.example.dpi.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages packet capture, flow tracking, and basic threat detection
 */
@Singleton
class PacketCaptureManager @Inject constructor(
    private val database: DpiDatabase
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Packet processing queue
    private val packetQueue = Channel<Packet>(capacity = 1000)
    
    // Active flows map (key: flow identifier)
    private val activeFlows = ConcurrentHashMap<String, PacketFlow>()
    
    // Statistics
    private val _statistics = MutableStateFlow(CaptureStatistics())
    val statistics: StateFlow<CaptureStatistics> = _statistics.asStateFlow()
    
    private var captureStartTime = 0L
    private var totalPackets = 0L
    private var totalBytes = 0L
    private var droppedPackets = 0L
    
    init {
        // Start packet processor
        scope.launch {
            processPacketQueue()
        }
        
        // Periodic cleanup
        scope.launch {
            performPeriodicCleanup()
        }
    }
    
    /**
     * Called when capture starts
     */
    fun onCaptureStarted() {
        captureStartTime = System.currentTimeMillis()
        totalPackets = 0
        totalBytes = 0
        droppedPackets = 0
        activeFlows.clear()
        
        _statistics.value = _statistics.value.copy(
            isCapturing = true,
            uptime = 0
        )
    }
    
    /**
     * Called when capture stops
     */
    fun onCaptureStopped() {
        _statistics.value = _statistics.value.copy(
            isCapturing = false
        )
        
        // Deactivate all flows
        scope.launch {
            database.flowDao().deactivateOldFlows(System.currentTimeMillis())
        }
    }
    
    /**
     * Called when a packet is captured
     */
    fun onPacketCaptured(packet: Packet) {
        totalPackets++
        totalBytes += packet.payloadSize
        
        // Try to add to queue, drop if full
        if (!packetQueue.trySend(packet).isSuccess) {
            droppedPackets++
        }
    }
    
    /**
     * Update packet rate from VPN service
     */
    fun updatePacketRate(pps: Int) {
        val bandwidthMbps = (pps * 1500 * 8) / 1_000_000f  // Estimate assuming 1500 byte packets
        
        _statistics.value = _statistics.value.copy(
            packetCount = totalPackets,
            packetRate = pps,
            bandwidthMbps = bandwidthMbps,
            activeFlows = activeFlows.size,
            droppedPackets = droppedPackets,
            uptime = System.currentTimeMillis() - captureStartTime
        )
    }
    
    /**
     * Process packets from queue
     */
    private suspend fun processPacketQueue() {
        for (packet in packetQueue) {
            try {
                // Store packet in database
                val packetId = database.packetDao().insert(packet)
                
                // Update or create flow
                val flowId = updateFlow(packet)
                
                // Perform basic threat detection
                detectThreats(packet.copy(id = packetId), flowId)
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Update or create flow for packet
     */
    private suspend fun updateFlow(packet: Packet): Long {
        val flowKey = createFlowKey(packet)
        
        val flow = activeFlows.compute(flowKey) { _, existing ->
            if (existing != null) {
                existing.copy(
                    packetCount = existing.packetCount + 1,
                    totalBytes = existing.totalBytes + packet.payloadSize,
                    lastSeen = packet.timestamp
                )
            } else {
                PacketFlow(
                    sourceIp = packet.sourceIp,
                    destinationIp = packet.destinationIp,
                    sourcePort = packet.sourcePort,
                    destinationPort = packet.destinationPort,
                    protocol = packet.protocol,
                    packetCount = 1,
                    totalBytes = packet.payloadSize.toLong(),
                    firstSeen = packet.timestamp,
                    lastSeen = packet.timestamp,
                    isActive = true
                )
            }
        }!!
        
        return database.flowDao().insert(flow)
    }
    
    /**
     * Create unique key for flow
     */
    private fun createFlowKey(packet: Packet): String {
        return "${packet.sourceIp}:${packet.sourcePort}-${packet.destinationIp}:${packet.destinationPort}-${packet.protocol}"
    }
    
    /**
     * Detect threats in packet/flow
     */
    private suspend fun detectThreats(packet: Packet, flowId: Long) {
        val threats = mutableListOf<ThreatAlert>()
        
        // Simple port scan detection
        if (detectPortScan(packet)) {
            threats.add(
                ThreatAlert(
                    packetId = packet.id,
                    flowId = flowId,
                    threatType = ThreatType.PORT_SCAN,
                    severity = ThreatSeverity.MEDIUM,
                    confidence = 0.8f,
                    description = "Potential port scan detected from ${packet.sourceIp}",
                    detectionMethod = "Behavioral",
                    timestamp = packet.timestamp
                )
            )
        }
        
        // Suspicious port detection
        if (isSuspiciousPort(packet.destinationPort)) {
            threats.add(
                ThreatAlert(
                    packetId = packet.id,
                    flowId = flowId,
                    threatType = ThreatType.SUSPICIOUS_ACTIVITY,
                    severity = ThreatSeverity.LOW,
                    confidence = 0.6f,
                    description = "Connection to suspicious port ${packet.destinationPort}",
                    detectionMethod = "Heuristic",
                    timestamp = packet.timestamp
                )
            )
        }
        
        // Store threats
        for (threat in threats) {
            database.threatDao().insert(threat)
        }
        
        // Update threat count
        if (threats.isNotEmpty()) {
            val totalThreats = database.threatDao().getCount()
            _statistics.value = _statistics.value.copy(threatCount = totalThreats)
        }
    }
    
    /**
     * Detect port scanning behavior
     */
    private fun detectPortScan(packet: Packet): Boolean {
        // Simple heuristic: multiple different destination ports from same source
        val recentPackets = activeFlows.values
            .filter { it.sourceIp == packet.sourceIp }
            .filter { System.currentTimeMillis() - it.lastSeen < 60_000 }  // Last minute
        
        val uniquePorts = recentPackets
            .mapNotNull { it.destinationPort }
            .distinct()
            .size
        
        return uniquePorts > 20  // More than 20 different ports in a minute
    }
    
    /**
     * Check if port is suspicious
     */
    private fun isSuspiciousPort(port: Int?): Boolean {
        if (port == null) return false
        
        val suspiciousPorts = setOf(
            1337,  // Common backdoor
            31337, // Back Orifice
            12345, // NetBus
            27374, // SubSeven
            6667,  // IRC (often used by botnets)
            4444   // Metasploit default
        )
        
        return port in suspiciousPorts
    }
    
    /**
     * Periodic cleanup of old data
     */
    private suspend fun performPeriodicCleanup() {
        while (true) {
            delay(60_000)  // Every minute
            
            try {
                val cutoff = System.currentTimeMillis() - (5 * 60 * 1000)  // 5 minutes ago
                
                // Deactivate old flows
                database.flowDao().deactivateOldFlows(cutoff)
                
                // Clean up in-memory flows
                val iterator = activeFlows.entries.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    if (System.currentTimeMillis() - entry.value.lastSeen > 300_000) {  // 5 minutes
                        iterator.remove()
                    }
                }
                
                // Clean up old packets (keep last 24 hours)
                val oldPacketCutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                database.packetDao().deleteOlderThan(oldPacketCutoff)
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Clear all captured data
     */
    suspend fun clearAllData() {
        database.packetDao().deleteAll()
        database.flowDao().deactivateOldFlows(System.currentTimeMillis())
        activeFlows.clear()
        
        totalPackets = 0
        totalBytes = 0
        droppedPackets = 0
        
        _statistics.value = CaptureStatistics(isCapturing = _statistics.value.isCapturing)
    }
}

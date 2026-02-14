package com.example.dpi.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a captured network packet
 */
@Entity(tableName = "packets")
data class Packet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val sourceIp: String,
    val destinationIp: String,
    val sourcePort: Int?,
    val destinationPort: Int?,
    val protocol: Protocol,
    val payloadSize: Int,
    val flags: Int = 0
)

/**
 * Represents an aggregated network flow (multiple packets)
 */
@Entity(tableName = "flows")
data class PacketFlow(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceIp: String,
    val destinationIp: String,
    val sourcePort: Int?,
    val destinationPort: Int?,
    val protocol: Protocol,
    val packetCount: Int = 1,
    val totalBytes: Long = 0,
    val firstSeen: Long,
    val lastSeen: Long,
    val isActive: Boolean = true
)

/**
 * Represents a detected threat
 */
@Entity(tableName = "threats")
data class ThreatAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packetId: Long,
    val flowId: Long?,
    val threatType: ThreatType,
    val severity: ThreatSeverity,
    val confidence: Float,
    val description: String,
    val detectionMethod: String,
    val timestamp: Long
)

/**
 * Network protocol types
 */
enum class Protocol(val displayName: String, val number: Int) {
    TCP("TCP", 6),
    UDP("UDP", 17),
    ICMP("ICMP", 1),
    HTTP("HTTP", 0),
    HTTPS("HTTPS", 0),
    DNS("DNS", 0),
    UNKNOWN("Unknown", -1);
    
    companion object {
        fun fromNumber(number: Int): Protocol {
            return entries.find { it.number == number } ?: UNKNOWN
        }
    }
}

/**
 * Types of detected threats
 */
enum class ThreatType(val displayName: String) {
    MALWARE("Malware"),
    PORT_SCAN("Port Scan"),
    DOS_ATTACK("DoS Attack"),
    DATA_EXFILTRATION("Data Exfiltration"),
    SUSPICIOUS_ACTIVITY("Suspicious Activity"),
    DNS_TUNNELING("DNS Tunneling"),
    BRUTE_FORCE("Brute Force"),
    UNKNOWN("Unknown")
}

/**
 * Threat severity levels with associated colors
 */
enum class ThreatSeverity(val displayName: String, val colorHex: String) {
    CRITICAL("Critical", "#FF0000"),  // Red
    HIGH("High", "#FF6600"),          // Orange
    MEDIUM("Medium", "#FFCC00"),      // Yellow
    LOW("Low", "#0066FF")             // Blue
}

/**
 * Real-time capture statistics
 */
data class CaptureStatistics(
    val isCapturing: Boolean = false,
    val packetCount: Long = 0,
    val packetRate: Int = 0,  // packets per second
    val bandwidthMbps: Float = 0f,
    val threatCount: Int = 0,
    val activeFlows: Int = 0,
    val droppedPackets: Long = 0,
    val cpuUsage: Float = 0f,
    val memoryUsageMb: Int = 0,
    val uptime: Long = 0  // milliseconds
)

/**
 * Packet with flow information (for UI display)
 */
data class PacketWithFlow(
    val packet: Packet,
    val flow: PacketFlow?,
    val threats: List<ThreatAlert> = emptyList()
)

/**
 * Filter for packet display
 */
data class PacketFilter(
    val protocols: Set<Protocol> = emptySet(),
    val sourceIp: String? = null,
    val destinationIp: String? = null,
    val minTimestamp: Long? = null,
    val maxTimestamp: Long? = null,
    val onlyThreats: Boolean = false
) {
    fun matches(packet: Packet): Boolean {
        if (protocols.isNotEmpty() && packet.protocol !in protocols) return false
        if (sourceIp != null && !packet.sourceIp.contains(sourceIp, ignoreCase = true)) return false
        if (destinationIp != null && !packet.destinationIp.contains(destinationIp, ignoreCase = true)) return false
        if (minTimestamp != null && packet.timestamp < minTimestamp) return false
        if (maxTimestamp != null && packet.timestamp > maxTimestamp) return false
        return true
    }
}

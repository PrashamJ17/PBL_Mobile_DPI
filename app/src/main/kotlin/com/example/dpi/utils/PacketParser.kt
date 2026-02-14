package com.example.dpi.utils

import com.example.dpi.models.Packet
import com.example.dpi.models.Protocol
import java.nio.ByteBuffer

/**
 * Utility for parsing network packets
 */
object PacketParser {
    
    /**
     * Parse a packet from raw bytes
     */
    fun parse(buffer: ByteBuffer): Packet? {
        return try {
            buffer.position(0)
            val version = (buffer.get(0).toInt() shr 4) and 0x0F
            
            when (version) {
                4 -> parseIPv4(buffer)
                6 -> parseIPv6(buffer)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Parse IPv4 packet
     */
    private fun parseIPv4(buffer: ByteBuffer): Packet {
        // IP Header (20 bytes minimum)
        val versionAndIHL = buffer.get(0).toInt()
        val ihl = (versionAndIHL and 0x0F) * 4  // Internet Header Length in bytes
        
        val totalLength = buffer.getShort(2).toInt() and 0xFFFF
        val protocolNumber = buffer.get(9).toInt() and 0xFF
        
        // Source and Destination IP
        val sourceIp = parseIPv4Address(buffer, 12)
        val destIp = parseIPv4Address(buffer, 16)
        
        // Parse transport layer
        val (sourcePort, destPort, protocol) = parseTransportLayer(
            buffer, ihl, protocolNumber
        )
        
        return Packet(
            timestamp = System.currentTimeMillis(),
            sourceIp = sourceIp,
            destinationIp = destIp,
            sourcePort = sourcePort,
            destinationPort = destPort,
            protocol = protocol,
            payloadSize = totalLength - ihl,
            flags = 0
        )
    }
    
    /**
     * Parse IPv6 packet (simplified)
     */
    private fun parseIPv6(buffer: ByteBuffer): Packet {
        // IPv6 Header (40 bytes fixed)
        val payloadLength = buffer.getShort(4).toInt() and 0xFFFF
        val nextHeader = buffer.get(6).toInt() and 0xFF
        
        // Source and Destination IP
        val sourceIp = parseIPv6Address(buffer, 8)
        val destIp = parseIPv6Address(buffer, 24)
        
        // Parse transport layer
        val (sourcePort, destPort, protocol) = parseTransportLayer(
            buffer, 40, nextHeader
        )
        
        return Packet(
            timestamp = System.currentTimeMillis(),
            sourceIp = sourceIp,
            destinationIp = destIp,
            sourcePort = sourcePort,
            destinationPort = destPort,
            protocol = protocol,
            payloadSize = payloadLength,
            flags = 0
        )
    }
    
    /**
     * Parse transport layer (TCP/UDP)
     */
    private fun parseTransportLayer(
        buffer: ByteBuffer,
        offset: Int,
        protocolNumber: Int
    ): Triple<Int?, Int?, Protocol> {
        return when (protocolNumber) {
            6 -> {  // TCP
                val sourcePort = buffer.getShort(offset).toInt() and 0xFFFF
                val destPort = buffer.getShort(offset + 2).toInt() and 0xFFFF
                
                // Identify application protocol by port
                val appProtocol = identifyProtocolByPort(destPort, sourcePort)
                
                Triple(sourcePort, destPort, appProtocol)
            }
            17 -> {  // UDP
                val sourcePort = buffer.getShort(offset).toInt() and 0xFFFF
                val destPort = buffer.getShort(offset + 2).toInt() and 0xFFFF
                
                // Identify application protocol by port
                val appProtocol = identifyProtocolByPort(destPort, sourcePort)
                
                Triple(sourcePort, destPort, appProtocol)
            }
            1 -> {  // ICMP
                Triple(null, null, Protocol.ICMP)
            }
            else -> {
                Triple(null, null, Protocol.UNKNOWN)
            }
        }
    }
    
    /**
     * Identify application protocol by port number
     */
    private fun identifyProtocolByPort(destPort: Int, sourcePort: Int): Protocol {
        return when {
            destPort == 80 || sourcePort == 80 -> Protocol.HTTP
            destPort == 443 || sourcePort == 443 -> Protocol.HTTPS
            destPort == 53 || sourcePort == 53 -> Protocol.DNS
            destPort < 1024 || sourcePort < 1024 -> {
                // Well-known port, determine protocol
                if (destPort == 80 || sourcePort == 80) Protocol.HTTP
                else if (destPort == 443 || sourcePort == 443) Protocol.HTTPS
                else Protocol.TCP
            }
            else -> Protocol.TCP
        }
    }
    
    /**
     * Parse IPv4 address from buffer
     */
    private fun parseIPv4Address(buffer: ByteBuffer, offset: Int): String {
        return buildString {
            append(buffer.get(offset).toInt() and 0xFF)
            append(".")
            append(buffer.get(offset + 1).toInt() and 0xFF)
            append(".")
            append(buffer.get(offset + 2).toInt() and 0xFF)
            append(".")
            append(buffer.get(offset + 3).toInt() and 0xFF)
        }
    }
    
    /**
     * Parse IPv6 address from buffer
     */
    private fun parseIPv6Address(buffer: ByteBuffer, offset: Int): String {
        return buildString {
            for (i in 0 until 8) {
                if (i > 0) append(":")
                val value = buffer.getShort(offset + i * 2).toInt() and 0xFFFF
                append(value.toString(16))
            }
        }
    }
    
    /**
     * Calculate payload entropy (for threat detection)
     */
    fun calculateEntropy(payload: ByteArray): Float {
        if (payload.isEmpty()) return 0f
        
        val frequency = IntArray(256)
        for (byte in payload) {
            frequency[byte.toInt() and 0xFF]++
        }
        
        var entropy = 0.0
        val length = payload.size.toDouble()
        
        for (count in frequency) {
            if (count > 0) {
                val probability = count / length
                entropy -= probability * Math.log(probability) / Math.log(2.0)
            }
        }
        
        return entropy.toFloat()
    }
}

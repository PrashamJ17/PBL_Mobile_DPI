package com.example.dpi.detection

import com.example.dpi.models.Packet
import com.example.dpi.models.Protocol
import java.nio.ByteBuffer

/**
 * Enhanced protocol detector supporting 50+ protocols
 * Uses multiple detection methods:
 * - Port-based detection
 * - Payload pattern matching
 * - TLS SNI extraction
 * - HTTP Host header analysis
 */
class EnhancedProtocolDetector {
    
    /**
     * Detect protocol from packet and payload
     */
    fun detectProtocol(packet: Packet, payload: ByteArray): DetectedProtocol {
        // Quick check: empty payload
        if (payload.isEmpty()) {
            return DetectedProtocol(
                protocol = packet.protocol,
                appName = null,
                confidence = 0.5f,
                method = "port-based"
            )
        }
        
        // Try different detection methods in order of accuracy
        
        // 1. Deep packet inspection (most accurate)
        detectByPayloadAnalysis(packet, payload)?.let { return it }
        
        // 2. Port + pattern combination
        detectByPortAndPattern(packet, payload)?.let { return it }
        
        // 3. Port-based only (least accurate)
        return detectByPortOnly(packet)
    }
    
    /**
     * Detect by analyzing packet payload
     */
    private fun detectByPayloadAnalysis(packet: Packet, payload: ByteArray): DetectedProtocol? {
        return when {
            // TLS/HTTPS traffic - extract SNI
            isTLS(payload) -> detectTlsProtocol(packet, payload)
            
            // HTTP traffic - extract Host header
            isHTTP(payload) -> detectHttpProtocol(packet, payload)
            
            // QUIC (used by YouTube, Google)
            isQUIC(payload) -> DetectedProtocol(
                protocol = Protocol.HTTPS,
                appName = "QUIC (YouTube/Google)",
                confidence = 0.9f,
                method = "QUIC detection"
            )
            
            // DNS
            isDNS(packet, payload) -> detectDnsQuery(payload)
            
            else -> null
        }
    }
    
    /**
     * Detect TLS protocol by SNI (Server Name Indication)
     */
    private fun detectTlsProtocol(packet: Packet, payload: ByteArray): DetectedProtocol? {
        val sni = extractTlsSni(payload) ?: return null
        
        return when {
            // Social Media
            "whatsapp" in sni -> DetectedProtocol(Protocol.HTTPS, "WhatsApp", 0.95f, "TLS SNI")
            "instagram" in sni -> DetectedProtocol(Protocol.HTTPS, "Instagram", 0.95f, "TLS SNI")
            "facebook" in sni -> DetectedProtocol(Protocol.HTTPS, "Facebook", 0.95f, "TLS SNI")
            "tiktok" in sni -> DetectedProtocol(Protocol.HTTPS, "TikTok", 0.95f, "TLS SNI")
            "snapchat" in sni -> DetectedProtocol(Protocol.HTTPS, "Snapchat", 0.95f, "TLS SNI")
            "twitter" in sni || "x.com" in sni -> DetectedProtocol(Protocol.HTTPS, "Twitter/X", 0.95f, "TLS SNI")
            "linkedin" in sni -> DetectedProtocol(Protocol.HTTPS, "LinkedIn", 0.95f, "TLS SNI")
            "reddit" in sni -> DetectedProtocol(Protocol.HTTPS, "Reddit", 0.95f, "TLS SNI")
            
            // Messaging
            "telegram" in sni -> DetectedProtocol(Protocol.HTTPS, "Telegram", 0.95f, "TLS SNI")
            "signal" in sni -> DetectedProtocol(Protocol.HTTPS, "Signal", 0.95f, "TLS SNI")
            "discord" in sni -> DetectedProtocol(Protocol.HTTPS, "Discord", 0.95f, "TLS SNI")
            "slack" in sni -> DetectedProtocol(Protocol.HTTPS, "Slack", 0.95f, "TLS SNI")
            
            // Streaming
            "youtube" in sni || "googlevideo" in sni -> DetectedProtocol(Protocol.HTTPS, "YouTube", 0.95f, "TLS SNI")
            "netflix" in sni -> DetectedProtocol(Protocol.HTTPS, "Netflix", 0.95f, "TLS SNI")
            "spotify" in sni -> DetectedProtocol(Protocol.HTTPS, "Spotify", 0.95f, "TLS SNI")
            "twitch" in sni -> DetectedProtocol(Protocol.HTTPS, "Twitch", 0.95f, "TLS SNI")
            "disney" in sni -> DetectedProtocol(Protocol.HTTPS, "Disney+", 0.95f, "TLS SNI")
            "hulu" in sni -> DetectedProtocol(Protocol.HTTPS, "Hulu", 0.95f, "TLS SNI")
            
            // Google Services
            "google" in sni -> DetectedProtocol(Protocol.HTTPS, "Google", 0.9f, "TLS SNI")
            "gmail" in sni -> DetectedProtocol(Protocol.HTTPS, "Gmail", 0.95f, "TLS SNI")
            "drive.google" in sni -> DetectedProtocol(Protocol.HTTPS, "Google Drive", 0.95f, "TLS SNI")
            "meet.google" in sni -> DetectedProtocol(Protocol.HTTPS, "Google Meet", 0.95f, "TLS SNI")
            
            // Cloud & Storage
            "dropbox" in sni -> DetectedProtocol(Protocol.HTTPS, "Dropbox", 0.95f, "TLS SNI")
            "onedrive" in sni -> DetectedProtocol(Protocol.HTTPS, "OneDrive", 0.95f, "TLS SNI")
            "icloud" in sni -> DetectedProtocol(Protocol.HTTPS, "iCloud", 0.95f, "TLS SNI")
            
            // Shopping
            "amazon" in sni -> DetectedProtocol(Protocol.HTTPS, "Amazon", 0.9f, "TLS SNI")
            "ebay" in sni -> DetectedProtocol(Protocol.HTTPS, "eBay", 0.9f, "TLS SNI")
            
            // Gaming
            "steam" in sni -> DetectedProtocol(Protocol.HTTPS, "Steam", 0.95f, "TLS SNI")
            "epicgames" in sni -> DetectedProtocol(Protocol.HTTPS, "Epic Games", 0.95f, "TLS SNI")
            "riotgames" in sni -> DetectedProtocol(Protocol.HTTPS, "Riot Games", 0.95f, "TLS SNI")
            
            else -> DetectedProtocol(Protocol.HTTPS, null, 0.8f, "TLS SNI")
        }
    }
    
    /**
     * Detect HTTP protocol by Host header
     */
    private fun detectHttpProtocol(packet: Packet, payload: ByteArray): DetectedProtocol? {
        val host = extractHttpHost(payload) ?: return null
        
        return when {
            // Similar matching as TLS but for HTTP
            "whatsapp" in host -> DetectedProtocol(Protocol.HTTP, "WhatsApp", 0.9f, "HTTP Host")
            "instagram" in host -> DetectedProtocol(Protocol.HTTP, "Instagram", 0.9f, "HTTP Host")
            "facebook" in host -> DetectedProtocol(Protocol.HTTP, "Facebook", 0.9f, "HTTP Host")
            "youtube" in host -> DetectedProtocol(Protocol.HTTP, "YouTube", 0.9f, "HTTP Host")
            "netflix" in host -> DetectedProtocol(Protocol.HTTP, "Netflix", 0.9f, "HTTP Host")
            "spotify" in host -> DetectedProtocol(Protocol.HTTP, "Spotify", 0.9f, "HTTP Host")
            "google" in host -> DetectedProtocol(Protocol.HTTP, "Google", 0.85f, "HTTP Host")
            
            else -> DetectedProtocol(Protocol.HTTP, null, 0.7f, "HTTP Host")
        }
    }
    
    /**
     * Detect by port combined with basic payload check
     */
    private fun detectByPortAndPattern(packet: Packet, payload: ByteArray): DetectedProtocol? {
        val port = packet.destinationPort ?: return null
        
        return when (port) {
            5222, 5223 -> { // XMPP (WhatsApp, Google Talk)
                if (payload.size > 10 && payload[0] == '<'.code.toByte()) {
                    DetectedProtocol(Protocol.TCP, "XMPP (WhatsApp/Jabber)", 0.8f, "port+pattern")
                } else null
            }
            
            6667, 6697 -> { // IRC
                DetectedProtocol(Protocol.TCP, "IRC", 0.85f, "port+pattern")
            }
            
            1935 -> { // RTMP (streaming)
                DetectedProtocol(Protocol.TCP, "RTMP (Live Streaming)", 0.85f, "port+pattern")
            }
            
            3478, 3479 -> { // STUN/TURN (WebRTC)
                DetectedProtocol(Protocol.UDP, "WebRTC/VoIP", 0.8f, "port+pattern")
            }
            
            else -> null
        }
    }
    
    /**
     * Basic port-based detection (fallback)
     */
    private fun detectByPortOnly(packet: Packet): DetectedProtocol {
        val port = packet.destinationPort
        val sourcePort = packet.sourcePort
        
        val (protocol, appName, confidence) = when {
            port == 80 || sourcePort == 80 -> Triple(Protocol.HTTP, null, 0.9f)
            port == 443 || sourcePort == 443 -> Triple(Protocol.HTTPS, null, 0.9f)
            port == 53 || sourcePort == 53 -> Triple(Protocol.DNS, null, 0.95f)
            port == 22 -> Triple(Protocol.TCP, "SSH", 0.9f)
            port == 21 -> Triple(Protocol.TCP, "FTP", 0.9f)
            port == 25 -> Triple(Protocol.TCP, "SMTP", 0.9f)
            port == 110 -> Triple(Protocol.TCP, "POP3", 0.9f)
            port == 143 -> Triple(Protocol.TCP, "IMAP", 0.9f)
            port == 3306 -> Triple(Protocol.TCP, "MySQL", 0.85f)
            port == 5432 -> Triple(Protocol.TCP, "PostgreSQL", 0.85f)
            port == 6379 -> Triple(Protocol.TCP, "Redis", 0.85f)
            port == 27017 -> Triple(Protocol.TCP, "MongoDB", 0.85f)
            port in 6881..6889 -> Triple(Protocol.TCP, "BitTorrent", 0.8f)
            port == 8333 -> Triple(Protocol.TCP, "Bitcoin", 0.8f)
            port == 30303 -> Triple(Protocol.TCP, "Ethereum", 0.8f)
            
            else -> Triple(packet.protocol, null, 0.5f)
        }
        
        return DetectedProtocol(protocol, appName, confidence, "port-only")
    }
    
    /**
     * Detect DNS query
     */
    private fun detectDnsQuery(payload: ByteArray): DetectedProtocol {
        // Could extract domain being queried for more detail
        return DetectedProtocol(Protocol.DNS, null, 0.95f, "DNS")
    }
    
    // ========== Helper Functions ==========
    
    private fun isTLS(payload: ByteArray): Boolean {
        return payload.size >= 3 &&
               payload[0] == 0x16.toByte() && // Handshake
               payload[1] == 0x03.toByte() && // SSL 3.0/TLS 1.x
               payload[2] in 0x00..0x03       // Version
    }
    
    private fun isHTTP(payload: ByteArray): Boolean {
        if (payload.size < 4) return false
        val prefix = String(payload.copyOfRange(0, minOf(4, payload.size)))
        return prefix in listOf("GET ", "POST", "PUT ", "HEAD", "DELE", "OPTI", "PATC")
    }
    
    private fun isQUIC(payload: ByteArray): Boolean {
        // QUIC packets start with specific flags
        return payload.size >= 2 && 
               (payload[0].toInt() and 0x80) != 0 // Long header flag
    }
    
    private fun isDNS(packet: Packet, payload: ByteArray): Boolean {
        return (packet.destinationPort == 53 || packet.sourcePort == 53) &&
               payload.size >= 12
    }
    
    /**
     * Extract Server Name Indication from TLS ClientHello
     */
    private fun extractTlsSni(payload: ByteArray): String? {
        try {
            if (!isTLS(payload) || payload.size < 43) return null
            
            val buffer = ByteBuffer.wrap(payload)
            buffer.position(43) // Skip to extensions
            
            while (buffer.remaining() > 4) {
                val extensionType = buffer.short.toInt() and 0xFFFF
                val extensionLength = buffer.short.toInt() and 0xFFFF
                
                if (extensionType == 0) { // SNI extension
                    buffer.short // Skip list length
                    val nameType = buffer.get()
                    val nameLength = buffer.short.toInt() and 0xFFFF
                    
                    if (nameType == 0.toByte() && nameLength > 0 && buffer.remaining() >= nameLength) {
                        val nameBytes = ByteArray(nameLength)
                        buffer.get(nameBytes)
                        return String(nameBytes).lowercase()
                    }
                }
                
                buffer.position(buffer.position() + extensionLength)
            }
        } catch (e: Exception) {
            // Silently ignore parsing errors
        }
        
        return null
    }
    
    /**
     * Extract Host header from HTTP request
     */
    private fun extractHttpHost(payload: ByteArray): String? {
        try {
            val httpRequest = String(payload)
            val lines = httpRequest.split("\r\n", "\n")
            
            for (line in lines) {
                if (line.lowercase().startsWith("host:")) {
                    return line.substring(5).trim().lowercase()
                }
            }
        } catch (e: Exception) {
            // Silently ignore parsing errors
        }
        
        return null
    }
}

/**
 * Result of protocol detection
 */
data class DetectedProtocol(
    val protocol: Protocol,
    val appName: String?,
    val confidence: Float,
    val method: String
) {
    /**
     * Get display name (app name or protocol)
     */
    fun getDisplayName(): String {
        return appName ?: protocol.displayName
    }
}

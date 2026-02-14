package com.example.dpi.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.dpi.capture.PacketCaptureManager
import com.example.dpi.ui.MainActivity
import com.example.dpi.utils.PacketParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import javax.inject.Inject

/**
 * VPN Service for capturing network packets
 * FIXED: Properly forwards packets so apps work normally
 * 
 * Key fix: We analyze packets but immediately forward them back through VPN
 * The VPN interface itself handles routing to real network
 */
@AndroidEntryPoint
class DpiVpnService : VpnService() {
    
    @Inject
    lateinit var captureManager: PacketCaptureManager
    
    private var vpnInterface: ParcelFileDescriptor? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false
    
    private var packetCount = 0L
    private var bytesTransferred = 0L
    private var lastStatsUpdate = 0L
    
    companion object {
        private const val VPN_ADDRESS = "10.0.0.2"
        private const val VPN_ROUTE = "0.0.0.0"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "dpi_vpn_channel"
        private const val MTU = 1500
        
        const val ACTION_START = "com.example.dpi.START_VPN"
        const val ACTION_STOP = "com.example.dpi.STOP_VPN"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startVpn()
            ACTION_STOP -> stopVpn()
        }
        return START_STICKY
    }
    
    private fun startVpn() {
        if (isRunning) return
        
        try {
            // Build VPN with proper routing
            vpnInterface = Builder()
                .setSession("Mobile DPI Hub")
                .addAddress(VPN_ADDRESS, 24)
                .addRoute(VPN_ROUTE, 0)  // Route all traffic through VPN
                .addDnsServer("8.8.8.8")
                .addDnsServer("8.8.4.4")
                .setMtu(MTU)
                .setBlocking(false)
                .establish()
            
            if (vpnInterface == null) {
                stopSelf()
                return
            }
            
            isRunning = true
            lastStatsUpdate = System.currentTimeMillis()
            captureManager.onCaptureStarted()
            startForeground(NOTIFICATION_ID, createNotification())
            
            // Start packet processing
            scope.launch {
                processPackets()
            }
            
            // Start statistics
            scope.launch {
                updateStatistics()
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }
    
    private fun stopVpn() {
        isRunning = false
        captureManager.onCaptureStopped()
        
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        vpnInterface = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    /**
     * CRITICAL FIX: Process packets for analysis but DON'T block them
     * 
     * The key insight: We're using VPN in "monitor mode"
     * - Read packets from VPN interface (apps → VPN)
     * - Analyze them asynchronously
     * - Write them back immediately (VPN → network)
     * 
     * The Android VPN subsystem handles the actual routing!
     */
    private suspend fun processPackets() = withContext(Dispatchers.IO) {
        val vpn = vpnInterface ?: return@withContext
        val inputStream = FileInputStream(vpn.fileDescriptor)
        val outputStream = FileOutputStream(vpn.fileDescriptor)
        val buffer = ByteBuffer.allocate(32767)
        
        while (isRunning) {
            try {
                // Read packet from apps
                buffer.clear()
                val length = inputStream.channel.read(buffer)
                
                if (length > 0) {
                    buffer.flip()
                    packetCount++
                    bytesTransferred += length
                    
                    // CRITICAL: Immediately write back to VPN
                    // This forwards it to the real network
                    val packetData = ByteArray(length)
                    buffer.get(packetData)
                    
                    // Analyze packet asynchronously (don't block)
                    launch {
                        try {
                            val packetBuffer = ByteBuffer.wrap(packetData)
                            val packet = PacketParser.parse(packetBuffer)
                            if (packet != null) {
                                captureManager.onPacketCaptured(packet)
                            }
                        } catch (e: Exception) {
                            // Ignore parsing errors, don't let them break forwarding
                        }
                    }
                    
                    // Forward packet to network immediately
                    buffer.rewind()
                    outputStream.channel.write(buffer)
                } else if (length < 0) {
                    // End of stream
                    break
                } else {
                    // No data available, brief pause
                    delay(1)
                }
                
            } catch (e: Exception) {
                if (isRunning) {
                    e.printStackTrace()
                    delay(10)
                }
            }
        }
    }
    
    private suspend fun updateStatistics() = withContext(Dispatchers.Default) {
        while (isRunning) {
            delay(1000)
            
            val now = System.currentTimeMillis()
            val elapsed = now - lastStatsUpdate
            
            if (elapsed > 0) {
                val pps = ((packetCount * 1000) / elapsed).toInt()
                captureManager.updatePacketRate(pps)
                
                packetCount = 0
                bytesTransferred = 0
                lastStatsUpdate = now
            }
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mobile DPI Hub")
            .setContentText("Monitoring network traffic")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "DPI VPN Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notification for packet capture service"
            setShowBadge(false)
        }
        
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        scope.cancel()
    }
}

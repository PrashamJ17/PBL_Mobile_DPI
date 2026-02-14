package com.example.dpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dpi.models.CaptureStatistics
import com.example.dpi.models.ThreatSeverity
import com.example.dpi.ui.MainViewModel
import com.example.dpi.ui.theme.getThreatColor
import java.text.DecimalFormat

/**
 * Home screen showing capture status and statistics
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onStartCapture: () -> Unit
) {
    val isCapturing by viewModel.isCapturing.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val recentThreats by viewModel.recentThreats.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Capture control card
        CaptureControlCard(
            isCapturing = isCapturing,
            statistics = statistics,
            onStart = onStartCapture,
            onStop = { viewModel.stopCapture() }
        )
        
        // Statistics cards
        StatisticsCards(statistics = statistics)
        
        // Threat summary
        ThreatSummaryCard(
            totalThreats = statistics.threatCount,
            threats = recentThreats.take(5)
        )
        
        // Quick actions
        QuickActionsCard(
            onClearData = { viewModel.clearAllData() }
        )
    }
}

@Composable
fun CaptureControlCard(
    isCapturing: Boolean,
    statistics: CaptureStatistics,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCapturing) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isCapturing) "Capture Active" else "Ready to Capture",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (isCapturing) {
                        Text(
                            text = formatUptime(statistics.uptime),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Icon(
                    imageVector = if (isCapturing) Icons.Default.PlayArrow else Icons.Default.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (isCapturing) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            if (isCapturing) {
                // Live stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Packets/s",
                        value = statistics.packetRate.toString()
                    )
                    StatItem(
                        label = "Bandwidth",
                        value = String.format("%.1f Mbps", statistics.bandwidthMbps)
                    )
                    StatItem(
                        label = "Threats",
                        value = statistics.threatCount.toString()
                    )
                }
            }
            
            Button(
                onClick = if (isCapturing) onStop else onStart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCapturing) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                Icon(
                    imageVector = if (isCapturing) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isCapturing) "Stop Capture" else "Start Capture")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatisticsCards(statistics: CaptureStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.List,
            label = "Total Packets",
            value = formatNumber(statistics.packetCount)
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.AccountTree,
            label = "Active Flows",
            value = statistics.activeFlows.toString()
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ThreatSummaryCard(
    totalThreats: Int,
    threats: List<com.example.dpi.models.ThreatAlert>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Threats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Badge {
                    Text(totalThreats.toString())
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (threats.isEmpty()) {
                Text(
                    text = "No threats detected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                threats.forEach { threat ->
                    ThreatItem(threat)
                    if (threat != threats.last()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThreatItem(threat: com.example.dpi.models.ThreatAlert) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = threat.threatType.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = threat.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Badge(
            containerColor = getThreatColor(threat.severity).copy(alpha = 0.2f),
            contentColor = getThreatColor(threat.severity)
        ) {
            Text(threat.severity.displayName)
        }
    }
}

@Composable
fun QuickActionsCard(onClearData: () -> Unit) {
    var showClearDialog by remember { mutableStateOf(false) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Data")
            }
        }
    }
    
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Data?") },
            text = { Text("This will delete all captured packets, flows, and threats. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearData()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatUptime(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}

private fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

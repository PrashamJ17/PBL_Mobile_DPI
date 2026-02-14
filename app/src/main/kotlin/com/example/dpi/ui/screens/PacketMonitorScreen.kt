package com.example.dpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dpi.models.Packet
import com.example.dpi.models.Protocol
import com.example.dpi.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Packet Monitor screen showing real-time packet list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacketMonitorScreen(viewModel: MainViewModel) {
    val filteredPackets by viewModel.filteredPackets.collectAsState()
    val isCapturing by viewModel.isCapturing.collectAsState()
    var selectedPacket by remember { mutableStateOf<Packet?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showFilterSheet = true },
                icon = { Icon(Icons.Default.FilterList, contentDescription = null) },
                text = { Text("Filter") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status bar
            StatusBar(
                isCapturing = isCapturing,
                packetCount = filteredPackets.size
            )
            
            // Packet list
            if (filteredPackets.isEmpty()) {
                EmptyState(isCapturing = isCapturing)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = filteredPackets,
                        key = { it.id }
                    ) { packet ->
                        PacketItem(
                            packet = packet,
                            onClick = { selectedPacket = packet }
                        )
                    }
                }
            }
        }
        
        // Packet details bottom sheet
        if (selectedPacket != null) {
            PacketDetailsSheet(
                packet = selectedPacket!!,
                viewModel = viewModel,
                onDismiss = { selectedPacket = null }
            )
        }
        
        // Filter bottom sheet
        if (showFilterSheet) {
            FilterBottomSheet(
                viewModel = viewModel,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
fun StatusBar(isCapturing: Boolean, packetCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCapturing) Icons.Default.Circle else Icons.Default.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = if (isCapturing) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCapturing) "Capturing" else "Paused",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = "$packetCount packets",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PacketItem(packet: Packet, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Protocol and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProtocolBadge(protocol = packet.protocol)
                Text(
                    text = formatTimestamp(packet.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Source and destination
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatAddress(packet.sourceIp, packet.sourcePort),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatAddress(packet.destinationIp, packet.destinationPort),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Size
                Text(
                    text = "${packet.payloadSize} bytes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProtocolBadge(protocol: Protocol) {
    val color = when (protocol) {
        Protocol.TCP -> MaterialTheme.colorScheme.primary
        Protocol.UDP -> MaterialTheme.colorScheme.secondary
        Protocol.HTTP -> MaterialTheme.colorScheme.tertiary
        Protocol.HTTPS -> MaterialTheme.colorScheme.tertiary
        Protocol.DNS -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Badge(
        containerColor = color.copy(alpha = 0.2f),
        contentColor = color
    ) {
        Text(protocol.displayName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacketDetailsSheet(
    packet: Packet,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val threats by remember(packet.id) {
        mutableStateOf<List<com.example.dpi.models.ThreatAlert>>(emptyList())
    }
    
    LaunchedEffect(packet.id) {
        // Load threats for this packet
        // threats = viewModel.getThreatsForPacket(packet.id)
    }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Packet Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Basic info
            DetailRow("Protocol", packet.protocol.displayName)
            DetailRow("Timestamp", formatFullTimestamp(packet.timestamp))
            DetailRow("Size", "${packet.payloadSize} bytes")
            
            Divider()
            
            // Network info
            Text(
                text = "Network Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            DetailRow("Source IP", packet.sourceIp)
            packet.sourcePort?.let {
                DetailRow("Source Port", it.toString())
            }
            DetailRow("Destination IP", packet.destinationIp)
            packet.destinationPort?.let {
                DetailRow("Destination Port", it.toString())
            }
            
            if (threats.isNotEmpty()) {
                Divider()
                Text(
                    text = "Threats (${threats.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
                threats.forEach { threat ->
                    ThreatItem(threat)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val currentFilter by viewModel.packetFilter.collectAsState()
    var selectedProtocols by remember { mutableStateOf(currentFilter.protocols) }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filter Packets",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Protocol filters
            Text(
                text = "Protocols",
                style = MaterialTheme.typography.titleMedium
            )
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Protocol.entries.forEach { protocol ->
                    if (protocol != Protocol.UNKNOWN) {
                        FilterChip(
                            selected = protocol in selectedProtocols,
                            onClick = {
                                selectedProtocols = if (protocol in selectedProtocols) {
                                    selectedProtocols - protocol
                                } else {
                                    selectedProtocols + protocol
                                }
                            },
                            label = { Text(protocol.displayName) }
                        )
                    }
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.clearFilter()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                
                Button(
                    onClick = {
                        viewModel.updateFilter(currentFilter.copy(protocols = selectedProtocols))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EmptyState(isCapturing: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (isCapturing) {
                    "Waiting for packets..."
                } else {
                    "Start capture to see packets"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatFullTimestamp(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatAddress(ip: String, port: Int?): String {
    return if (port != null) "$ip:$port" else ip
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Simple flow row implementation
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

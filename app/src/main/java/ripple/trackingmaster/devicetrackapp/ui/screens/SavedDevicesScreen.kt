package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedDevicesScreen(
    onDeviceSelected: (String) -> Unit,
    vm: SavedDevicesViewModel = hiltViewModel()
) {
    val devices by vm.devices.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Saved Devices") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            if (devices.isEmpty()) {
                Text("No devices saved yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(devices.size) { idx ->
                        val d = devices[idx]
                        DeviceCard(d, onDeviceSelected)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceCard(entity: DeviceEntity, onDeviceSelected: (String) -> Unit) {
    val date = remember(entity.createdAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(entity.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceSelected(entity.mac) },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(entity.customName ?: "HipPro Belt",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(4.dp))
            Text("MAC: ${entity.mac}")
            Text("Serial: ${entity.serialNumber ?: "--"}")
            Text("Belt No: ${entity.beltNumber ?: "--"}")
            Text("Size: ${entity.beltSize ?: "--"}")
            Text("Status: ${entity.lastSeenStatus ?: "--"}")
            Text("Saved: $date", style = MaterialTheme.typography.bodySmall)
        }
    }
}

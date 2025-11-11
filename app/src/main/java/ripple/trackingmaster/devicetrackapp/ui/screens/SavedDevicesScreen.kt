package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ripple.trackingmaster.devicetrackapp.data.local.entity.DeviceEntity

@Composable
fun SavedDevicesScreen(
    onDeviceSelected: (String) -> Unit,
    vm: SavedDevicesViewModel = hiltViewModel()
) {
    val devices by vm.devices.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Saved Devices") })
        }
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
                LazyColumn {
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onDeviceSelected(entity.mac) },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(entity.name ?: "HipPro Belt", style = MaterialTheme.typography.titleMedium)
            Text("MAC: ${entity.mac}", style = MaterialTheme.typography.bodySmall)
            Text("Last Seen: ${entity.lastSeen}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

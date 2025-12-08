package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse

@Composable
fun SavedDevicesScreen(
    onDeviceSelected: (String) -> Unit,
    vm: SavedDevicesViewModel = hiltViewModel()
) {
    val belts by vm.belts.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Saved Devices") }) }
    ) { padding ->
        if (belts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("No saved devices found.", style = MaterialTheme.typography.titleMedium)
                    Text("Connect to a device and click 'Save Details'.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(belts, key = { it.macAddress }) { belt ->
                    DetailedDeviceRow(belt = belt, onClick = { onDeviceSelected(belt.macAddress) })
                }
            }
        }
    }
}

@Composable
private fun DetailedDeviceRow(belt: BeltApiResponse, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header: Team Member
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Tested by: ${belt.teamMember ?: "Unknown"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Details Grid
            Row(Modifier.fillMaxWidth()) {
                // Column 1
                Column(Modifier.weight(1f)) {
                    InfoLabelValue(label = "MAC Address", value = belt.macAddress)
                    Spacer(Modifier.height(8.dp))
                    InfoLabelValue(label = "Size", value = belt.beltSize ?: "--")
                }
                // Column 2
                Column(Modifier.weight(1f)) {
                    InfoLabelValue(label = "Serial No", value = belt.serialNumber ?: "--")
                    Spacer(Modifier.height(8.dp))
                    InfoLabelValue(label = "Assigned To", value = belt.assignedTo ?: "Unassigned")
                }
            }
        }
    }
}

@Composable
fun InfoLabelValue(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ripple.trackingmaster.devicetrackapp.data.remote.dto.BeltApiResponse

@Composable
fun SiteDetailScreen(
    siteId: Int,
    onDeviceClick: (String) -> Unit,
    vm: SiteDetailViewModel = hiltViewModel()
) {
    // Get the new UI state from the ViewModel
    val uiState by vm.uiState.collectAsState()
    val site = uiState.site
    val assignedBelts = uiState.assignedBelts

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(site?.siteName ?: "Loading...") }
            )
        }
    ) { padding ->

        if (site == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    "Devices assigned to this site:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))

                if (assignedBelts.isEmpty()) {
                    Text("No devices are currently assigned to this site.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(assignedBelts, key = { it.macAddress }) { belt ->
                            DeviceRow(
                                belt = belt,
                                onClick = { onDeviceClick(belt.macAddress) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// We can reuse the DeviceRow from SavedDevicesScreen
// (You could move this to a shared 'components' folder later)
@Composable
private fun DeviceRow(belt: BeltApiResponse, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = belt.teamMember ?: (belt.serialNumber ?: "Unknown Device"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(text = "MAC: ${belt.macAddress}", style = MaterialTheme.typography.bodySmall)
            belt.serialNumber?.let {
                Text(text = "Serial: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
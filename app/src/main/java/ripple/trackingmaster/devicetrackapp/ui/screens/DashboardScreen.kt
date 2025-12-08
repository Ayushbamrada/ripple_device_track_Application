package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    onScanClick: () -> Unit,
    onSavedDevicesClick: () -> Unit,
    onSitesClick: () -> Unit,
    vm: DashboardViewModel = hiltViewModel() // ✅ USE NEW VIEWMODEL
) {
    // Get the new state from the ViewModel
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Hip-Pro Dashboard") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // "Scan for Devices" button
            DashboardButton(
                icon = Icons.Filled.Search,
                title = "Scan for Devices",
                subtitle = "Find and connect to new belts",
                onClick = onScanClick
            )

            // "Saved Devices" button - now shows count
            DashboardButton(
                icon = Icons.Filled.List,
                title = "Saved Devices",
                subtitle = "${uiState.deviceCount} devices in database",
                onClick = onSavedDevicesClick
            )

            // "Clinical Sites" button - now shows count
            DashboardButton(
                icon = Icons.Filled.Build,
                title = "Clinical Sites",
                subtitle = "${uiState.siteCount} sites configured",
                onClick = onSitesClick
            )
        }
    }
}

@Composable
private fun DashboardButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
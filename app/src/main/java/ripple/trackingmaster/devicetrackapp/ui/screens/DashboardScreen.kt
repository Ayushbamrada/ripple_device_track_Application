//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Bluetooth
//import androidx.compose.material.icons.filled.List
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//
//@Composable
//fun DashboardScreen(
//    onScanClick: () -> Unit,
//    onSavedDevicesClick: () -> Unit,
//    onSitesClick: () -> Unit,
//    vm: DashboardViewModel = hiltViewModel()
//) {
//    val stats by vm.stats.collectAsState()
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("HipPro Device Manager") }
//            )
//        }
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(20.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Top
//        ) {
//
//            // ---- Stats Card ----
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(18.dp),
//                elevation = CardDefaults.cardElevation(6.dp)
//            ) {
//                Column(
//                    Modifier.padding(20.dp)
//                ) {
//                    Text(
//                        "Total Registered Belts",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Spacer(Modifier.height(8.dp))
//                    Text(
//                        stats.totalDevices.toString(),
//                        style = MaterialTheme.typography.headlineLarge,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(32.dp))
//
//            // ---- Actions ----
//            Text("Actions", style = MaterialTheme.typography.titleMedium)
//            Spacer(Modifier.height(16.dp))
//
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(18.dp)
//            ) {
//
//                DashboardButton(
//                    text = "Scan Nearby Devices",
//                    icon = Icons.Default.Bluetooth,
//                    onClick = onScanClick
//                )
//
//                DashboardButton(
//                    text = "Saved Belts",
//                    icon = Icons.Default.List,
//                    onClick = onSavedDevicesClick
//                )
//
//                DashboardButton(
//                    text = "Site Management",
//                    icon = Icons.Default.LocationOn,
//                    onClick = onSitesClick
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun DashboardButton(
//    text: String,
//    icon: androidx.compose.ui.graphics.vector.ImageVector,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(70.dp),
//        shape = RoundedCornerShape(14.dp),
//        elevation = CardDefaults.cardElevation(4.dp),
//        onClick = onClick
//    ) {
//        Row(
//            Modifier
//                .fillMaxSize()
//                .padding(horizontal = 20.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(20.dp)
//        ) {
//
//            Icon(
//                imageVector = icon,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.primary
//            )
//
//            Text(
//                text,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    onScanClick: () -> Unit,
    onSavedDevicesClick: () -> Unit,
    onSitesClick: () -> Unit,
    vm: DashboardViewModel = hiltViewModel()
) {
    val stats by vm.stats.collectAsState()

    val total by animateIntAsState(stats.totalDevices)
    val assigned by animateIntAsState(stats.assignedDevices)
    val sites by animateIntAsState(stats.totalSites)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HipPro Device Manager") }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            MetricsRow(total, assigned, sites)

            Spacer(Modifier.height(30.dp))

            Text("Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                DashboardButton(
                    text = "Scan Nearby Devices",
                    icon = Icons.Filled.Bluetooth,
                    onClick = onScanClick
                )

                DashboardButton(
                    text = "Saved Belts",
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = onSavedDevicesClick
                )

                DashboardButton(
                    text = "Site Management",
                    icon = Icons.Filled.LocationOn,
                    onClick = onSitesClick
                )
            }
        }
    }
}

@Composable
fun MetricsRow(total: Int, assigned: Int, sites: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MetricCard("Total Belts", total, Icons.Filled.Storage, Modifier.weight(1f))
        MetricCard("Assigned", assigned, Icons.Filled.Bluetooth, Modifier.weight(1f))
        MetricCard("Sites", sites, Icons.Filled.LocationOn, Modifier.weight(1f))
    }
}

@Composable
fun MetricCard(
    label: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DashboardButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(icon, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

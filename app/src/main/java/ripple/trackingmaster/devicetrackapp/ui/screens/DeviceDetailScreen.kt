//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
//
//@Composable
//fun DeviceDetailScreen(
//    mac: String,
//    vm: DeviceDetailViewModel = hiltViewModel()
//) {
//    LaunchedEffect(mac) { vm.init(mac) }
//
//    val state by vm.uiState.collectAsState()
//    val sites by vm.sites.collectAsState()
//
//    var showAssignSheet by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Device Details") }) }
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(24.dp)
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.Start
//        ) {
//
//            Text("MAC: ${state.mac}", style = MaterialTheme.typography.titleMedium)
//
//            Spacer(Modifier.height(20.dp))
//
//            if (!state.isConnected) {
//                Button(
//                    onClick = vm::connect,
//                    modifier = Modifier.fillMaxWidth()
//                ) { Text(if (state.isConnecting) "Connecting..." else "Connect") }
//            } else {
//                Button(
//                    onClick = vm::disconnect,
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.error
//                    )
//                ) { Text("Disconnect") }
//
//                Spacer(Modifier.height(20.dp))
//
//                Text("Live Sensor Data:", style = MaterialTheme.typography.titleMedium)
//                Spacer(Modifier.height(10.dp))
//                Text("Left Pitch: ${state.lPitch ?: "--"}°")
//                Text("Right Pitch: ${state.rPitch ?: "--"}°")
//                Text("Center Pitch: ${state.cPitch ?: "--"}°")
//            }
//
//            Spacer(Modifier.height(32.dp))
//
//            Button(
//                onClick = { showAssignSheet = true },
//                modifier = Modifier.fillMaxWidth()
//            ) { Text("Assign to Site") }
//
//            if (showAssignSheet) {
//                AssignSiteSheet(
//                    sites = sites,
//                    onSelect = { selectedSiteId ->
//                        vm.assignToSite(selectedSiteId)
//                        showAssignSheet = false
//                    },
//                    onDismiss = { showAssignSheet = false }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun AssignSiteSheet(
//    sites: List<SiteEntity>,
//    onSelect: (Int) -> Unit,
//    onDismiss: () -> Unit
//) {
//    ModalBottomSheet(onDismissRequest = onDismiss) {
//        Column(Modifier.padding(16.dp)) {
//            Text("Assign to Site", style = MaterialTheme.typography.titleLarge)
//            Spacer(Modifier.height(16.dp))
//
//            if (sites.isEmpty()) {
//                Text("No sites yet. Create one from Dashboard → Site Management.")
//                Spacer(Modifier.height(12.dp))
//            } else {
//                LazyColumn {
//                    items(sites.size) { idx ->
//                        val site = sites[idx]
//                        Card(
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 6.dp)
//                                .clickable { onSelect(site.id) }
//                        ) {
//                            Column(Modifier.padding(16.dp)) {
//                                Text(site.siteName, style = MaterialTheme.typography.titleMedium)
//                                site.location?.let {
//                                    Text(it, style = MaterialTheme.typography.bodySmall)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity

@Composable
fun DeviceDetailScreen(
    mac: String,
    vm: DeviceDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { vm.init(mac) }

    val state by vm.uiState.collectAsState()
    val sites by vm.sites.collectAsState()

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Device Details") }) }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            // Header
            DeviceHeaderSection(state)

            Spacer(Modifier.height(20.dp))

            // Connect/Disconnect
            ConnectionButtons(state, vm)

            Spacer(Modifier.height(30.dp))

            // Live sensor only when connected
            if (state.isConnected) {
                LiveSensorSection(state)
            }

            Spacer(Modifier.height(40.dp))

            // Assign
            Button(
                onClick = { showSheet = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Assign to Site") }

            if (showSheet) {
                AssignSiteSheet(
                    sites = sites,
                    onSelect = { siteId ->
                        vm.assignToSite(siteId)
                        showSheet = false
                    },
                    onDismiss = { showSheet = false }
                )
            }
        }
    }
}

@Composable
private fun DeviceHeaderSection(state: DeviceDetailUiState) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "HipPro Belt",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text("MAC: ${state.mac}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = null,
                    tint = if (state.isConnected) Color(0xFF4CAF50) else Color.Red
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    when {
                        state.isConnected -> "Connected"
                        state.isConnecting -> "Connecting…"
                        else -> "Disconnected"
                    },
                    color = if (state.isConnected) Color(0xFF4CAF50) else Color.Red
                )
            }
        }
    }
}

@Composable
private fun ConnectionButtons(
    state: DeviceDetailUiState,
    vm: DeviceDetailViewModel
) {
    if (!state.isConnected) {
        Button(
            onClick = vm::connect,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isConnecting
        ) {
            if (state.isConnecting) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Connecting…")
            } else {
                Text("Connect")
            }
        }
    } else {
        Button(
            onClick = vm::disconnect,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Filled.Close, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Disconnect")
        }
    }
}

@Composable
private fun LiveSensorSection(state: DeviceDetailUiState) {
    Text("Live Sensor Data", style = MaterialTheme.typography.titleMedium)

    Spacer(Modifier.height(16.dp))

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SensorCard("Left Pitch", state.lPitch)
        SensorCard("Right Pitch", state.rPitch)
        SensorCard("Center Pitch", state.cPitch)
    }
}

@Composable
private fun SensorCard(label: String, value: Float?) {
    Card(
        Modifier.size(width = 110.dp, height = 80.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))
            Text(
                value?.let { "%.1f°".format(it) } ?: "--",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun AssignSiteSheet(
    sites: List<SiteEntity>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp)) {
            Text("Assign to Site", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(20.dp))

            LazyColumn {
                items(sites.size) { idx ->
                    val site = sites[idx]
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onSelect(site.id) },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(site.siteName, style = MaterialTheme.typography.titleMedium)
                            site.location?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun SiteDetailScreen(
//    siteId: Int,
//    onDeviceClick: (String) -> Unit,
//    vm: SiteDetailViewModel = hiltViewModel()
//) {
//    LaunchedEffect(siteId) { vm.load(siteId) }
//
//    val devices by vm.devices.collectAsState()
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Site Details") }) }
//    ) { pad ->
//        Column(Modifier.padding(pad).padding(16.dp)) {
//            Text("Assigned Devices", style = MaterialTheme.typography.titleMedium)
//            Spacer(Modifier.height(16.dp))
//
//            LazyColumn {
//                items(devices.size) { idx ->
//                    val d = devices[idx]
//                    Card(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 6.dp)
//                            .clickable { onDeviceClick(d.mac) }
//                    ) {
//                        Column(Modifier.padding(16.dp)) {
//                            Text(d.name ?: "HipPro Belt")
//                            Text("MAC: ${d.mac}")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SiteDetailScreen(
    siteId: Int,
    onDeviceClick: (String) -> Unit,
    onBack: () -> Unit = {},
    vm: SiteDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(siteId) {
        vm.load(siteId)
    }

    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.site?.siteName ?: "Site Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize()
        ) {

            // ✅ header
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(state.site?.siteName ?: "", style = MaterialTheme.typography.titleLarge)
                    state.site?.location?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${state.devices.size} devices assigned",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Assigned Devices", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            if (state.devices.isEmpty()) {
                Text("No devices assigned.")
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.devices.size) { i ->
                    val d = state.devices[i]

                    DeviceRow(
                        name = d.name ?: "HipPro Belt",
                        mac = d.mac,
                        onOpen = { onDeviceClick(d.mac) },
                        onUnassign = { vm.unassign(d.mac) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceRow(
    name: String,
    mac: String,
    onOpen: () -> Unit,
    onUnassign: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth().clickable { onOpen() }
    ) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text("MAC: $mac", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = onUnassign) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import android.annotation.SuppressLint
//import android.bluetooth.BluetoothDevice
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//
//@Composable
//fun ClassicScanScreen(
//    onDeviceSelected: (String) -> Unit,
//    vm: ClassicScanViewModel = hiltViewModel()
//) {
//    val state by vm.state.collectAsState()
//
//    LaunchedEffect(Unit) {
//        vm.loadPairedDevices()
//        vm.startDiscovery()
//    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Select Device") },
//                actions = {
//                    if (!state.isScanning) {
//                        TextButton(onClick = { vm.startDiscovery() }) { Text("Scan") }
//                    }
//                }
//            )
//        }
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//                .fillMaxSize()
//        ) {
//
//            // ---- Paired Devices ----
//            Text("Paired Devices", style = MaterialTheme.typography.titleMedium)
//            DeviceList(
//                devices = state.paired,
//                onClick = { onDeviceSelected(it.address) }
//            )
//
//            Spacer(Modifier.height(24.dp))
//
//            // ---- Discovered Devices ----
//            Text("Discovered Devices", style = MaterialTheme.typography.titleMedium)
//            if (state.isScanning) {
//                LinearProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                )
//            }
//
//            DeviceList(
//                devices = state.discovered,
//                onClick = { onDeviceSelected(it.address) }
//            )
//        }
//    }
//}
//
//@SuppressLint("MissingPermission")
//@Composable
//fun DeviceList(devices: List<BluetoothDevice>, onClick: (BluetoothDevice) -> Unit) {
//    LazyColumn {
//        items(devices.size) { idx ->
//            val d = devices[idx]
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 6.dp)
//                    .clickable { onClick(d) },
//                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
//            ) {
//                Column(Modifier.padding(16.dp)) {
//                    Text(d.name ?: "Unknown", style = MaterialTheme.typography.bodyLarge)
//                    Text(d.address, style = MaterialTheme.typography.bodySmall)
//                }
//            }
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ClassicScanScreen(
    onDeviceSelected: (String) -> Unit,
    vm: ClassicScanViewModel = hiltViewModel()
) {
    val devices by vm.devices.collectAsState()
    val isScanning by vm.isScanning.collectAsState()

    LaunchedEffect(Unit) {
        vm.startScan()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan Devices") },
                actions = {
                    IconButton(onClick = vm::startScan) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rescan")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            if (isScanning) {
                ScanningHeader()
                Spacer(Modifier.height(20.dp))
            }

            if (!isScanning && devices.isEmpty()) {
                EmptyScanState(onRetry = vm::startScan)
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(devices.size) { idx ->
                    val dev = devices[idx]
                    DeviceCard(
                        name = dev.name ?: "Unknown",
                        mac = dev.mac,
                        onClick = { onDeviceSelected(dev.mac) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScanningHeader() {
    val trans = rememberInfiniteTransition()
    val scale by trans.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Filled.BluetoothSearching,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scale).size(50.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text("Scanning for devices…", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DeviceCard(name: String, mac: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(mac, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EmptyScanState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No devices found", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Scan Again")
        }
    }
}

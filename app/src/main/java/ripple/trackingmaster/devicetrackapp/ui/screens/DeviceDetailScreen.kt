package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class) // Needed for ModalBottomSheet
@Composable
fun DeviceDetailScreen(
    mac: String,
    navController: NavController, // ✅ NEW: Get NavController
    vm: DeviceDetailViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    val sites by vm.sites.collectAsState()
    val connState by vm.connectionState.collectAsState()

    // ✅ NEW: Get new states from VM
    val saveButtonText by vm.saveButtonText.collectAsState()
    val assignedSiteId by vm.assignedSiteId.collectAsState()

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Device Details") }) }
    ) { padding ->

        LazyColumn(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            item {
                DeviceHeaderSection(state, connState)
                Spacer(Modifier.height(20.dp))
            }

            item {
                OutlinedTextField(
                    value = state.customName ?: "",
                    onValueChange = { vm.updateName(it) },
                    label = { Text("Device Name") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) }
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.beltNumber?.toString() ?: "",
                    onValueChange = { vm.updateBeltNumber(it) },
                    label = { Text("Belt Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.beltSize ?: "",
                    onValueChange = { vm.updateBeltSize(it) },
                    label = { Text("Belt Size") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(Modifier.height(16.dp))

                // ✅ UPDATED SAVE BUTTON
                Button(
                    onClick = { vm.saveDeviceDetails() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    // Disable button when saving/saved
                    enabled = saveButtonText == "Save Details",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (saveButtonText == "Saved!") {
                            Color(0xFF4CAF50) // Green color
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(saveButtonText)
                    if (saveButtonText == "Saved!") {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.Check, contentDescription = "Saved")
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                ConnectionButtons(connState, vm)
                Spacer(Modifier.height(30.dp))
            }

            item {
                // ✅ UPDATED ASSIGN BUTTON
                val assignedSite = sites.find { it.id == assignedSiteId }
                Button(
                    onClick = { showSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (assignedSite != null) {
                            Color(0xFF4CAF50) // Green
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    if (assignedSite != null) {
                        Icon(Icons.Filled.Check, contentDescription = "Assigned")
                        Spacer(Modifier.width(8.dp))
                        Text("Assigned to: ${assignedSite.siteName}")
                    } else {
                        Text("Assign to Site")
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        } // <-- END OF LAZYCOLUMN

        if (showSheet) {
            // ✅ PASS ALL NEW PARAMETERS TO THE SHEET
            AssignSiteSheet(
                sites = sites,
                assignedSiteId = assignedSiteId,
                onSelect = { siteId ->
                    vm.assignToSite(siteId)
                    showSheet = false
                },
                onUnassign = {
                    vm.unassignFromSite()
                    showSheet = false
                },
                onCreateNew = {
                    showSheet = false
                    navController.navigate("createSite")
                },
                onDismiss = { showSheet = false }
            )
        }
    } // <-- END OF SCAFFOLD
}

@Composable
fun DeviceHeaderSection(state: DeviceDetailUiState, connState: ConnectionState) {
    // (This composable is unchanged)
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "HipPro Belt Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text("Serial No: ${state.serialNumber ?: "--"}", style = MaterialTheme.typography.bodyMedium)
            Text("MAC: ${state.mac}", style = MaterialTheme.typography.bodyMedium)
            Text("Last Seen: ${state.lastSeenStatus ?: "--"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val isConnected = connState == ConnectionState.CONNECTED
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = null,
                    tint = if (isConnected) Color(0xFF4CAF50) else Color.Red
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    when (connState) {
                        ConnectionState.CONNECTED -> "Connected"
                        ConnectionState.CONNECTING -> "Connecting…"
                        ConnectionState.FAILED -> "Failed"
                        ConnectionState.DISCONNECTED -> "Disconnected"
                    },
                    color = if (isConnected) Color(0xFF4CAF50) else Color.Red
                )
            }
        }
    }
}

@Composable
fun ConnectionButtons(
    connState: ConnectionState,
    vm: DeviceDetailViewModel
) {
    // (This composable is unchanged)
    if (connState != ConnectionState.CONNECTED) {
        Button(
            onClick = vm::connect,
            modifier = Modifier.fillMaxWidth(),
            enabled = (connState != ConnectionState.CONNECTING)
        ) {
            if (connState == ConnectionState.CONNECTING) {
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

// ✅ NEW, FULLY UPDATED BOTTOM SHEET COMPOSABLE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignSiteSheet(
    sites: List<SiteEntity>,
    assignedSiteId: Int?,
    onSelect: (Int) -> Unit,
    onUnassign: () -> Unit,
    onCreateNew: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .padding(20.dp)
                .navigationBarsPadding() // Add padding for gesture nav
        ) {
            Text("Assign to Site", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(20.dp))

            // --- Check if site list is empty ---
            if (sites.isEmpty()) {
                Text(
                    "No sites found. Create a new site to assign this device.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = onCreateNew,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create New Site")
                }
            } else {
                // --- Show list of sites ---
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(sites.size) { idx ->
                        val site = sites[idx]
                        val isAssigned = site.id == assignedSiteId

                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onSelect(site.id) },
                            shape = RoundedCornerShape(16.dp),
                            // Highlight the assigned site
                            colors = if (isAssigned) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            } else {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(site.siteName, style = MaterialTheme.typography.titleMedium)
                                    site.location?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (isAssigned) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Assigned",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // --- Show Unassign button if a site is assigned ---
                if (assignedSiteId != null) {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onUnassign,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Unassign Device")
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
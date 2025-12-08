package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
import ripple.trackingmaster.devicetrackapp.domain.model.ConnectionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    vm: DeviceDetailViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var showSiteSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen for Save/Error Messages
    LaunchedEffect(state.saveMessage, state.errorMessage) {
        if (state.saveMessage != null) {
            snackbarHostState.showSnackbar(state.saveMessage!!)
            vm.clearMessages()
        }
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage!!)
            vm.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Device Tracker") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. STATUS CARD ---
            item {
                DeviceStatusCard(state = state, onGetSerial = { vm.fetchSerialNumber() })
            }

            // --- 2. DETAILS FORM ---
            item {
                Text(
                    "Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 8.dp)
                )

                // ✅ Team Member DROPDOWN
                TeamMemberDropdown(
                    options = state.teamMemberOptions,
                    selectedOption = state.teamMember,
                    onOptionSelected = { vm.updateTeamMember(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Belt Size Input
                OutlinedTextField(
                    value = state.beltSize,
                    onValueChange = { vm.updateBeltSize(it) },
                    label = { Text("Belt Size") },
                    leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // --- 3. SITE ASSIGNMENT ---
            item {
                SiteAssignmentCard(
                    assignedSiteName = state.assignedSite?.siteName,
                    onClick = { showSiteSheet = true }
                )
            }

            // --- 4. ACTION BUTTONS ---
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { vm.saveDetails() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Details", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ConnectionButton(
                    state = state.connectionState,
                    onConnect = vm::connect,
                    onDisconnect = vm::disconnect
                )
            }
        }
    }

    if (showSiteSheet) {
        SiteSelectionSheet(
            sites = state.availableSites,
            onSelect = {
                vm.assignSite(it)
                showSiteSheet = false
            },
            onDismiss = { showSiteSheet = false }
        )
    }
}

// --- COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamMemberDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = { },
            label = { Text("Team Member") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceStatusCard(state: DeviceDetailUiState, onGetSerial: () -> Unit) {
    val isConnected = state.connectionState == ConnectionState.CONNECTED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = if (isConnected) Color(0xFF4CAF50) else Color(0xFFEF5350)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(state.macAddress, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Serial: ${state.serialNumber}", style = MaterialTheme.typography.bodyMedium)
                if (isConnected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onGetSerial) { Text("Get Serial") }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = if (isConnected) Color(0xFF4CAF50) else Color.Gray,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = state.connectionState.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun SiteAssignmentCard(assignedSiteName: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Assigned Site", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                Text(
                    text = assignedSiteName ?: "Tap to assign site",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun ConnectionButton(state: ConnectionState, onConnect: () -> Unit, onDisconnect: () -> Unit) {
    val isConnected = state == ConnectionState.CONNECTED
    OutlinedButton(
        onClick = if (isConnected) onDisconnect else onConnect,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isConnected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (state == ConnectionState.CONNECTING) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Connecting...")
        } else {
            Text(if (isConnected) "Disconnect Device" else "Connect Device")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteSelectionSheet(
    sites: List<SiteEntity>,
    onSelect: (SiteEntity) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp).padding(bottom = 24.dp)) {
            Text("Select Site", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if(sites.isEmpty()) {
                Text("No sites available. Go to 'Clinical Sites' on Dashboard to create one (e.g., Apollo, Fortis).")
            } else {
                LazyColumn {
                    items(sites.size) { i ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(sites[i]) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(sites[i].siteName, fontSize = 16.sp)
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
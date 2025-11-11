//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import android.os.Build
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.google.accompanist.permissions.*
//
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun PermissionsScreen(onContinue: () -> Unit) {
//
//    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        listOf(
//            android.Manifest.permission.BLUETOOTH_CONNECT,
//            android.Manifest.permission.BLUETOOTH_SCAN
//        )
//    } else listOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
//
//    val permissionState = rememberMultiplePermissionsState(permissions)
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(title = { Text("Permissions Required") })
//        }
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(24.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Text(
//                "Bluetooth Access",
//                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
//            )
//
//            Spacer(Modifier.height(16.dp))
//
//            Text(
//                "HipPro needs Bluetooth permissions to connect and read sensor data.",
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(Modifier.height(32.dp))
//
//            Button(
//                onClick = { permissionState.launchMultiplePermissionRequest() },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Grant Permissions")
//            }
//
//            Spacer(Modifier.height(18.dp))
//
//            val granted = permissionState.permissions.all { it.status.isGranted }
//            if (granted) {
//                Button(
//                    onClick = onContinue,
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.secondary
//                    )
//                ) {
//                    Text("Continue")
//                }
//            }
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.*
import ripple.trackingmaster.devicetrackapp.ui.screens.permissions.PermissionViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    onContinue: () -> Unit,
    vm: PermissionViewModel = hiltViewModel()
) {
    val storedGranted by vm.hasPermissions.collectAsState()

    // ✅ If stored value says permissions were already granted → skip screen
    LaunchedEffect(storedGranted) {
        if (storedGranted) onContinue()
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        listOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val permissionState = rememberMultiplePermissionsState(permissions)

    val runtimeGranted = permissionState.permissions.all { it.status.isGranted }

    // ✅ When user grants runtime permissions → save to DataStore
    LaunchedEffect(runtimeGranted) {
        if (runtimeGranted) {
            vm.setGranted()
            onContinue()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Permissions Required") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Bluetooth Access",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "HipPro needs Bluetooth permissions to connect and read sensor data.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { permissionState.launchMultiplePermissionRequest() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !runtimeGranted
            ) {
                Text(
                    if (runtimeGranted || storedGranted)
                        "✅ Permissions Granted"
                    else "Grant Permissions"
                )
            }
        }
    }
}

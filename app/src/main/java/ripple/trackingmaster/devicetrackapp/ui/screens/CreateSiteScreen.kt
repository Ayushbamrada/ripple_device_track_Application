//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//
//@Composable
//fun CreateSiteScreen(
//    onCreated: () -> Unit,
//    vm: SitesViewModel = hiltViewModel()
//) {
//    var name by remember { mutableStateOf("") }
//    var location by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Create Site") }) }
//    ) { pad ->
//
//        Column(
//            Modifier
//                .padding(pad)
//                .padding(24.dp)
//        ) {
//
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Site Name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = location,
//                onValueChange = { location = it },
//                label = { Text("Location (Optional)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(24.dp))
//
//            Button(
//                onClick = {
//                    vm.createSite(name, location.ifBlank { null })
//                    onCreated()
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Create")
//            }
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateSiteScreen(
    onCreated: () -> Unit,
    vm: CreateSiteViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Create Site") })
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Site Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    vm.createSite(name, location.ifBlank { null })
                    onCreated()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create")
            }
        }
    }
}

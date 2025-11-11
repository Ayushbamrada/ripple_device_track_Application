//package ripple.trackingmaster.devicetrackapp.ui.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import ripple.trackingmaster.devicetrackapp.data.local.entity.SiteEntity
//
//@Composable
//fun SitesScreen(
//    onSiteSelected: (Int) -> Unit,
//    onCreateSiteClick: () -> Unit,
//    vm: SitesViewModel = hiltViewModel()
//) {
//    val sites by vm.sites.collectAsState()
//
//    Scaffold(
//        topBar = { CenterAlignedTopAppBar(title = { Text("Sites / Hospitals") }) },
//        floatingActionButton = {
//            FloatingActionButton(onClick = onCreateSiteClick) {
//                Text("+")
//            }
//        }
//    ) { pad ->
//
//        Column(
//            Modifier
//                .padding(pad)
//                .padding(16.dp)
//        ) {
//
//            LazyColumn {
//                items(sites.size) { idx ->
//                    SiteCard(
//                        site = sites[idx],
//                        onClick = { onSiteSelected(sites[idx].id) }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SiteCard(site: SiteEntity, onClick: () -> Unit) {
//    Card(
//        Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable { onClick() },
//        elevation = CardDefaults.cardElevation(3.dp)
//    ) {
//        Column(Modifier.padding(16.dp)) {
//            Text(site.siteName, style = MaterialTheme.typography.titleMedium)
//            site.location?.let {
//                Text(it, style = MaterialTheme.typography.bodySmall)
//            }
//        }
//    }
//}
package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SitesScreen(
    onSiteSelected: (Int) -> Unit,
    onCreateSiteClick: () -> Unit,
    vm: SitesViewModel = hiltViewModel()
) {
    val sites by vm.sites.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sites") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateSiteClick
            ) {
                Icon(Icons.Default.Add, "Create Site")
            }
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (sites.isEmpty()) {
                Text(
                    "No sites created yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
                return@Column
            }

            LazyColumn {
                items(sites.size) { idx ->
                    val s = sites[idx]

                    SiteCard(
                        name = s.siteName,
                        location = s.location,
                        deviceCount = s.deviceCount,
                        onClick = { onSiteSelected(s.id) }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SiteCard(
    name: String,
    location: String?,
    deviceCount: Int,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(name, style = MaterialTheme.typography.titleLarge)

            location?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "$deviceCount devices assigned",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

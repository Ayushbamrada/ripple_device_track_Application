package ripple.trackingmaster.devicetrackapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SitesScreen(
    onSiteSelected: (Int) -> Unit,
    onCreateSiteClick: () -> Unit,
    vm: SitesViewModel = hiltViewModel()
) {
    // Get the new UI state
    val sitesWithCount by vm.sitesWithCount.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Clinical Sites") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateSiteClick) {
                Icon(Icons.Filled.Add, contentDescription = "Create Site")
            }
        }
    ) { padding ->

        if (sitesWithCount.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No sites created. Tap the '+' button to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sitesWithCount, key = { it.site.id }) { siteData ->
                    SiteRow(
                        siteName = siteData.site.siteName,
                        location = siteData.site.location,
                        deviceCount = siteData.deviceCount,
                        onClick = { onSiteSelected(siteData.site.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SiteRow(
    siteName: String,
    location: String?,
    deviceCount: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(siteName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (!location.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(location, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                "$deviceCount Devices",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
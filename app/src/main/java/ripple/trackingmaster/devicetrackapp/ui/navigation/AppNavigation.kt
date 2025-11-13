package ripple.trackingmaster.devicetrackapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ripple.trackingmaster.devicetrackapp.ui.screens.*
import ripple.trackingmaster.devicetrackapp.ui.screens.permissions.PermissionViewModel

@Composable
fun AppNavigation() {

    val nav = rememberNavController()

    val vm: PermissionViewModel = hiltViewModel()

    val hasPermissions by vm.hasPermissions.collectAsState(initial = false)

    NavHost(
        navController = nav,
        startDestination = if (hasPermissions) "dashboard" else "permissions"
    )
    {

        composable("permissions") {
            PermissionsScreen(onContinue = { nav.navigate("dashboard") })
        }

        composable("dashboard") {
            DashboardScreen(
                onScanClick = { nav.navigate("scan") },
                onSavedDevicesClick = { nav.navigate("savedDevices") },
                onSitesClick = { nav.navigate("sites") }
            )
        }

        composable("scan") {
            ClassicScanScreen(onDeviceSelected = { mac ->
                nav.navigate("device/$mac")
            })
        }

        composable("savedDevices") {
            SavedDevicesScreen(
                onDeviceSelected = { mac -> nav.navigate("device/$mac") }
            )
        }

//        composable("device/{mac}") { backStack ->
//            val mac = backStack.arguments?.getString("mac") ?: ""
//            DeviceDetailScreen(mac = mac)
//        }

        composable("device/{mac}") { backStack ->
            val mac = backStack.arguments?.getString("mac") ?: ""
            // ✅ PASS THE NAVCONTROLLER HERE
            DeviceDetailScreen(
                mac = mac,
                navController = nav
            )
        }

        composable("sites") {
            SitesScreen(
                onSiteSelected = { nav.navigate("siteDetail/$it") },
                onCreateSiteClick = { nav.navigate("createSite") }
            )
        }

        composable("createSite") {
            CreateSiteScreen(onCreated = { nav.popBackStack() })
        }

        composable("siteDetail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toInt() ?: 0
            SiteDetailScreen(
                siteId = id,
                onDeviceClick = { mac -> nav.navigate("device/$mac") }
            )
        }
    }
}

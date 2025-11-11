package ripple.trackingmaster.devicetrackapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColors = lightColorScheme(
    primary = Color(0xFF005AC1),
    onPrimary = Color.White,
    secondary = Color(0xFF006D3B),
    onSecondary = Color.White,
    background = Color(0xFFF7F9FC),
    surface = Color.White
)

private val DarkColors = darkColorScheme()

@Composable
fun HipProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val sysUi = rememberSystemUiController()

    sysUi.setStatusBarColor(colorScheme.primary, darkIcons = !darkTheme)
    sysUi.setNavigationBarColor(colorScheme.surface, darkIcons = !darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

package dz.pharmstockdz.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// DZ-2026 shared direction: Material3 dynamic color, seeded fallback below
// Android 12 (teal #006A60 / #4DDAD0, error #BA1A1A / #FFB4AB).
private val LightFallback = lightColorScheme(
    primary = Color(0xFF006A60),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9EF2E4),
    onPrimaryContainer = Color(0xFF00201C),
    error = Color(0xFFBA1A1A)
)
private val DarkFallback = darkColorScheme(
    primary = Color(0xFF4DDAD0),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF004F47),
    onPrimaryContainer = Color(0xFF9EF2E4),
    error = Color(0xFFFFB4AB)
)

@Composable
fun PharmStockDZTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkFallback
        else -> LightFallback
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

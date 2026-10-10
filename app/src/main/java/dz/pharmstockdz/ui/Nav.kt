package dz.pharmstockdz.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dz.pharmstockdz.PharmStockApp
import dz.pharmstockdz.R
import dz.pharmstockdz.ui.screens.DashboardScreen
import dz.pharmstockdz.ui.screens.EditProductScreen
import dz.pharmstockdz.ui.screens.ProductDetailScreen
import dz.pharmstockdz.ui.screens.SettingsScreen
import dz.pharmstockdz.ui.screens.StockScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val STOCK = "stock"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{productId}"
    const val EDIT = "edit?productId={productId}"

    fun detail(productId: Long) = "detail/$productId"
    fun edit(productId: Long? = null) =
        if (productId == null) "edit" else "edit?productId=$productId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {
    val app = LocalContext.current.applicationContext as PharmStockApp
    val vm: StockViewModel = viewModel(factory = StockViewModel.Factory(app.repository))
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    if (route == Routes.STOCK) {
                        IconButton(onClick = { nav.navigate(Routes.edit()) }) {
                            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_product))
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = route == Routes.DASHBOARD,
                    onClick = { nav.navigate(Routes.DASHBOARD) { popUpTo(Routes.DASHBOARD) } },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_dashboard)) }
                )
                NavigationBarItem(
                    selected = route == Routes.STOCK,
                    onClick = { nav.navigate(Routes.STOCK) { popUpTo(Routes.DASHBOARD) } },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_stock)) }
                )
                NavigationBarItem(
                    selected = route == Routes.SETTINGS,
                    onClick = { nav.navigate(Routes.SETTINGS) { popUpTo(Routes.DASHBOARD) } },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) }
                )
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(inner)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(vm, onOpenProduct = { nav.navigate(Routes.detail(it)) })
            }
            composable(Routes.STOCK) {
                StockScreen(
                    vm,
                    onOpenProduct = { nav.navigate(Routes.detail(it)) },
                    onAddProduct = { nav.navigate(Routes.edit()) }
                )
            }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(
                Routes.DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { entry ->
                ProductDetailScreen(vm, entry.arguments!!.getLong("productId"), onBack = { nav.popBackStack() })
            }
            composable(
                Routes.EDIT,
                arguments = listOf(navArgument("productId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { entry ->
                val id = entry.arguments!!.getLong("productId").takeIf { it >= 0 }
                EditProductScreen(vm, id, onDone = { nav.popBackStack() })
            }
        }
    }
}

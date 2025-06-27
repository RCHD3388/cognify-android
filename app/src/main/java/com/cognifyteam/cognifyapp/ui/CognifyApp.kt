package com.cognifyteam.cognifyapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cognifyteam.cognifyapp.data.AppContainer
import com.cognifyteam.cognifyapp.ui.navigation.AppBottomNavItem
import com.cognifyteam.cognifyapp.ui.navigation.AppNavGraph
import kotlinx.coroutines.launch

data class FabState(
    val isVisible: Boolean = false,
    val icon: ImageVector = Icons.Filled.Add,
    val description: String = "Add",
    val onClick: () -> Unit = {}
)

// --- State BARU untuk mengelola TopAppBar di MainAppScreen ---
data class TopBarState(
    val isVisible: Boolean = false, // <-- Ubah default menjadi 'true' jika sebagian besar layar punya TopBar, atau 'false' jika kebanyakan tidak punya
    val title: String = "",
    val navigationIcon: (@Composable () -> Unit)? = null,
    val actions: (@Composable () -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainScreen(
    appContainer: AppContainer,
) {
    val navController = rememberNavController()
    val items = listOf(AppBottomNavItem.Home, AppBottomNavItem.Smart, AppBottomNavItem.Profile)

    var fabState by remember { mutableStateOf(FabState()) }
    var topBarState by remember { mutableStateOf(TopBarState()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (topBarState.isVisible) {
                CenterAlignedTopAppBar(
                    title = { Text(topBarState.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = { topBarState.navigationIcon?.invoke() },
                    actions = { topBarState.actions?.invoke() },
//                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.primaryContainer,
//                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    )
                )
            }
        },
        floatingActionButton = {
            if (fabState.isVisible) {
                FloatingActionButton(onClick = fabState.onClick) {
                    Icon(fabState.icon, fabState.description)
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(item.icon), contentDescription = null) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            AppNavGraph(
                navController = navController,
                appContainer = appContainer,
                onFabStateChange = { newState -> fabState = newState },
                onTopBarStateChange = { newState -> topBarState = newState },
                onShowSnackbar = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }
    }
}
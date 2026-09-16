package com.krithi.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.krithi.ui.theme.BackgroundDark
import com.krithi.ui.theme.PrimaryAccent
import com.krithi.ui.theme.SecondaryTextDark
import com.krithi.ui.theme.SurfaceVariantDark

sealed class NavDestination(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavDestination("home", "Home", Icons.Default.Home)
    object Library : NavDestination("library", "Library", Icons.Default.List)
    object Search : NavDestination("search", "Search", Icons.Default.Search)
}

val bottomNavItems = listOf(NavDestination.Home, NavDestination.Library, NavDestination.Search)

@Composable
fun KrithiBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = BackgroundDark
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryAccent,
                    unselectedIconColor = SecondaryTextDark,
                    selectedTextColor = PrimaryAccent,
                    unselectedTextColor = SecondaryTextDark,
                    indicatorColor = SurfaceVariantDark
                )
            )
        }
    }
}

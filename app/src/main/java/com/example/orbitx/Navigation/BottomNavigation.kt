package com.example.orbitx.Navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.orbitx.R


@Composable
fun BottomNavigationBar(navController: NavController, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        BottomNavigationItem(
            title = "home",
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "search",
            selectedIcon = Icons.Default.Search,
            unselectedIcon = Icons.Outlined.Search,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "newpost",
            selectedIcon = Icons.Default.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircle,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "profile",
            selectedIcon = Icons.Default.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "logout",
            selectedIcon = Icons.Default.ExitToApp,
            unselectedIcon = Icons.Outlined.ExitToApp,
            hasNews = false
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = Modifier.height(80.dp), containerColor = colorResource(id = R.color.orange)) {
        items.forEachIndexed { index, item ->
            val isSelected = currentRoute == item.title

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute == item.title) {
                        navController.popBackStack(item.title, inclusive = false)
                    } else {
                        onItemSelected(index)
                        navController.navigate(item.title) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    BadgedBox(badge = {}) {
                        Icon(
                            imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null
                        )
                    }
                },
                label = {}
            )
        }
    }
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    val routesWithoutBottomBar = listOf("chats", "MainChatScreen/{data}", "logout")
    return !routesWithoutBottomBar.contains(currentRoute)
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

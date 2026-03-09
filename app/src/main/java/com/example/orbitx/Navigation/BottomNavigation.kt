package com.example.orbitx.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavController, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        BottomNavigationItem("home", Icons.Outlined.Home),
        BottomNavigationItem("search", Icons.Outlined.Tv),
        BottomNavigationItem("newpost", Icons.Outlined.ChatBubbleOutline),
        BottomNavigationItem("notifications", Icons.Outlined.Notifications),
        BottomNavigationItem("profile", Icons.Outlined.Person),
        BottomNavigationItem("logout", Icons.Outlined.ExitToApp)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .background(Color.Transparent)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            color = Color.White,
            shape = RoundedCornerShape(34.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = currentRoute == item.title
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (item.title == "notifications") return@clickable // Dummy route
                                
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
                            }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color(0xFF8A72B8) else Color(0xFF2D3748),
                            modifier = Modifier.size(28.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(3.dp)
                                    .background(Color(0xFF8A72B8), RoundedCornerShape(1.5.dp))
                            )
                        } else {
                            // Maintain consistent height avoiding jumping layout
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }
                }
            }
        }
    }
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    val routesWithoutBottomBar = listOf("chats", "MainChatScreen/{data}", "logout")
    return !routesWithoutBottomBar.contains(currentRoute)
}

data class BottomNavigationItem(
    val title: String,
    val icon: ImageVector
)

package com.d_shield_parent.Dashboard

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d_shield_parent.Profile.ProfileScreen
import com.d_shield_parent.SharedPreference.shareprefManager
import com.d_shield_parent.presentation.auth.HomeScreen


sealed class navItem(
    val tittle: String,
    val icon: ImageVector){
    object Home : navItem("Home", Icons.Default.Home)
    object List : navItem("List", Icons.Default.List)
    object Service: navItem("Service", Icons.Default.DesignServices)
    object History: navItem("History", Icons.Default.History)

    object Profile: navItem("Profile", Icons.Default.Person)
    object Retailers : navItem("Retailers", Icons.Default.People)
}
@Composable
fun MainScreen(navController: NavController) {

    // get saved user type from SharedPreferences
    val userType = shareprefManager.getUserType() // "RETAILER" or "DISTRIBUTOR"
    val isDistributor = userType == "DISTRIBUTOR"

    var selectedIndex = remember { mutableStateOf(0) }

    val profileViewModel: ProfileViewModel = viewModel()
    LaunchedEffect(Unit) {
        profileViewModel.fetchProfile()
    }

    BackHandler {
        if (selectedIndex.value != 0) {
            // if not on home tab, go back to home tab
            selectedIndex.value = 0
        } else {
            // if already on home tab, exit app — don't go to log in
            (navController.context as? Activity)?.finish()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemClick = { selectedIndex.value = it },
                isDistributor = isDistributor
            )
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding)) {

            if (isDistributor) {
                // Distributor tabs
                when (selectedIndex.value) {
                    0 -> HomeScreen(navController = navController)
                    1 -> ListScreen(navController = navController)
                    2 -> ProfileScreen(navController = navController)
                }
            } else {
                // Retailer tabs — existing screens
                when (selectedIndex.value) {
                    0  -> HomeScreen(navController = navController, viewModel = profileViewModel)
                    1 -> ListScreen(navController = navController)
//                    2 -> ServiceScreen(navController = navController)
                    2 -> HistoryScreen(navController = navController)
                    3 -> ProfileScreen(navController = navController)
                }
            }
        }
    }
}


@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    isDistributor: Boolean = false   // ← add this
) {
    // different tabs per user type
    val items = if (isDistributor) {
        listOf(
            navItem.Home,
            navItem.Retailers,
            navItem.Profile
        )
    } else {
        listOf(
            navItem.Home,
            navItem.List,
//         navItem.Service,
            navItem.History,
            navItem.Profile
        )
    }

    NavigationBar(containerColor = Color(0xFFD4AF37)) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemClick(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        tint = Color.White,
                        contentDescription = item.tittle
                    )
                },
                label = { Text(item.tittle, color = Color.White) }
            )
        }
    }
}



package com.example.ballighandroidapp.approot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ballighandroidapp.features.auth.RoleSelectionScreen
import com.example.ballighandroidapp.features.auth.login.view.LoginScreen
import com.example.ballighandroidapp.features.auth.register.view.RegisterScreen
import com.example.ballighandroidapp.features.onboardingScreens.OnboardingScreen
import com.example.ballighandroidapp.features.splashScreen.SplashScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenHomeScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenReportsScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenAccountScreen
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenMainViewModel
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.ui.theme.Primary

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object RoleSelection : Screen("role_selection")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
}

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavScreen("home_tab", "Home", Icons.Default.Home)
    object Reports : BottomNavScreen("reports_tab", "Reports", Icons.Outlined.Assignment)
    object Account : BottomNavScreen("account_tab", "Account", Icons.Default.Person)
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences(context) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    when {
                        appPreferences.isFirstTimeLaunch -> {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        appPreferences.isUserLoggedIn -> {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    appPreferences.isFirstTimeLaunch = false
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { roleId ->
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    appPreferences.isUserLoggedIn = true
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPassword = {}
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    appPreferences.isUserLoggedIn = true
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            CitizenDashboard(onLogout = {
                appPreferences.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun CitizenDashboard(onLogout: () -> Unit) {
    val dashboardNavController = rememberNavController()
    val viewModel: CitizenMainViewModel = hiltViewModel()
    
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Reports,
        BottomNavScreen.Account
    )

    Scaffold(
        containerColor = Color(0xFFF1F4F9), // Professional light background
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.height(80.dp)
                ) {
                    val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { 
                                Box(
                                    modifier = Modifier
                                        .size(width = 64.dp, height = 32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selected) Primary.copy(alpha = 0.15f) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screen.icon, 
                                        contentDescription = null, 
                                        tint = if (selected) Primary else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = { 
                                Text(
                                    text = screen.title, 
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Primary else Color.Gray
                                ) 
                            },
                            selected = selected,
                            onClick = {
                                dashboardNavController.navigate(screen.route) {
                                    popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            dashboardNavController, 
            startDestination = BottomNavScreen.Home.route, 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) { 
                CitizenHomeScreen(
                    viewModel = viewModel, 
                    onReportClick = { /* Navigate to Create Report screen */ },
                    onViewAllReports = { dashboardNavController.navigate(BottomNavScreen.Reports.route) }
                ) 
            }
            composable(BottomNavScreen.Reports.route) { 
                CitizenReportsScreen(viewModel = viewModel) 
            }
            composable(BottomNavScreen.Account.route) { 
                CitizenAccountScreen(onLogout = onLogout) 
            }
        }
    }
}

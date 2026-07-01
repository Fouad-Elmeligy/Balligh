package com.example.ballighandroidapp.approot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.features.auth.login.view.LoginScreen
import com.example.ballighandroidapp.features.auth.register.view.RegisterScreen
import com.example.ballighandroidapp.features.onboardingScreens.OnboardingScreen
import com.example.ballighandroidapp.features.splashScreen.SplashScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenHomeScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenReportsScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenAccountScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenEditProfileScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenAddReportScreen
import com.example.ballighandroidapp.features.citizen.view.CitizenNotificationScreen
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenMainViewModel
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenAccountViewModel
import com.example.ballighandroidapp.features.citizen.viewmodel.NotificationViewModel
import com.example.ballighandroidapp.helpers.local.AppPreferences
import com.example.ballighandroidapp.ui.theme.Primary

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object EditProfile : Screen("edit_profile")
    object AddReport : Screen("add_report?reportId={reportId}") {
        fun createRoute(reportId: Int? = null) = "add_report?reportId=${reportId ?: -1}"
    }
    object Notifications : Screen("notifications")
}

sealed class BottomNavScreen(val route: String, val title: Int, val icon: ImageVector) {
    object Home : BottomNavScreen("home_tab", R.string.nav_home, Icons.Default.Home)
    object Reports : BottomNavScreen("reports_tab", R.string.nav_reports, Icons.Outlined.Assignment)
    object Account : BottomNavScreen("account_tab", R.string.nav_my_account, Icons.Default.Person)
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
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
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
            val userRole = appPreferences.currentUserRole

            val handleLogout = {
                appPreferences.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                }
            }

            when (userRole) {
                3 -> {
                    AdminDashboardScreen(onLogout = handleLogout)
                }
                2 -> {
                    EmployeeDashboardScreen(onLogout = handleLogout)
                }
                else -> {
                    val accountViewModel: CitizenAccountViewModel = hiltViewModel()
                    LaunchedEffect(Unit) {
                        accountViewModel.prepareEdit()
                    }
                    CitizenDashboard(
                        accountViewModel = accountViewModel,
                        onEditProfile = { navController.navigate(Screen.EditProfile.route) },
                        onAddReport = { reportId ->
                            navController.navigate(Screen.AddReport.createRoute(reportId))
                        },
                        onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                        onLogout = handleLogout
                    )
                }
            }
        }

        composable(Screen.EditProfile.route) {
            val dashboardEntry = remember(navController) { navController.getBackStackEntry(Screen.Dashboard.route) }
            val accountViewModel: CitizenAccountViewModel = hiltViewModel(dashboardEntry)

            CitizenEditProfileScreen(
                viewModel = accountViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddReport.route,
            arguments = listOf(
                navArgument("reportId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            CitizenAddReportScreen(
                onBackClick = { navController.popBackStack() },
                onReportSent = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            val notificationViewModel: NotificationViewModel = hiltViewModel()
            CitizenNotificationScreen(
                viewModel = notificationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun AdminDashboardScreen(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("مرحباً بك في لوحة تحكم الأدمن 🛡️", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("تسجيل الخروج", color = Color.White)
            }
        }
    }
}

@Composable
fun EmployeeDashboardScreen(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("مرحباً بك في لوحة تحكم الموظف 💼", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("تسجيل الخروج", color = Color.White)
            }
        }
    }
}

@Composable
fun CitizenDashboard(
    accountViewModel: CitizenAccountViewModel,
    onEditProfile: () -> Unit,
    onAddReport: (Int?) -> Unit,
    onNotificationsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val dashboardNavController = rememberNavController()
    val viewModel: CitizenMainViewModel = hiltViewModel()

    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Reports,
        BottomNavScreen.Account
    )

    val selectedPillColor = Color(0xFFD3E3DC)
    val selectedContentColor = Color(0xFF1B4332)
    val unselectedContentColor = Color(0xFF6B6B6B)

    Scaffold(
        containerColor = Color(0xFFF1F4F9),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logodark),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.app_name),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Primary.copy(alpha = 0.08f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .navigationBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (selected) selectedPillColor else Color.Transparent)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        dashboardNavController.navigate(screen.route) {
                                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (selected) selectedContentColor else unselectedContentColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(id = screen.title),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) selectedContentColor else unselectedContentColor
                                )
                            }
                        }
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
                    onReportClick = { onAddReport(null) },
                    onReportItemClick = { reportId -> onAddReport(reportId) },
                    onViewAllReports = {
                        dashboardNavController.navigate(BottomNavScreen.Reports.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomNavScreen.Reports.route) {
                CitizenReportsScreen(
                    viewModel = viewModel,
                    onReportClick = { reportId -> onAddReport(reportId) }
                )
            }
            composable(BottomNavScreen.Account.route) {
                CitizenAccountScreen(
                    viewModel = accountViewModel,
                    onEditProfile = onEditProfile,
                    onLogout = onLogout
                )
            }
        }
    }
}
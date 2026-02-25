package com.riku.kisanmitra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.riku.kisanmitra.domain.model.UserRole
import com.riku.kisanmitra.ui.MainViewModel
import com.riku.kisanmitra.ui.screens.auth.*
import com.riku.kisanmitra.ui.screens.buyer.*
import com.riku.kisanmitra.ui.screens.driver.*
import com.riku.kisanmitra.ui.screens.farmer.*
import com.riku.kisanmitra.ui.screens.trader.*

@Composable
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val selectedRole by mainViewModel.selectedRole.collectAsState()
    val loggedInUser by mainViewModel.loggedInUser.collectAsState()
    
    val startDestination = when {
        loggedInUser == null -> Screen.LanguageSelection.route
        selectedRole == UserRole.FARMER -> Screen.FarmerDashboard.route
        selectedRole == UserRole.BUYER -> Screen.BuyerDashboard.route
        selectedRole == UserRole.DRIVER -> Screen.DriverDashboard.route
        selectedRole == UserRole.TRADER -> Screen.TraderDashboard.route
        else -> Screen.LanguageSelection.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                onLanguageSelected = { lang ->
                    mainViewModel.selectLanguage(lang)
                    navController.navigate(Screen.RoleSelection.route)
                }
            )
        }
        
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    mainViewModel.selectRole(role)
                    navController.navigate(Screen.Login.createRoute(role.name))
                }
            )
        }

        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val roleName = backStackEntry.arguments?.getString("role") ?: UserRole.BUYER.name
            val role = UserRole.valueOf(roleName)
            LoginScreen(
                role = role,
                onLoginSuccess = {
                    val route = when (role) {
                        UserRole.FARMER -> Screen.FarmerDashboard.route
                        UserRole.BUYER -> Screen.BuyerDashboard.route
                        UserRole.DRIVER -> Screen.DriverDashboard.route
                        UserRole.TRADER -> Screen.TraderDashboard.route
                        else -> Screen.BuyerDashboard.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.createRoute(roleName))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val roleName = backStackEntry.arguments?.getString("role") ?: UserRole.BUYER.name
            val role = UserRole.valueOf(roleName)
            RegisterScreen(
                role = role,
                onRegisterSuccess = {
                    val route = when (role) {
                        UserRole.FARMER -> Screen.FarmerDashboard.route
                        UserRole.BUYER -> Screen.BuyerDashboard.route
                        UserRole.DRIVER -> Screen.DriverDashboard.route
                        UserRole.TRADER -> Screen.TraderDashboard.route
                        else -> Screen.BuyerDashboard.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.createRoute(roleName)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Farmer Module
        composable(Screen.FarmerDashboard.route) {
            FarmerDashboardScreen(navController)
        }
        composable(Screen.CreateListing.route) {
            CreateListingScreen(navController)
        }
        composable(Screen.FarmerOrderStatus.route) {
            FarmerOrderStatusScreen(navController)
        }
        composable(Screen.TransportBooking.route) {
            TransportBookingScreen(navController)
        }
        composable(Screen.FarmerEarnings.route) {
            FarmerEarningsScreen(navController)
        }
        
        // Buyer Module
        composable(Screen.BuyerDashboard.route) {
            BuyerDashboardScreen(navController)
        }
        composable(Screen.BrowseListings.route) {
            BrowseListingsScreen(navController)
        }
        composable(
            route = Screen.ListingDetail.route,
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            ListingDetailScreen(navController, listingId)
        }
        composable(Screen.BuyerOrderTracking.route) {
            BuyerOrderTrackingScreen(navController)
        }
        
        // Driver Module
        composable(Screen.DriverDashboard.route) {
            DriverDashboardScreen(navController)
        }
        composable(Screen.ActiveTrip.route) {
            ActiveTripScreen(navController)
        }
        composable(Screen.DriverEarnings.route) {
            DriverEarningsScreen(navController)
        }

        // Trader Module
        composable(Screen.TraderDashboard.route) {
            TraderDashboardScreen(navController)
        }
    }
}

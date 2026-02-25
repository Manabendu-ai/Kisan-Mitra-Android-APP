package com.riku.kisanmitra.ui.screens.trader

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.riku.kisanmitra.navigation.Screen
import com.riku.kisanmitra.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraderDashboardScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trader Coordinator", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { 
                        mainViewModel.logout()
                        navController.navigate(Screen.LanguageSelection.route) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Trader Dashboard", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Coordinator Model - Managing supply chain matches",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Feature coming soon for high-volume logistics matching.")
        }
    }
}

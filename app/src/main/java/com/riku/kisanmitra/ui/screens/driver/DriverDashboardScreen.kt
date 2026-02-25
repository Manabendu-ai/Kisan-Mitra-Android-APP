package com.riku.kisanmitra.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.riku.kisanmitra.domain.model.Trip
import com.riku.kisanmitra.domain.model.TripStatus
import com.riku.kisanmitra.navigation.Screen
import com.riku.kisanmitra.ui.MainViewModel
import com.riku.kisanmitra.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    viewModel: DriverViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val tripsState by viewModel.tripsState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    var showAcceptDialog by remember { mutableStateOf<Trip?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isOnline) "Online" else "Offline", style = MaterialTheme.typography.bodySmall)
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { viewModel.toggleOnlineStatus() },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
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
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Feed, contentDescription = null) },
                    label = { Text("Trips") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.DriverEarnings.route) },
                    icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    label = { Text("Earnings") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Fixed Feed", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("On-demand", modifier = Modifier.padding(16.dp))
                }
            }

            if (!isOnline) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Go online to see available trips", color = Color.Gray)
                }
            } else {
                when (val state = tripsState) {
                    is UiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is UiState.Success -> {
                        val availableTrips = state.data.filter { it.status == TripStatus.AVAILABLE }
                        if (availableTrips.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No trips available right now")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(availableTrips) { trip ->
                                    TripCard(trip) {
                                        showAcceptDialog = trip
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> Text("Error")
                }
            }
        }
        
        // Confirmation Dialog
        showAcceptDialog?.let { trip ->
            AlertDialog(
                onDismissRequest = { showAcceptDialog = null },
                title = { Text("Accept Trip?") },
                text = { Text("Are you sure you want to accept this trip from ${trip.pickupLocation} to ${trip.dropLocation} for ₹${trip.earnings}?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.acceptTrip(trip.id)
                        showAcceptDialog = null
                        navController.navigate(Screen.ActiveTrip.route)
                    }) {
                        Text("Accept")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAcceptDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun TripCard(trip: Trip, onAccept: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Earnings: ₹${trip.earnings}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                Badge { Text("${trip.weight} kg") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pickup: ${trip.pickupLocation}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Drop: ${trip.dropLocation}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${trip.distance} km", style = MaterialTheme.typography.bodySmall)
                Row {
                    OutlinedButton(onClick = { /* Skip */ }) { Text("Skip") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAccept) { Text("Accept") }
                }
            }
        }
    }
}

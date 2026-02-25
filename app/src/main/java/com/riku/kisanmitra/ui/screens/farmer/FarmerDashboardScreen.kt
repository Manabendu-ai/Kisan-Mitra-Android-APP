package com.riku.kisanmitra.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.riku.kisanmitra.domain.model.Listing
import com.riku.kisanmitra.domain.model.Order
import com.riku.kisanmitra.navigation.Screen
import com.riku.kisanmitra.ui.MainViewModel
import com.riku.kisanmitra.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDashboardScreen(
    navController: NavController,
    viewModel: FarmerViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val listingsState by viewModel.listingsState.collectAsState()
    val ordersState by viewModel.ordersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Dashboard", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.CreateListing.route) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Sell Crops") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EarningsSummaryCard(onViewEarnings = { navController.navigate(Screen.FarmerEarnings.route) })
            }

            item {
                SectionHeader(title = "Your Active Listings", onSeeAll = {})
            }

            when (val state = listingsState) {
                is UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Success -> {
                    val sortedListings = state.data.sortedByDescending { it.id }
                    items(sortedListings) { listing ->
                        ListingItem(listing)
                    }
                }
                is UiState.Error -> item { Text("Error: ${state.message}") }
            }

            item {
                SectionHeader(title = "Order Status tracker", onSeeAll = {})
            }

            when (val state = ordersState) {
                is UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Success -> {
                    items(state.data) { order ->
                        OrderStatusCard(order) {
                            navController.navigate(Screen.FarmerOrderStatus.route)
                        }
                    }
                }
                is UiState.Error -> item { Text("Error: ${state.message}") }
            }
        }
    }
}

@Composable
fun EarningsSummaryCard(onViewEarnings: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Earnings", style = MaterialTheme.typography.titleSmall)
            Text("₹ 45,280", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onViewEarnings, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("View Details")
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeAll) {
            Text("See All")
        }
    }
}

@Composable
fun ListingItem(listing: Listing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = listing.displayImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.cropName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${listing.quantity} kg @ ₹${listing.pricePerKg}/kg", style = MaterialTheme.typography.bodySmall)
            }
            Text("Active", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusCard(order: Order, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Order #${order.id.takeLast(4)}", fontWeight = FontWeight.Bold)
                Text(order.status.name.replace("_", " "), style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

package com.riku.kisanmitra.ui.screens.farmer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.riku.kisanmitra.R
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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.firstpage),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("Farmer Dashboard", fontWeight = FontWeight.Bold, color = Color.White) },
                        actions = {
                            IconButton(onClick = {
                                mainViewModel.logout()
                                navController.navigate(Screen.LanguageSelection.route) {
                                    popUpTo(0)
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.CreateListing.route) },
                        icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                        text = { Text("Sell Crops", color = Color.White) },
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        EarningsSummaryCard(onViewEarnings = { navController.navigate(Screen.FarmerEarnings.route) })
                    }

                    item {
                        SectionHeader(title = "Your Active Listings", onSeeAll = {})
                    }

                    when (val state = listingsState) {
                        is UiState.Loading -> item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.Success -> {
                            if (state.data.isEmpty()) {
                                item {
                                    Text(
                                        "You have no active listings. Tap 'Sell Crops' to create one.",
                                        color = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                val sortedListings = state.data.sortedByDescending { it.id }
                                items(sortedListings) { listing ->
                                    ListingItem(listing)
                                }
                            }
                        }
                        is UiState.Error -> item { Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error) }
                    }

                    item {
                        SectionHeader(title = "Order Status tracker", onSeeAll = {})
                    }

                    when (val state = ordersState) {
                        is UiState.Loading -> item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.Success -> {
                             if (state.data.isEmpty()) {
                                item {
                                    Text(
                                        "You have no recent orders.",
                                        color = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                items(state.data) { order ->
                                    OrderStatusCard(order) {
                                        navController.navigate(Screen.FarmerOrderStatus.route)
                                    }
                                }
                            }
                        }
                        is UiState.Error -> item { Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }
}

@Composable
fun EarningsSummaryCard(onViewEarnings: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Earnings", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f))
            Text("₹ 45,280", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onViewEarnings,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    contentColor = Color.White
                )
            ) {
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
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        TextButton(onClick = onSeeAll) {
            Text("See All", color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ListingItem(listing: Listing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = listing.displayImage,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.cropName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${listing.quantity} kg @ ₹${listing.pricePerKg}/kg", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.width(8.dp))
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Order #${order.id.takeLast(4)}", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(order.status.name.replace("_", " "), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
        }
    }
}

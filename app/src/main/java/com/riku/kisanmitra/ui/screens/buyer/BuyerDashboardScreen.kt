package com.riku.kisanmitra.ui.screens.buyer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.riku.kisanmitra.domain.model.Crop
import com.riku.kisanmitra.navigation.Screen
import com.riku.kisanmitra.ui.MainViewModel
import com.riku.kisanmitra.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboardScreen(
    navController: NavController,
    viewModel: BuyerViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val livePricesState by viewModel.livePrices.collectAsState()
    val ordersState by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kisan Mitra Market", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SearchBar(onClick = { navController.navigate(Screen.BrowseListings.route) })
            }

            item {
                Text("Live Market Prices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                when (val state = livePricesState) {
                    is UiState.Loading -> CircularProgressIndicator()
                    is UiState.Success -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.data) { crop ->
                                PriceCard(crop)
                            }
                        }
                    }
                    is UiState.Error -> Text("Error loading prices")
                }
            }

            item {
                Text("Recent Orders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                when (val state = ordersState) {
                    is UiState.Loading -> CircularProgressIndicator()
                    is UiState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.data.forEach { order ->
                                OrderSummaryCard(order) {
                                    navController.navigate(Screen.BuyerOrderTracking.route)
                                }
                            }
                        }
                    }
                    is UiState.Error -> Text("Error loading orders")
                }
            }
            
            item {
                Button(
                    onClick = { navController.navigate(Screen.BrowseListings.route) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Browse All Listings")
                }
            }
        }
    }
}

@Composable
fun PriceCard(crop: Crop) {
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(crop.name, fontWeight = FontWeight.Bold)
            Text("₹${crop.currentMandiPrice}/kg", fontSize = 18.sp, fontWeight = FontWeight.Black)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (crop.priceTrend >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (crop.priceTrend >= 0) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${if (crop.priceTrend >= 0) "+" else ""}${crop.priceTrend}%",
                    color = if (crop.priceTrend >= 0) Color(0xFF4CAF50) else Color.Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text("High Demand", fontSize = 10.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Search crops, farmers...", color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryCard(order: com.riku.kisanmitra.domain.model.Order, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Order #${order.id.takeLast(4)}", fontWeight = FontWeight.Bold)
                Text("Status: ${order.status}", style = MaterialTheme.typography.bodySmall)
            }
            Text("₹${order.totalPrice}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

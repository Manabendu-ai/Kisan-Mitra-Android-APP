package com.riku.kisanmitra.ui.screens.trader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.riku.kisanmitra.domain.model.Listing
import com.riku.kisanmitra.domain.model.Order
import com.riku.kisanmitra.domain.model.Trip
import com.riku.kisanmitra.navigation.Screen
import com.riku.kisanmitra.ui.MainViewModel
import com.riku.kisanmitra.ui.screens.buyer.BuyerViewModel
import com.riku.kisanmitra.ui.screens.driver.DriverViewModel
import com.riku.kisanmitra.ui.screens.farmer.FarmerViewModel
import com.riku.kisanmitra.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraderDashboardScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    farmerViewModel: FarmerViewModel = hiltViewModel(),
    buyerViewModel: BuyerViewModel = hiltViewModel(),
    driverViewModel: DriverViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Buying", "Selling", "Transport", "Update")

    val listingsState by buyerViewModel.listings.collectAsState()
    val tripsState by driverViewModel.tripsState.collectAsState()
    val ordersState by farmerViewModel.ordersState.collectAsState()

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
        Column(modifier = Modifier.padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> BuyingTab(listingsState, navController)
                1 -> SellingTab(listingsState)
                2 -> TransportTab(tripsState)
                3 -> UpdateTab(listingsState, buyerViewModel)
            }
        }
    }
}

@Composable
fun BuyingTab(listingsState: UiState<List<Listing>>, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Source from Farmers", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        when (listingsState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listingsState.data) { listing ->
                        TraderListingCard(listing, "Buy Now") {
                             navController.navigate(Screen.ListingDetail.createRoute(listing.id))
                        }
                    }
                }
            }
            is UiState.Error -> Text("Error loading supply")
        }
    }
}

@Composable
fun SellingTab(listingsState: UiState<List<Listing>>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fulfill Buyer Orders", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        when (listingsState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listingsState.data.reversed()) { listing ->
                        TraderListingCard(listing, "Fulfill") { }
                    }
                }
            }
            is UiState.Error -> Text("Error loading orders")
        }
    }
}

@Composable
fun TransportTab(tripsState: UiState<List<Trip>>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Coordinate Logistics", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        when (tripsState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tripsState.data) { trip ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${trip.pickupLocation} → ${trip.dropLocation}", fontWeight = FontWeight.Bold)
                                    Text("${trip.weight}kg • ₹${trip.earnings}", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(onClick = {}) { Text("Assign") }
                            }
                        }
                    }
                }
            }
            is UiState.Error -> Text("Error loading transport")
        }
    }
}

@Composable
fun UpdateTab(listingsState: UiState<List<Listing>>, buyerViewModel: BuyerViewModel) {
    var showEditDialog by remember { mutableStateOf<Listing?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Update Vegetable Prices", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        when (listingsState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listingsState.data) { listing ->
                        TraderListingCard(listing, "Edit Price") {
                            showEditDialog = listing
                        }
                    }
                }
            }
            is UiState.Error -> Text("Error loading inventory")
        }
    }

    if (showEditDialog != null) {
        var newPrice by remember { mutableStateOf(showEditDialog?.pricePerKg.toString()) }
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Update Price for ${showEditDialog?.cropName}") },
            text = {
                OutlinedTextField(
                    value = newPrice,
                    onValueChange = { newPrice = it },
                    label = { Text("New Price per kg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceValue = newPrice.toDoubleOrNull()
                        if (priceValue != null && showEditDialog != null) {
                            buyerViewModel.updateListingPrice(showEditDialog!!.id, priceValue)
                            showEditDialog = null
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraderListingCard(listing: Listing, actionText: String, onAction: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onAction) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = listing.displayImage,
                contentDescription = null,
                modifier = Modifier.size(64.dp).background(Color.LightGray, MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.cropName, fontWeight = FontWeight.Bold)
                Text("${listing.quantity}kg • ₹${listing.pricePerKg}/kg", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onAction) { Text(actionText) }
        }
    }
}

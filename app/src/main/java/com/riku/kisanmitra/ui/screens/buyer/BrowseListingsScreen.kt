package com.riku.kisanmitra.ui.screens.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
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
import com.riku.kisanmitra.navigation.Screen
import com.riku.kisanmitra.ui.state.UiState
import com.riku.kisanmitra.ui.theme.TrustStar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseListingsScreen(
    navController: NavController,
    viewModel: BuyerViewModel = hiltViewModel()
) {
    val listingsState by viewModel.listings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Listings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Tune, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = listingsState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.data) { listing ->
                            ListingCard(listing) {
                                navController.navigate(Screen.ListingDetail.createRoute(listing.id))
                            }
                        }
                    }
                }
                is UiState.Error -> Text("Error loading listings")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingCard(listing: Listing, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, MaterialTheme.shapes.medium)
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
                Text("${listing.quantity} kg available", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = TrustStar, modifier = Modifier.size(16.dp))
                    Text(
                        " ${listing.farmerTrustScore} • Farmer Trust Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            Text("₹${listing.pricePerKg}/kg", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

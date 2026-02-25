package com.riku.kisanmitra.ui.screens.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.riku.kisanmitra.ui.state.UiState
import com.riku.kisanmitra.ui.theme.TrustStar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    navController: NavController,
    listingId: String,
    viewModel: BuyerViewModel = hiltViewModel()
) {
    val listingsState by viewModel.listings.collectAsState()
    val listing = (listingsState as? UiState.Success)?.data?.find { it.id == listingId }
    var quantityToBuy by remember { mutableStateOf("100") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listing Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (listing == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = listing.displayImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(listing.cropName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("₹${listing.pricePerKg}/kg", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = TrustStar)
                        Text(" ${listing.farmerTrustScore} Farmer Rating", fontWeight = FontWeight.Bold)
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    Text("Farmer Information", fontWeight = FontWeight.Bold)
                    Text("Manjunath Gowda • Mandya, Karnataka", color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Available Quantity: ${listing.quantity} kg", fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = quantityToBuy,
                        onValueChange = { quantityToBuy = it },
                        label = { Text("Quantity to Buy (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "Total Estimate: ₹${(quantityToBuy.toDoubleOrNull() ?: 0.0) * listing.pricePerKg}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            viewModel.placeOrder(listing, quantityToBuy.toDoubleOrNull() ?: 0.0)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Place Order", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

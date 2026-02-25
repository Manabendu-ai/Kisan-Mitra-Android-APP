package com.riku.kisanmitra.ui.screens.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerEarningsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earnings Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Withdrawable Balance", style = MaterialTheme.typography.titleSmall)
                        Text("₹ 12,450", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* Withdraw */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Withdraw to Bank")
                        }
                    }
                }
            }

            item {
                Text("Earnings Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            items(listOf("Tomato" to 5400.0, "Onion" to 3200.0, "Potato" to 3850.0)) { (crop, amount) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(crop)
                    Text("₹$amount", fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Transaction History", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            items(5) { index ->
                TransactionItem(index)
            }
        }
    }
}

@Composable
fun TransactionItem(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Order #KM772$index", fontWeight = FontWeight.Bold)
                Text("Nov 22, 2023", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text("+ ₹2,500", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        }
    }
}

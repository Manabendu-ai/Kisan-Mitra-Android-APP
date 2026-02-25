package com.riku.kisanmitra.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riku.kisanmitra.domain.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerOrderStatusScreen(navController: NavController) {
    val statuses = OrderStatus.values()
    val currentStatus = OrderStatus.TRANSPORT_BOOKED

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Tracking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Order #KM9821", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Crop: Tomato (500 kg)", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))

            statuses.forEachIndexed { index, status ->
                val isCompleted = index <= currentStatus.ordinal
                val isCurrent = index == currentStatus.ordinal
                
                TimelineItem(
                    title = status.name.replace("_", " "),
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLast = index == statuses.size - 1
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (currentStatus == OrderStatus.ORDER_RECEIVED) {
                Button(
                    onClick = { /* Navigate to Booking */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Book Transport")
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(if (isCompleted) MaterialTheme.colorScheme.primary else Color.Gray)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = title,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else if (isCompleted) Color.Black else Color.Gray
            )
            if (isCurrent) {
                Text("Updated 2 hours ago", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

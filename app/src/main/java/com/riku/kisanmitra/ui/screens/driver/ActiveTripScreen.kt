package com.riku.kisanmitra.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.riku.kisanmitra.ui.state.UiState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTripScreen(
    navController: NavController,
    viewModel: DriverViewModel = hiltViewModel()
) {
    val mandya = GeoPoint(12.5218, 76.8951)
    val mysuru = GeoPoint(12.2958, 76.6394)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Trip") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(12.0)
                            controller.setCenter(mandya)
                            
                            val pickupMarker = Marker(this).apply {
                                position = mandya
                                title = "Pickup: Mandya Farm"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(pickupMarker)

                            val dropMarker = Marker(this).apply {
                                position = mysuru
                                title = "Drop: Mysuru Hub"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(dropMarker)
                        }
                    }
                )
                
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("ETA: 45 mins", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text("12 km remaining", color = Color.Gray)
                            }
                            IconButton(onClick = { /* Open External Maps */ }) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        Text("Destination: Mysuru Hub", fontWeight = FontWeight.Bold)
                        Text("500kg Tomato • Earn: ₹1,200", style = MaterialTheme.typography.bodySmall)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                navController.popBackStack() 
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Mark as Delivered")
                        }
                    }
                }
            }
        }
    }
}

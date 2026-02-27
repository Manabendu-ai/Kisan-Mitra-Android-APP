package com.riku.kisanmitra.ui.screens.farmer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.riku.kisanmitra.R
import com.riku.kisanmitra.domain.model.AiPriceAdvice
import com.riku.kisanmitra.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    navController: NavController,
    viewModel: FarmerViewModel = hiltViewModel()
) {
    var cropType by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expectedPrice by remember { mutableStateOf("") }
    
    val aiAdviceState by viewModel.aiAdviceState.collectAsState()
    val scrollState = rememberScrollState()

    val vegetables = listOf(
        "cabbage", "cauliflower", "chilli", "capcicum", "carrot", "onion", "tomato", "potato"
    )
    var expanded by remember { mutableStateOf(false) }

    // Dynamic Voice Prompts based on selected language
    LaunchedEffect(Unit) {
        viewModel.speakPrompt(
            englishPrompt = "Welcome. Please select the crop type you want to sell from the list.",
            kannadaPrompt = "ಸ್ವಾಗತ. ನೀವು ಮಾರಾಟ ಮಾಡಲು ಬಯಸುವ ಬೆಳೆ ಪ್ರಕಾರವನ್ನು ಪಟ್ಟಿಯಿಂದ ಆಯ್ಕೆಮಾಡಿ."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post New Listing") },
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Crop Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = cropType.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Crop Type") },
                    leadingIcon = if (cropType.isNotEmpty()) {
                        {
                            Image(
                                painter = painterResource(id = getVegImage(cropType)),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else null,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .onFocusChanged { 
                            if (it.isFocused && cropType.isEmpty()) {
                                viewModel.speakPrompt(
                                    englishPrompt = "Please select a crop from the dropdown menu.",
                                    kannadaPrompt = "ದಯವಿಟ್ಟು ಡ್ರಾಪ್‌ಡೌನ್ ಮೆನುವಿನಿಂದ ಬೆಳೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ."
                                )
                            }
                        }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    vegetables.forEach { vegetable ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = getVegImage(vegetable)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            },
                            text = { Text(vegetable.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                cropType = vegetable
                                expanded = false
                                val vegCap = vegetable.replaceFirstChar { it.uppercase() }
                                viewModel.speakPrompt(
                                    englishPrompt = "You have selected $vegCap.",
                                    kannadaPrompt = "ನೀವು $vegCap ಅನ್ನು ಆಯ್ಕೆ ಮಾಡಿದ್ದೀರಿ."
                                )
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { 
                    quantity = it 
                },
                label = { Text("Quantity (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { 
                        if (it.isFocused && quantity.isEmpty()) {
                            viewModel.speakPrompt(
                                englishPrompt = "Please enter the quantity in kilograms.",
                                kannadaPrompt = "ದಯವಿಟ್ಟು ಪ್ರಮಾಣವನ್ನು ಕಿಲೋಗ್ರಾಂಗಳಲ್ಲಿ ನಮೂದಿಸಿ."
                            )
                        } else if (!it.isFocused && quantity.isNotEmpty()) {
                            // Announce quantity when moving away from the field
                            viewModel.speakPrompt(
                                englishPrompt = "Quantity set to $quantity kilograms.",
                                kannadaPrompt = "ಪ್ರಮಾಣವನ್ನು $quantity ಕಿಲೋಗ್ರಾಂಗಳಿಗೆ ನಿಗದಿಪಡಿಸಲಾಗಿದೆ."
                            )
                        }
                    }
            )

            var isPriceFocused by remember { mutableStateOf(false) }
            
            OutlinedTextField(
                value = expectedPrice,
                onValueChange = { expectedPrice = it },
                label = { Text("Expected Price (per kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { 
                        isPriceFocused = it.isFocused
                        if (it.isFocused) {
                            if (expectedPrice.isEmpty()) {
                                viewModel.speakPrompt(
                                    englishPrompt = "Please enter your expected price per kilogram.",
                                    kannadaPrompt = "ದಯವಿಟ್ಟು ಪ್ರತಿ ಕಿಲೋಗ್ರಾಂಗೆ ನಿಮ್ಮ ನಿರೀಕ್ಷಿತ ಬೆಲೆಯನ್ನು ನಮೂದಿಸಿ."
                                )
                            }
                            if (cropType.isNotEmpty()) {
                                viewModel.getPriceAdvice(cropType, quantity.toDoubleOrNull() ?: 0.0)
                            }
                        } else if (!it.isFocused && expectedPrice.isNotEmpty()) {
                            viewModel.speakPrompt(
                                englishPrompt = "Expected price set to $expectedPrice rupees per kilogram.",
                                kannadaPrompt = "ನಿರೀಕ್ಷಿತ ಬೆಲೆಯನ್ನು ಪ್ರತಿ ಕಿಲೋಗ್ರಾಂಗೆ $expectedPrice ರೂಪಾಯಿಗಳಿಗೆ ನಿಗದಿಪಡಿಸಲಾಗಿದೆ."
                            )
                        }
                    },
                trailingIcon = {
                    if (isPriceFocused) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
            
            if (isPriceFocused) {
                Text(
                    "AI is analyzing market trends for $cropType...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // AI Price Suggestion Card
            aiAdviceState?.let { state ->
                when (state) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    is UiState.Success -> AiAdviceCard(state.data)
                    is UiState.Error -> Text("Failed to load AI advice", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.postListing(
                        cropType,
                        quantity.toDoubleOrNull() ?: 0.0,
                        expectedPrice.toDoubleOrNull() ?: 0.0,
                        emptyList() // Images handled by the ViewModel/Model based on cropType
                    ) {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = cropType.isNotEmpty() && quantity.isNotEmpty() && expectedPrice.isNotEmpty()
            ) {
                Text("Post Listing", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AiAdviceCard(advice: AiPriceAdvice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Price Insights", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PriceStat("Current", "₹${advice.currentPrice}")
                PriceStat("24h Prediction", "₹${advice.predicted24h}")
                PriceStat("48h Prediction", "₹${advice.predicted48h}")
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Recommendation: ${advice.recommendation}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = advice.reasonText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PriceStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

fun getVegImage(name: String): Int {
    return when (name.lowercase()) {
        "cabbage" -> R.drawable.cabbage
        "cauliflower" -> R.drawable.cauliflower
        "chilli" -> R.drawable.chilli
        "capcicum" -> R.drawable.capcicum
        "carrot" -> R.drawable.carrot
        "onion" -> R.drawable.onion
        "tomato" -> R.drawable.tomato
        "potato" -> R.drawable.potato
        else -> R.drawable.kisan_mitra_logo
    }
}

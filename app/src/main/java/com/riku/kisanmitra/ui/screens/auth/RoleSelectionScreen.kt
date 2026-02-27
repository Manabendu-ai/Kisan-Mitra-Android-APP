package com.riku.kisanmitra.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.riku.kisanmitra.R
import com.riku.kisanmitra.domain.model.UserRole

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit
) {
    val roles = listOf(
        RoleItem(stringResource(R.string.role_farmer), Icons.Default.Agriculture, UserRole.FARMER, stringResource(R.string.role_farmer_desc)),
        RoleItem(stringResource(R.string.role_buyer), Icons.Default.Storefront, UserRole.BUYER, stringResource(R.string.role_buyer_desc)),
        RoleItem(stringResource(R.string.role_driver), Icons.Default.LocalShipping, UserRole.DRIVER, stringResource(R.string.role_driver_desc)),
        RoleItem(stringResource(R.string.role_trader), Icons.Default.Person, UserRole.TRADER, stringResource(R.string.role_trader_desc))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = stringResource(R.string.choose_role),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.select_role_desc),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(roles) { role ->
                RoleCard(role) { onRoleSelected(role.role) }
            }
        }
    }
}

data class RoleItem(
    val title: String,
    val icon: ImageVector,
    val role: UserRole,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleCard(role: RoleItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = role.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = role.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = role.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

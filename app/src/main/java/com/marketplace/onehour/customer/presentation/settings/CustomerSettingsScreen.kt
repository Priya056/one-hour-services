package com.marketplace.onehour.customer.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.marketplace.onehour.BuildConfig
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.CustomerBottomNavBar
import com.marketplace.onehour.common.theme.SuccessGreen
import com.marketplace.onehour.common.update.AppUpdateChecker

@Composable
fun CustomerSettingsScreen(
    onSwitchToHelperMode: () -> Unit,
    onLogout: () -> Unit,
    onNavigateBottom: (String) -> Unit,
    viewModel: CustomerSettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(title = "Profile & Settings")
        },
        bottomBar = {
            CustomerBottomNavBar(
                currentRoute = "customer_settings",
                onNavigate = onNavigateBottom
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Profile Card Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        AsyncImage(
                            model = state.avatarUrl,
                            contentDescription = state.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        IconButton(
                            onClick = { /* Edit Avatar */ },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = state.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = state.phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(text = state.email, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Switch to Helper Mode Card Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchToHelperMode() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WorkOutline, contentDescription = null, tint = Color(0xFF38BDF8))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Become a 1-Hour Helper", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "Earn money by helping neighbors near you", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = onSwitchToHelperMode,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Switch", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Saved Addresses Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Saved Service Addresses", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        IconButton(onClick = { /* Add address */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Address", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    state.addresses.forEach { addr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (addr.label == "Home") Icons.Default.Home else Icons.Default.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (addr.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = SuccessGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                            Text(text = "DEFAULT", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(text = addr.fullAddress, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                            }
                            RadioButton(
                                selected = addr.isDefault,
                                onClick = { viewModel.setDefaultAddress(addr.id) }
                            )
                        }
                    }
                }
            }

            // Account & Preferences List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    SettingsRowItem(icon = Icons.Default.Payment, title = "Payment Methods", subtitle = "UPI, Cards & Wallets", onClick = {})
                    Divider(color = Color.Gray.copy(alpha = 0.1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Push Notifications", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "Booking updates & helper ETA alerts", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Switch(
                            checked = state.notificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications(it) }
                        )
                    }
                    Divider(color = Color.Gray.copy(alpha = 0.1f))
                    SettingsRowItem(icon = Icons.Default.Language, title = "App Language", subtitle = state.selectedLanguage, onClick = {})
                    Divider(color = Color.Gray.copy(alpha = 0.1f))
                    SettingsRowItem(icon = Icons.Default.HelpOutline, title = "Help & Support", subtitle = "FAQs, Live Chat & Contact Us", onClick = {})
                    Divider(color = Color.Gray.copy(alpha = 0.1f))
                    SettingsRowItem(icon = Icons.Default.Description, title = "Terms & Privacy Policy", subtitle = "Legal terms & privacy rights", onClick = {})
                }
            }

            // Check for Update — debug builds only, never shows in a real
            // client release. See common.update.AppUpdateChecker.
            if (BuildConfig.DEBUG) {
                var updateStatus by remember { mutableStateOf<String?>(null) }
                var isCheckingUpdate by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isCheckingUpdate) {
                                    isCheckingUpdate = true
                                    updateStatus = null
                                    AppUpdateChecker.checkForUpdate(
                                        onNoUpdateAvailable = {
                                            isCheckingUpdate = false
                                            updateStatus = "You're on the latest build."
                                        },
                                        onError = { message ->
                                            isCheckingUpdate = false
                                            updateStatus = message
                                        }
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Check for Update", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(text = "Dev build v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            if (isCheckingUpdate) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                            }
                        }
                        if (updateStatus != null) {
                            Text(
                                text = updateStatus!!,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log Out", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

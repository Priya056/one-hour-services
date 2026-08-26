package com.marketplace.onehour.customer.presentation.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    helperId: Int,
    onBackClick: () -> Unit,
    onProceedToPayment: (bookingId: Int) -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(helperId) {
        viewModel.loadHelper(helperId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Configure 1-Hour Booking",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TOTAL AMOUNT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${"%.2f".format(state.totalAmount)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    PrimaryButton(
                        text = if (state.isCreatingBooking) "Creating Booking..." else "Proceed to Payment",
                        onClick = { 
                            if (!state.isCreatingBooking) {
                                viewModel.createBooking { bookingId ->
                                    onProceedToPayment(bookingId)
                                }
                            }
                        },
                        enabled = !state.isCreatingBooking,
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (state.helper == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val helper = state.helper!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Helper Quick Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = helper.photoUrl,
                            contentDescription = helper.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = helper.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "1-Hour ${helper.mainCategory} Service", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                // Booking Mode Selector
                Column {
                    Text(text = "Booking Mode", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        BookingModeTab(
                            title = "Book Instant",
                            subtitle = "Arrive in ~15 mins",
                            icon = Icons.Default.Bolt,
                            isSelected = state.isInstantBooking,
                            onClick = { viewModel.setInstantBooking(true) },
                            modifier = Modifier.weight(1f)
                        )
                        BookingModeTab(
                            title = "Schedule Later",
                            subtitle = "Pick date & time",
                            icon = Icons.Default.CalendarMonth,
                            isSelected = !state.isInstantBooking,
                            onClick = { viewModel.setInstantBooking(false) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Schedule Details (Visible if Schedule Later)
                AnimatedVisibility(visible = !state.isInstantBooking) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Text(text = "Select Date", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Today, Aug 24", "Tomorrow, Aug 25", "Wed, Aug 26").forEach { dateStr ->
                                    val isSel = state.selectedDate == dateStr
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.selectDate(dateStr) },
                                        label = { Text(dateStr, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        Column {
                            Text(text = "Select 1-Hour Slot", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("02:00 - 03:00 PM", "04:00 - 05:00 PM", "06:00 - 07:00 PM").forEach { slot ->
                                    val isSel = state.selectedTimeSlot == slot
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.selectTimeSlot(slot) },
                                        label = { Text(slot, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Service Address Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Service Location", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { }) {
                            Text("Change Address", fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = state.selectedAddress, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }

                // Special Notes / Instructions
                Column {
                    Text(text = "Instructions for Helper (Optional)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.specialInstructions,
                        onValueChange = { viewModel.onInstructionsChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Please bring extra 10A socket replacements") },
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }

                // Price Summary Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Booking Price Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                        PriceRow(label = "1-Hour Helper Fee", amount = state.baseHourlyFee)
                        PriceRow(label = "Platform & Safety Fee (10%)", amount = state.platformFee)
                        PriceRow(label = "Taxes & Charges (5%)", amount = state.taxAmount)
                        Divider(color = Color.Gray.copy(alpha = 0.2f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Payable Amount", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(
                                text = "$${"%.2f".format(state.totalAmount)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BookingModeTab(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
            }
            Text(subtitle, fontSize = 10.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray)
        }
    }
}

@Composable
private fun PriceRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = "$${"%.2f".format(amount)}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

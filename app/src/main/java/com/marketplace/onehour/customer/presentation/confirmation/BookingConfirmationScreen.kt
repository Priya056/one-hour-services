package com.marketplace.onehour.customer.presentation.confirmation

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.theme.SuccessGreen

@Composable
fun BookingConfirmationScreen(
    bookingId: String,
    onTrackLiveBooking: (bookingId: String) -> Unit,
    onBackToHome: () -> Unit,
    viewModel: ConfirmationViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadBookingConfirmation(bookingId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))

            // Celebration Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = SuccessGreen,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Booking Confirmed!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Ref: ${state.bookingReferenceCode}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ETA Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = "ESTIMATED ARRIVAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = state.estimatedEta, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assigned Helper Summary Card
            if (state.helper != null) {
                val helper = state.helper!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Assigned Helper", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = helper.photoUrl,
                                contentDescription = helper.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = helper.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = helper.mainCategory, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                            Row {
                                FilledTonalIconButton(onClick = { /* Call Helper */ }) {
                                    Icon(Icons.Default.Call, contentDescription = "Call")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                FilledTonalIconButton(onClick = { /* Chat Helper */ }) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chat")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Booking Details Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Service Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Divider(color = Color.Gray.copy(alpha = 0.2f))
                    DetailRow(label = "Duration", value = "Exactly 1 Hour (60 Mins)")
                    DetailRow(label = "Address", value = state.serviceAddress)
                    DetailRow(label = "Total Paid", value = "$${"%.2f".format(state.totalPaid)} (Paid via UPI)")
                }
            }
        }

        // Action Buttons Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                text = "Track Live Booking Progress",
                onClick = { onTrackLiveBooking(state.bookingId) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onBackToHome) {
                Text(text = "Back to Home", fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

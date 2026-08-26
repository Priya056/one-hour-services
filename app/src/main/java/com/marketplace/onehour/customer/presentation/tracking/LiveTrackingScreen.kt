package com.marketplace.onehour.customer.presentation.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
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
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.BookingStatusStepper
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.placeholders.MapsPlaceholder
import com.marketplace.onehour.common.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTrackingScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    onOpenChat: (bookingId: String, helperId: String) -> Unit,
    onRateReview: (bookingId: String) -> Unit,
    viewModel: TrackingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadTrackingDetails(bookingId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Live Booking Tracking",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { viewModel.advanceStatusSimulated() }) {
                        Icon(Icons.Default.Autorenew, contentDescription = "Simulate Status Advance")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Map View (Top 45% of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
            ) {
                MapsPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    label = "Live GPS Tracking — Helper '${state.helper?.name ?: "Alex"}' Status: ${state.status.title}"
                )

                // Current Status Badge Overlay
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Status: ${state.status.title.uppercase()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Bottom Tracking Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Assigned Helper Header
                    if (state.helper != null) {
                        val helper = state.helper!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
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
                                Column {
                                    Text(text = helper.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "1-Hour ${helper.mainCategory}", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                            }

                            Row {
                                FilledTonalIconButton(onClick = { /* Call helper */ }) {
                                    Icon(Icons.Default.Call, contentDescription = "Call")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                FilledTonalIconButton(onClick = { onOpenChat(state.bookingId, helper.id) }) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chat")
                                }
                            }
                        }
                    }

                    Divider(color = Color.Gray.copy(alpha = 0.2f))

                    // 5-Stage Stepper Component
                    Text(text = "Booking Progress", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    BookingStatusStepper(
                        steps = state.steps,
                        currentStepIndex = state.status.index
                    )

                    // Action Controls
                    if (state.status == BookingStatus.COMPLETED) {
                        PrimaryButton(
                            text = "Rate & Review Service",
                            onClick = { onRateReview(state.bookingId) }
                        )
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.advanceStatusSimulated() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Simulate Next Status Step (${state.status.title} → Next)")
                        }
                    }
                }
            }
        }
    }
}

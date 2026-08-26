package com.marketplace.onehour.customer.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.CustomerBottomNavBar
import com.marketplace.onehour.common.network.BookingDto
import com.marketplace.onehour.common.theme.SuccessGreen

@Composable
fun BookingHistoryScreen(
    onTrackLive: (bookingId: String) -> Unit,
    onOpenChat: (bookingId: String, helperId: String) -> Unit,
    onRateReview: (bookingId: String) -> Unit,
    onRebookHelper: (helperId: String) -> Unit,
    onNavigateBottom: (String) -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                AppTopBar(title = "My Service Bookings")
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = {
                            Text(
                                "Upcoming (${state.upcomingBookings.size})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = {
                            Text(
                                "Past (${state.pastBookings.size})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }
        },
        bottomBar = {
            CustomerBottomNavBar(
                currentRoute = "booking_history",
                onNavigate = onNavigateBottom
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            val currentList = if (state.selectedTabIndex == 0) state.upcomingBookings else state.pastBookings

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.selectedTabIndex == 0) "No upcoming 1-hour bookings" else "No past booking history",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(currentList) { booking ->
                        BookingCardItem(
                            booking = booking,
                            isUpcoming = state.selectedTabIndex == 0,
                            onTrackLive = { onTrackLive(booking.id) },
                            onOpenChat = { onOpenChat(booking.id, booking.helperId) },
                            onRateReview = { onRateReview(booking.id) },
                            onRebookHelper = { onRebookHelper(booking.helperId) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCardItem(
    booking: BookingDto,
    isUpcoming: Boolean,
    onTrackLive: () -> Unit,
    onOpenChat: () -> Unit,
    onRateReview: () -> Unit,
    onRebookHelper: () -> Unit
) {
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
                Surface(
                    color = if (isUpcoming) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUpcoming) SuccessGreen else MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "$${"%.2f".format(booking.totalAmount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = booking.serviceName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Helper: ${booking.helperName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = booking.scheduledTime, fontSize = 12.sp, color = Color.Gray)
            }

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isUpcoming) {
                    OutlinedButton(onClick = onOpenChat, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Chat")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onTrackLive, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Track Live")
                    }
                } else {
                    OutlinedButton(onClick = onRateReview, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate & Review")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onRebookHelper, shape = RoundedCornerShape(10.dp)) {
                        Text("Book Again")
                    }
                }
            }
        }
    }
}

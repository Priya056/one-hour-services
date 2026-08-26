package com.marketplace.onehour.customer.presentation.history

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.CustomerBottomNavBar
import com.marketplace.onehour.common.components.InitialsAvatar
import com.marketplace.onehour.common.network.BookingDto
import com.marketplace.onehour.common.theme.SuccessGreen
import kotlinx.coroutines.delay

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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (state.selectedTabIndex == 0) Icons.Default.EventAvailable else Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (state.selectedTabIndex == 0) "No upcoming bookings yet" else "No past bookings yet",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (state.selectedTabIndex == 0) "Book a helper and it'll show up here" else "Completed bookings land here",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(currentList) { index, booking ->
                        var appeared by remember(booking.id) { mutableStateOf(false) }
                        LaunchedEffect(booking.id) {
                            delay(index * 40L)
                            appeared = true
                        }
                        val entrance by animateFloatAsState(
                            targetValue = if (appeared) 1f else 0f,
                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                            label = "booking_entrance"
                        )

                        BookingCardItem(
                            booking = booking,
                            isUpcoming = state.selectedTabIndex == 0,
                            modifier = Modifier
                                .alpha(entrance)
                                .scale(0.95f + entrance * 0.05f),
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
    modifier: Modifier = Modifier,
    onTrackLive: () -> Unit,
    onOpenChat: () -> Unit,
    onRateReview: () -> Unit,
    onRebookHelper: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                InitialsAvatar(name = booking.helperName, size = 22.dp, shape = CircleShape)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = booking.helperName, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
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

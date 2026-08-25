package com.marketplace.onehour.helper.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.theme.SuccessGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HourlyRateScheduleScreen(
    onBackClick: () -> Unit,
    onCompleteRegistration: () -> Unit,
    viewModel: ScheduleViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val daysOfWeek = remember { listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }
    val timeSlots = remember {
        listOf(
            "Morning (8 AM - 12 PM)",
            "Afternoon (12 PM - 5 PM)",
            "Evening (5 PM - 9 PM)"
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Hourly Rate & Schedule",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val registrationError = state.registrationError
                    if (registrationError != null) {
                        Text(
                            text = registrationError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    PrimaryButton(
                        text = if (state.isLoading) "Submitting..." else "Complete Registration & Go Live 🎉",
                        onClick = {
                            if (!state.isLoading) {
                                viewModel.completeRegistration(onSuccess = onCompleteRegistration)
                            }
                        }
                    )
                }
            }
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

            // Stepper Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "STEP 4 OF 4", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "Rate & Availability", fontSize = 12.sp, color = Color.Gray)
            }
            LinearProgressIndicator(
                progress = 1.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            // Hourly Rate Pricing Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Set Your Hourly Rate", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Surface(color = SuccessGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                            Text(text = "Popular: $25 - $45/hr", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "$", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.hourlyRate.toString(),
                            onValueChange = { input ->
                                input.toDoubleOrNull()?.let { viewModel.onRateChanged(it) }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "/ hour", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }

                    val takeHome = state.hourlyRate * 0.90
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 You keep $${"%.2f".format(takeHome)} (90%) per 1-hour job completed.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Instant Booking Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Enable Instant 15-Min Jobs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Auto-match with nearby customers who need urgent 1-hour service", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = state.instantBookingEnabled,
                        onCheckedChange = { viewModel.toggleInstantBooking(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFF38BDF8))
                    )
                }
            }

            // Working Days Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Working Days", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    daysOfWeek.forEach { day ->
                        val isSelected = state.selectedDays.contains(day)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleDay(day) },
                            label = { Text(day, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Working Time Slots Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Preferred Working Hours", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeSlots.forEach { slot ->
                        val isSelected = state.selectedTimeSlots.contains(slot)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleTimeSlot(slot) },
                            label = { Text(slot, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

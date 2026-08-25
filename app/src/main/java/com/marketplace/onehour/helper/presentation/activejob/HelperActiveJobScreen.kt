package com.marketplace.onehour.helper.presentation.activejob

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperActiveJobScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    onJobCompleted: () -> Unit,
    viewModel: ActiveJobViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(bookingId) {
        bookingId.toIntOrNull()?.let { viewModel.loadJob(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Job • $bookingId") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Customer Information", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = state.customerName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = state.address, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Call Customer */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Customer")
                        }

                        OutlinedButton(
                            onClick = { /* Open Navigation */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Navigate")
                        }
                    }
                }
            }

            // Job Status Stepper & Workflow Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Current Job Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Status: ${state.status.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.errorMessage != null) {
                        Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    when (state.status) {
                        JobLifecycleStatus.ACCEPTED -> {
                            Button(
                                onClick = { viewModel.advanceTo(JobLifecycleStatus.ON_THE_WAY) },
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Traveling (On The Way)")
                            }
                        }
                        JobLifecycleStatus.ON_THE_WAY -> {
                            Button(
                                onClick = { viewModel.advanceTo(JobLifecycleStatus.ARRIVED) },
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.PinDrop, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I Have Arrived at Location")
                            }
                        }
                        JobLifecycleStatus.ARRIVED -> {
                            Button(
                                onClick = { viewModel.advanceTo(JobLifecycleStatus.IN_PROGRESS) },
                                enabled = !state.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Begin 1-Hour Service")
                            }
                        }
                        JobLifecycleStatus.IN_PROGRESS -> {
                            Column {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "1-Hour Service is currently in progress...", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.advanceTo(JobLifecycleStatus.COMPLETED) },
                                    enabled = !state.isLoading,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Complete Service & Collect Payout")
                                }
                            }
                        }
                        JobLifecycleStatus.COMPLETED -> {
                            LaunchedEffect(Unit) { onJobCompleted() }
                            Text(text = "🎉 Job Completed Successfully!", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }
    }
}

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

                    when (state.status) {
                        JobLifecycleStatus.ACCEPTED -> {
                            Button(
                                onClick = { viewModel.updateStatus(JobLifecycleStatus.ON_THE_WAY) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Start Traveling (On The Way)")
                            }
                        }
                        JobLifecycleStatus.ON_THE_WAY -> {
                            Button(
                                onClick = { viewModel.updateStatus(JobLifecycleStatus.ARRIVED) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.PinDrop, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I Have Arrived at Location")
                            }
                        }
                        JobLifecycleStatus.ARRIVED -> {
                            Column {
                                Text(text = "Ask customer for 4-Digit Start Code (OTP: 1234)", fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = state.otpCodeInput,
                                    onValueChange = { viewModel.setOtpInput(it) },
                                    label = { Text("Enter 4-Digit Customer OTP") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (state.otpError != null) {
                                    Text(text = state.otpError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.verifyOtpAndStart("1234") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Verify & Begin 1-Hour Service")
                                }
                            }
                        }
                        JobLifecycleStatus.IN_PROGRESS -> {
                            Column {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "1-Hour Service is currently in progress...", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        viewModel.updateStatus(JobLifecycleStatus.COMPLETED)
                                        onJobCompleted()
                                    },
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
                            Text(text = "🎉 Job Completed Successfully!", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }
    }
}

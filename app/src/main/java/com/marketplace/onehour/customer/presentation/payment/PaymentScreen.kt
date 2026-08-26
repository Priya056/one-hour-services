package com.marketplace.onehour.customer.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    onPaymentSuccess: (bookingId: String) -> Unit,
    viewModel: PaymentViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.setBookingDetails(bookingId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Payment Options",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrimaryButton(
                        text = if (state.isProcessing) "Opening Razorpay..." else "Pay $${"%.2f".format(state.totalAmount)} & Confirm",
                        onClick = { viewModel.processPayment(activity, onPaymentSuccess) },
                        enabled = !state.isProcessing
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Total Amount Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Amount to Pay", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(
                            text = "$${"%.2f".format(state.totalAmount)}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "1-Hour Service",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // TODO Integration Banner for Razorpay SDK
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TODO: Razorpay Payment Gateway Integration",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mock Gateway Active • Connect Razorpay SDK (`com.razorpay:checkout`) here for live transactions.",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Payment Methods Header
            Text(text = "Select Payment Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Method 1: UPI Options
            PaymentOptionCard(
                title = "UPI Payment (Instant)",
                subtitle = "Google Pay, PhonePe, Paytm, BHIM",
                icon = Icons.Default.QrCodeScanner,
                isSelected = state.selectedMethod == PaymentMethodType.UPI,
                onClick = { viewModel.selectPaymentMethod(PaymentMethodType.UPI) }
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    listOf("Google Pay", "PhonePe", "Paytm", "Other UPI ID").forEach { upiApp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectUpiApp(upiApp) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.selectedUpiApp == upiApp,
                                onClick = { viewModel.selectUpiApp(upiApp) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(upiApp, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Method 2: Credit / Debit Cards
            PaymentOptionCard(
                title = "Credit / Debit Card",
                subtitle = "Visa, MasterCard, RuPay, Maestro",
                icon = Icons.Default.CreditCard,
                isSelected = state.selectedMethod == PaymentMethodType.CARD,
                onClick = { viewModel.selectPaymentMethod(PaymentMethodType.CARD) }
            )

            // Method 3: Net Banking
            PaymentOptionCard(
                title = "Net Banking",
                subtitle = "All major Indian banks supported",
                icon = Icons.Default.AccountBalance,
                isSelected = state.selectedMethod == PaymentMethodType.NETBANKING,
                onClick = { viewModel.selectPaymentMethod(PaymentMethodType.NETBANKING) }
            )

            // Method 4: Cash on Completion
            PaymentOptionCard(
                title = "Pay Cash After Service",
                subtitle = "Pay directly to helper when 1-hour job is complete",
                icon = Icons.Default.Payments,
                isSelected = state.selectedMethod == PaymentMethodType.CASH,
                onClick = { viewModel.selectPaymentMethod(PaymentMethodType.CASH) }
            )

            // Safety Shield
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "100% Secure & Encrypted Payments", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                RadioButton(selected = isSelected, onClick = onClick)
            }
            if (isSelected && content != null) {
                content()
            }
        }
    }
}

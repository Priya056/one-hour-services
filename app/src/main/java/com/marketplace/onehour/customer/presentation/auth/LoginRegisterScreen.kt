package com.marketplace.onehour.customer.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.PrimaryButton

@Composable
fun LoginRegisterScreen(
    onLoginSuccess: () -> Unit,
    onSwitchToHelperOnboarding: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(32.dp))

            if (state.isOtpSent) {
                IconButton(
                    onClick = { viewModel.resetToPhoneEntry() },
                    modifier = Modifier.offset(x = (-12).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Phone Input",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Text(
                text = if (!state.isOtpSent) "Welcome to 1-HOUR" else "Verify Mobile OTP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!state.isOtpSent)
                    "Enter your phone number to get started with instant 1-hour local helper bookings."
                else
                    "Enter the 6-digit verification code sent to +91 ${state.phoneNumber}",
                fontSize = 15.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            AnimatedContent(
                targetState = state.isOtpSent,
                label = "auth_step_transition"
            ) { isOtpStep ->
                if (!isOtpStep) {
                    // Phone Number Input Flow
                    Column {
                        OutlinedTextField(
                            value = state.phoneNumber,
                            onValueChange = { viewModel.onPhoneNumberChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mobile Number") },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("+91", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color.Gray.copy(alpha = 0.5f)))
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(14.dp)
                        )

                        if (state.errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        PrimaryButton(
                            text = if (state.isLoading) "Sending OTP..." else "Send Verification Code",
                            onClick = { viewModel.sendOtp() },
                            enabled = !state.isLoading && state.phoneNumber.length == 10
                        )
                    }
                } else {
                    // OTP Entry Flow
                    Column {
                        OutlinedTextField(
                            value = state.otpCode,
                            onValueChange = { viewModel.onOtpChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("6-Digit OTP Code") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Helpful Mock Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "💡 Mock Testing Mode: Auto-filled OTP code is 123456",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (state.errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = if (state.isLoading) "Verifying..." else "Verify & Continue",
                            onClick = { viewModel.verifyOtp(onLoginSuccess) },
                            enabled = !state.isLoading && state.otpCode.length >= 4
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { viewModel.sendOtp() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Didn't receive code? Resend OTP")
                        }
                    }
                }
            }
        }

        // Footer Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "By continuing, you agree to our Terms & Privacy Policy.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSwitchToHelperOnboarding) {
                Text(
                    text = "Want to earn money? Become a Helper →",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

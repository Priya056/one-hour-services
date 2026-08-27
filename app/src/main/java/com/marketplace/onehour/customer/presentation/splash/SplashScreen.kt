package com.marketplace.onehour.customer.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.R
import com.marketplace.onehour.common.theme.BarlowCondensed
import com.marketplace.onehour.common.theme.TealDeep
import com.marketplace.onehour.common.theme.Terracotta

@Composable
fun SplashScreen(
    onSplashFinished: (isUserLoggedIn: Boolean) -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            onSplashFinished(state.isUserLoggedIn)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF13201C),
                        TealDeep,
                        Terracotta.copy(alpha = 0.85f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = "OneHour Logo Mark",
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "OneHour",
                fontFamily = BarlowCondensed,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Text(
                text = "Local Services Marketplace",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "LOCAL HELP, JUST 1 HOUR AWAY.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4ADE80),
                letterSpacing = 1.2.sp
            )
        }
    }
}

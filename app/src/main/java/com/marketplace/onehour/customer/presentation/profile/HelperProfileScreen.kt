package com.marketplace.onehour.customer.presentation.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.InitialsAvatar
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.components.StarRatingBar
import com.marketplace.onehour.common.theme.StarYellow
import com.marketplace.onehour.common.theme.SuccessGreen

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HelperProfileScreen(
    helperId: String,
    onBackClick: () -> Unit,
    onBookNowClick: (String) -> Unit,
    viewModel: HelperProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(helperId) {
        viewModel.loadHelperProfile(helperId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Helper Profile",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { /* Favorite helper */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                    IconButton(onClick = { /* Share profile */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            if (state.helper != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "HOURLY RATE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "$${state.helper!!.hourlyRate.toInt()} / hr",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val haptics = LocalHapticFeedback.current
                        PrimaryButton(
                            text = "Book Now",
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onBookNowClick(state.helper!!.id)
                            },
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading || state.helper == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val helper = state.helper!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Profile Info
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    var appeared by remember { mutableStateOf(false) }
                    LaunchedEffect(helper.id) { appeared = true }
                    val entranceScale by animateFloatAsState(
                        targetValue = if (appeared) 1f else 0.8f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                        label = "profile_avatar_entrance"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.scale(entranceScale)) {
                            if (helper.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(helper.photoUrl)
                                        .crossfade(300)
                                        .build(),
                                    contentDescription = helper.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.2f))
                                )
                            } else {
                                InitialsAvatar(
                                    name = helper.name,
                                    size = 110.dp,
                                    shape = CircleShape
                                )
                            }

                            if (helper.isAvailable) {
                                Surface(
                                    color = SuccessGreen,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .offset(y = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color.White, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Available Now",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = helper.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = helper.mainCategory,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Quick Stats Card Row
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatItem(
                                title = "Rating",
                                value = "${helper.rating} ★",
                                icon = Icons.Default.Star,
                                iconColor = StarYellow
                            )
                            StatDivider()
                            StatItem(
                                title = "Reviews",
                                value = "${helper.reviewCount}",
                                icon = Icons.Default.RateReview,
                                iconColor = MaterialTheme.colorScheme.primary
                            )
                            StatDivider()
                            StatItem(
                                title = "Distance",
                                value = "${helper.distanceKm} km",
                                icon = Icons.Default.Navigation,
                                iconColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // About / Bio Section
                item {
                    Column {
                        Text(
                            text = "About Helper",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = helper.bio,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Skills & Services Section
                item {
                    Column {
                        Text(
                            text = "Skills & Services",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            helper.skills.forEach { skill ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(skill, fontWeight = FontWeight.Medium) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }

                // Reviews Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customer Reviews",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "See all (${helper.reviewCount})",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Reviews List
                items(state.reviews) { review ->
                    ReviewCard(review = review)
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(title, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(30.dp)
            .background(Color.Gray.copy(alpha = 0.3f))
    )
}

@Composable
private fun ReviewCard(review: ReviewItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(
                        name = review.reviewerName,
                        size = 28.dp,
                        shape = CircleShape
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = review.reviewerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = review.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            StarRatingBar(rating = review.rating, starSize = 16)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )
        }
    }
}

package com.marketplace.onehour.customer.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import com.marketplace.onehour.common.components.InitialsAvatar
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.components.StarRatingBar
import com.marketplace.onehour.common.theme.StarYellow

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RateReviewScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: RateReviewViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(bookingId) {
        viewModel.loadReviewDetails(bookingId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Rate & Review Helper",
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
                    PrimaryButton(
                        text = if (state.isSubmitting) "Submitting Review..." else "Submit Review",
                        onClick = { viewModel.submitReview(onReviewSubmitted) },
                        enabled = !state.isSubmitting
                    )
                }
            }
        }
    ) { innerPadding ->
        if (state.helper == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val helper = state.helper!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Helper Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (helper.photoUrl.isNotBlank()) {
                            AsyncImage(
                                model = helper.photoUrl,
                                contentDescription = helper.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            InitialsAvatar(name = helper.name, size = 64.dp, shape = CircleShape)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = helper.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(text = "1-Hour ${helper.mainCategory} Service", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                // Interactive Rating Selector Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "How was your 1-Hour Service?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        StarRatingBar(
                            rating = state.selectedRating,
                            onRatingSelected = { viewModel.onRatingChanged(it) },
                            starSize = 36
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val ratingLabel = when (state.selectedRating) {
                            5 -> "5.0 • Outstanding Experience!"
                            4 -> "4.0 • Very Good Service"
                            3 -> "3.0 • Average Service"
                            2 -> "2.0 • Needs Improvement"
                            else -> "1.0 • Unsatisfactory"
                        }
                        Text(
                            text = ratingLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = StarYellow
                        )
                    }
                }

                // Quick Feedback Tags Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "What went well?", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Punctual & On Time", "Great Communication", "Professional Work", "Clean & Neat", "Value for Money").forEach { tag ->
                            val isSelected = state.selectedTags.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleTag(tag) },
                                label = { Text(tag, fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // Comment Text Field Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Write your review (Optional)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.commentText,
                        onValueChange = { viewModel.onCommentChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Share details of your 1-hour service experience with ${helper.name}...") },
                        shape = RoundedCornerShape(14.dp),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                // Optional Helper Tip Selection Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Add a tip for ${helper.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "100% goes to helper", fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0.0, 2.0, 5.0, 10.0).forEach { tipVal ->
                                val isSel = state.selectedTipAmount == tipVal
                                val label = if (tipVal == 0.0) "No Tip" else "$${tipVal.toInt()}"
                                ElevatedFilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.selectTip(tipVal) },
                                    label = { Text(label, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

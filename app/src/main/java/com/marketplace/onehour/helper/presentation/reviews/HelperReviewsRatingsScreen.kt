package com.marketplace.onehour.helper.presentation.reviews

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperReviewsRatingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: HelperReviewsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val reviewsList = state.reviews

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Ratings & Reviews") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Rating Summary Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${"%.1f".format(state.averageRating)} ★",
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = Color(0xFFFFB300)
                            )
                            Text(text = "Based on ${state.totalReviews} customer reviews", fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Text(text = "Customer Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            items(reviewsList, key = { it.id }) { review ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = review.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "${review.rating} ★", fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = review.comment, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = review.date, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

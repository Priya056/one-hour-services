package com.marketplace.onehour.customer.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.theme.StarYellow

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FiltersSheet(
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    viewModel: FilterViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Filters",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Filter Helpers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(onClick = { viewModel.resetFilters() }) {
                Text(
                    text = "Reset All",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Category Filter Section
            Column {
                Text(
                    text = "Service Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.availableCategories.forEach { category ->
                        val isSelected = state.selectedCategories.contains(category)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleCategory(category) },
                            label = { Text(category, fontSize = 13.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Price Range Slider Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Hourly Rate Range",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${state.selectedPriceRange.start.toInt()} - $${state.selectedPriceRange.endInclusive.toInt()} / hr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = state.selectedPriceRange,
                    onValueChange = { viewModel.onPriceRangeChanged(it) },
                    valueRange = 10f..100f,
                    steps = 18
                )
            }

            // Minimum Rating Filter Section
            Column {
                Text(
                    text = "Minimum Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.0f, 3.5f, 4.0f, 4.5f).forEach { ratingVal ->
                        val isSelected = state.minRating == ratingVal
                        val label = if (ratingVal == 0.0f) "Any" else "${ratingVal}★+"
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onMinRatingChanged(ratingVal) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (ratingVal > 0) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = StarYellow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(label, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Max Distance Radius Slider Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Maximum Distance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Within ${state.maxDistanceKm.toInt()} km",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = state.maxDistanceKm,
                    onValueChange = { viewModel.onMaxDistanceChanged(it) },
                    valueRange = 1f..25f,
                    steps = 24
                )
            }

            // Available Right Now Switch Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Available Right Now Only",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Show helpers ready for immediate 1-hour service",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = state.isAvailableNowOnly,
                    onCheckedChange = { viewModel.onAvailableNowToggled(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Button
        PrimaryButton(
            text = "Apply Filters",
            onClick = {
                onApply()
                onDismiss()
            }
        )
    }
}

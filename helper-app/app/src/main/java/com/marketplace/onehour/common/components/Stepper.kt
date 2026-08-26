package com.marketplace.onehour.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marketplace.onehour.common.theme.SuccessGreen

@Composable
fun BookingStatusStepper(
    steps: List<String>,
    currentStepIndex: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, stepTitle ->
            val isCompleted = index < currentStepIndex
            val isCurrent = index == currentStepIndex
            val stepColor = when {
                isCompleted || isCurrent -> SuccessGreen
                else -> Color.Gray
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(stepColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stepTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isCurrent) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 4.dp, bottom = 4.dp)
                        .width(2.dp)
                        .height(24.dp)
                        .background(if (index < currentStepIndex) SuccessGreen else Color.Gray.copy(alpha = 0.4f))
                )
            }
        }
    }
}

package com.marketplace.onehour.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marketplace.onehour.common.theme.StarYellow

@Composable
fun StarRatingBar(
    rating: Int,
    maxRating: Int = 5,
    onRatingSelected: ((Int) -> Unit)? = null,
    starSize: Int = 28
) {
    Row {
        for (i in 1..maxRating) {
            val isSelected = i <= rating
            Icon(
                imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = "Star $i",
                tint = StarYellow,
                modifier = Modifier
                    .size(starSize.dp)
                    .padding(2.dp)
                    .then(
                        if (onRatingSelected != null) Modifier.clickable { onRatingSelected(i) }
                        else Modifier
                    )
            )
        }
    }
}

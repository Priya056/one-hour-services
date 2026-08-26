package com.marketplace.onehour.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marketplace.onehour.common.theme.BarlowCondensed
import com.marketplace.onehour.common.theme.StarYellow
import com.marketplace.onehour.common.theme.TealBright
import com.marketplace.onehour.common.theme.TealDeep
import com.marketplace.onehour.common.theme.Terracotta
import com.marketplace.onehour.common.theme.TerracottaLight
import kotlin.math.abs

private val AVATAR_PALETTE = listOf(TealDeep, Terracotta, TealBright, StarYellow, TerracottaLight)

/**
 * A person doesn't have a real uploaded photo yet more often than not on a
 * fresh marketplace — this is the honest fallback (their initials on a
 * brand-colored tile) rather than a broken image box or a stock photo of a
 * stranger standing in for them.
 */
@Composable
fun InitialsAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: Shape = RoundedCornerShape(14.dp)
) {
    val initials = name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifBlank { "?" }

    val color = AVATAR_PALETTE[abs(name.hashCode()) % AVATAR_PALETTE.size]

    Box(
        modifier = modifier.size(size).clip(shape).background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontFamily = BarlowCondensed,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.34f).sp
        )
    }
}

/**
 * Deterministic real-photo URL for a service category tile — same category
 * always resolves to the same image (Picsum's seed param), so the grid
 * doesn't reshuffle its look between app launches.
 */
fun categoryPhotoUrl(categoryName: String, sizePx: Int = 300): String {
    val seed = categoryName.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
    return "https://picsum.photos/seed/$seed/$sizePx/$sizePx"
}

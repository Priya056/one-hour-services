package com.marketplace.onehour.common.theme

import androidx.compose.ui.graphics.Color

/**
 * 1-Hour's identity: a tradesperson's confidence (deep teal — distinct from
 * generic SaaS blue/purple) paired with the product's actual promise —
 * urgency without alarm (warm terracotta, not stoplight red). Neutrals lean
 * warm, not cold-slate, to keep "neighbor helping neighbor" over "corporate
 * dashboard."
 */

// Brand
val TealDeep = Color(0xFF0E5C4F)       // primary — trust, professionalism
val TealBright = Color(0xFF17B39A)     // primary, light-mode-on-dark-surface / accents
val Terracotta = Color(0xFFE0603E)     // "act now" — instant booking, CTAs, urgency badges
val TerracottaLight = Color(0xFFF08462)

// Dark mode surfaces — warm charcoal, not blue-grey slate
val BackgroundDark = Color(0xFF17201D)
val SurfaceDark = Color(0xFF1F2B27)
val CardDark = Color(0xFF283733)
val TextPrimaryDark = Color(0xFFF4F1EC)
val TextSecondaryDark = Color(0xFFA9B5B0)

// Light mode surfaces — warm off-white, not clinical white
val BackgroundLight = Color(0xFFFAF8F4)
val SurfaceLight = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1B2521)
val TextSecondaryLight = Color(0xFF5C6864)

// Semantic — shifted off the literal Tailwind defaults, still WCAG-legible
val StarYellow = Color(0xFFE0A526)
val SuccessGreen = Color(0xFF3F9463)
val AlertRed = Color(0xFFD9483F)
val WarningAmber = Color(0xFFDB9432)

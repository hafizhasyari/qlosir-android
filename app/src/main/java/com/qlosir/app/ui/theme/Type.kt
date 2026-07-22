package com.qlosir.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.qlosir.app.R

/**
 * Bundled static Plus Jakarta Sans font family
 * Maps each font weight (Normal 400, Medium 500, SemiBold 600, Bold 700, ExtraBold 800)
 * directly to its corresponding static TTF resource file for 100% exact header bold rendering.
 */
val PlusJakartaSansFontFamily = FontFamily(
    Font(resId = R.font.plus_jakarta_sans_regular, weight = FontWeight.Normal),
    Font(resId = R.font.plus_jakarta_sans_medium, weight = FontWeight.Medium),
    Font(resId = R.font.plus_jakarta_sans_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.plus_jakarta_sans_bold, weight = FontWeight.Bold),
    Font(resId = R.font.plus_jakarta_sans_extrabold, weight = FontWeight.ExtraBold)
)

// Material 3 Typography configuration using static Plus Jakarta Sans
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 44.sp,
        letterSpacing = (-1).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 31.2.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 19.sp,
        letterSpacing = (-0.3).sp
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 23.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
)

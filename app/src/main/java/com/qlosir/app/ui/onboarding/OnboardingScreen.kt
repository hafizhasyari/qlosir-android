package com.qlosir.app.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qlosir.app.R
import com.qlosir.app.ui.theme.AccentCheckmarkAmber
import com.qlosir.app.ui.theme.BarChartBlueLight
import com.qlosir.app.ui.theme.BarChartBlueLightAlt
import com.qlosir.app.ui.theme.BarChartBlueMedium
import com.qlosir.app.ui.theme.BarChartBlueMediumAlt
import com.qlosir.app.ui.theme.BrandPrimaryBlue
import com.qlosir.app.ui.theme.DarkPrimaryText
import com.qlosir.app.ui.theme.IllustrationBgEnd
import com.qlosir.app.ui.theme.IllustrationBgStart
import com.qlosir.app.ui.theme.InactiveDot
import com.qlosir.app.ui.theme.LowStockBg
import com.qlosir.app.ui.theme.LowStockBorder
import com.qlosir.app.ui.theme.LowStockDot
import com.qlosir.app.ui.theme.LowStockText
import com.qlosir.app.ui.theme.PlusJakartaSansFontFamily
import com.qlosir.app.ui.theme.SuccessGreen
import com.qlosir.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Main Onboarding Screen Composable with Plus Jakarta Sans typography.
 */
@Composable
fun OnboardingScreen(
    onNavigate: (OnboardingNavigationEvent) -> Unit,
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Handle ViewModel navigation events
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            onNavigate(event)
        }
    }

    // Sync pagerState with ViewModel state
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onPageChanged(pagerState.currentPage)
    }

    // Sync ViewModel page updates back to pagerState
    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        // Top Skip Header (visible on pages 0 and 1)
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .height(38.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (pagerState.currentPage < 2) {
                Text(
                    text = stringResource(id = R.string.onboarding_skip),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable { viewModel.onSkipClicked() }
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                        .semantics { contentDescription = "Skip onboarding button" }
                )
            }
        }

        // Horizontal Pager for 3 slides
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                // Hero Illustration Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(IllustrationBgStart, IllustrationBgEnd),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (page) {
                        0 -> BarcodeScanIllustration()
                        1 -> StockKasbonIllustration()
                        2 -> ReportChartIllustration()
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Page Indicator Dots
                PageIndicator(currentPage = page, pageCount = 3)

                Spacer(modifier = Modifier.height(22.dp))

                // Title Text
                Text(
                    text = when (page) {
                        0 -> stringResource(id = R.string.onboarding_1_title)
                        1 -> stringResource(id = R.string.onboarding_2_title)
                        else -> stringResource(id = R.string.onboarding_3_title)
                    },
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkPrimaryText,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 31.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Text
                Text(
                    text = when (page) {
                        0 -> stringResource(id = R.string.onboarding_1_desc)
                        1 -> stringResource(id = R.string.onboarding_2_desc)
                        else -> stringResource(id = R.string.onboarding_3_desc)
                    },
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    lineHeight = 23.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary Action Button ("Lanjut" / "Mulai Sekarang")
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = BrandPrimaryBlue.copy(alpha = 0.32f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(BrandPrimaryBlue)
                .clickable {
                    if (pagerState.currentPage < 2) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.onFinishOnboarding()
                    }
                }
                .padding(vertical = 17.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (pagerState.currentPage < 2) {
                    stringResource(id = R.string.onboarding_next)
                } else {
                    stringResource(id = R.string.onboarding_start)
                },
                fontFamily = PlusJakartaSansFontFamily,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Login Link (Visible on page 2)
        if (pagerState.currentPage == 2) {
            Spacer(modifier = Modifier.height(16.dp))
            val annotatedLoginString = buildAnnotatedString {
                append(stringResource(id = R.string.onboarding_have_account))
                append(" ")
                withStyle(
                    style = SpanStyle(
                        fontFamily = PlusJakartaSansFontFamily,
                        color = BrandPrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(stringResource(id = R.string.onboarding_login))
                }
            }
            Text(
                text = annotatedLoginString,
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .clickable { viewModel.onLoginClicked() }
            )
        } else {
            Spacer(modifier = Modifier.height(34.dp))
        }
    }
}

/**
 * Animated Page Indicator component.
 * Active dot expands to a 26dp wide pill; inactive dots are 8dp circles.
 */
@Composable
private fun PageIndicator(
    currentPage: Int,
    pageCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage

            val dotWidth by animateDpAsState(
                targetValue = if (isSelected) 26.dp else 8.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "indicator_width_$index"
            )

            val dotColor by animateColorAsState(
                targetValue = if (isSelected) BrandPrimaryBlue else InactiveDot,
                animationSpec = tween(durationMillis = 300),
                label = "indicator_color_$index"
            )

            Box(
                modifier = Modifier
                    .size(width = dotWidth, height = 8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

/**
 * Page 1 Illustration: Barcode Scanning with animated laser line (qscan).
 */
@Composable
private fun BarcodeScanIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "qscan_transition")

    // Vertical scan beam animation: 0f (top) to 1f (bottom)
    val scanFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "qscan_beam"
    )

    val cardWidth = 150.dp
    val cardHeight = 200.dp
    val topMin = 16.dp
    val topMax = 181.dp // cardHeight - 16.dp - 3.dp

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Floating Barcode White Card (150dp x 200dp)
        Box(
            modifier = Modifier
                .size(width = cardWidth, height = cardHeight)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color(0x28123C8C)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(3.dp, BrandPrimaryBlue, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Barcode Bars Representation (10 vertical bars)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(58.dp)
                ) {
                    val barWidths = listOf(4, 3, 6, 3, 5, 3, 7, 4, 3, 6)
                    barWidths.forEach { w ->
                        Box(
                            modifier = Modifier
                                .width(w.dp)
                                .height(58.dp)
                                .background(DarkPrimaryText)
                        )
                    }
                }

                // Barcode SKU Text
                Text(
                    text = "8 992134 001",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = DarkPrimaryText
                )
            }

            // Animated Yellow Laser Scan Line (#FFB020) - Precisely fitted inside Barcode Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = topMin + (topMax - topMin) * scanFraction)
                    .height(3.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(3.dp),
                        spotColor = AccentCheckmarkAmber
                    )
                    .background(AccentCheckmarkAmber, RoundedCornerShape(3.dp))
            )
        }

        // Success Checkmark Badge (46dp circle #16A34A at bottom right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 22.dp, end = 36.dp)
                .size(46.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = SuccessGreen.copy(alpha = 0.35f)
                )
                .clip(CircleShape)
                .background(SuccessGreen),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(24.dp)) {
                val scaleFactor = size.width / 24f
                scale(scaleFactor, pivot = Offset.Zero) {
                    val path = Path().apply {
                        moveTo(5f, 12.5f)
                        lineTo(10f, 17.5f)
                        lineTo(19f, 7f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = 3f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}

/**
 * Page 2 Illustration: Stock Bar Charts + Kasbon Card + Low Stock Badge.
 */
@Composable
private fun StockKasbonIllustration() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left: Low Stock Alert Badge Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 26.dp, start = 30.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(LowStockBg)
                .border(1.dp, LowStockBorder, RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(LowStockDot, CircleShape)
            )
            Text(
                text = stringResource(id = R.string.onboarding_low_stock_badge),
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LowStockText
            )
        }

        // Bottom-left: Stock Columns Bar Chart
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 60.dp, start = 34.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 54.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp), spotColor = BrandPrimaryBlue.copy(alpha = 0.28f))
                    .background(BrandPrimaryBlue, RoundedCornerShape(12.dp))
            )
            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 78.dp)
                    .background(BarChartBlueMediumAlt, RoundedCornerShape(12.dp))
            )
            Box(
                modifier = Modifier
                    .size(width = 54.dp, height = 44.dp)
                    .background(BarChartBlueLightAlt, RoundedCornerShape(12.dp))
            )
        }

        // Top-right: Kasbon Debt Card Sample
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 32.dp)
                .width(120.dp)
                .shadow(elevation = 14.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x28123C8C))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.onboarding_kasbon_sample_title),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPrimaryText
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFEAF0F8), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(8.dp)
                        .background(Color(0xFFEAF0F8), RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Rp 45.000",
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SuccessGreen
                )
            }
        }
    }
}

/**
 * Page 3 Illustration: Daily Revenue Chart + Floating Gold Currency Badge.
 */
@Composable
private fun ReportChartIllustration() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left Revenue Header Text
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 26.dp, start = 30.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_revenue_sample_title),
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = R.string.onboarding_revenue_sample_amount),
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkPrimaryText
            )
        }

        // Top-right Floating Gold "Rp" Circle Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp, end = 26.dp)
                .size(44.dp)
                .shadow(elevation = 8.dp, shape = CircleShape, spotColor = AccentCheckmarkAmber.copy(alpha = 0.40f))
                .clip(CircleShape)
                .background(AccentCheckmarkAmber),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Rp",
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        // Bottom Revenue Bar Chart (5 columns)
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 38.dp, start = 34.dp, end = 34.dp)
        ) {
            val barSpecs = listOf(
                Pair(70.dp, BarChartBlueLight),
                Pair(110.dp, BarChartBlueMedium),
                Pair(90.dp, BarChartBlueLight),
                Pair(150.dp, BrandPrimaryBlue),
                Pair(124.dp, BarChartBlueMedium)
            )

            barSpecs.forEach { (h, color) ->
                Box(
                    modifier = Modifier
                        .width(38.dp)
                        .height(h)
                        .background(
                            color = color,
                            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                        )
                )
            }
        }
    }
}

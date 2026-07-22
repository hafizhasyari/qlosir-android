package com.qlosir.app.ui.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qlosir.app.R
import com.qlosir.app.ui.components.QlosirLogo
import com.qlosir.app.ui.theme.SplashGradientEnd
import com.qlosir.app.ui.theme.SplashGradientMiddle
import com.qlosir.app.ui.theme.SplashGradientStart
import kotlinx.coroutines.flow.collectLatest

/**
 * Qlosir Splash Screen Composable
 * Recreates the exact design specs from `Qlosir Onboarding.dc.html`
 */
@Composable
fun SplashScreen(
    onNavigateNext: (SplashNavigationEvent) -> Unit,
    viewModel: SplashViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            onNavigateNext(event)
        }
    }

    // 165 degree linear gradient background
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            SplashGradientStart,
            SplashGradientMiddle,
            SplashGradientEnd
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Center Content: Logo, Title, Tagline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Frosted outer container (150dp x 150dp, rx=44dp, bg rgba(255,255,255,0.10))
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(44.dp),
                        spotColor = Color(0x30000000)
                    )
                    .clip(RoundedCornerShape(44.dp))
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                // Qlosir Logo Card (112dp x 112dp)
                QlosirLogo(
                    size = 112.dp,
                    modifier = Modifier.semantics {
                        contentDescription = "Qlosir Brand Logo"
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // App Title "Qlosir"
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline "Kasir & manajemen stok untuk warung" / "POS & stock management for your store"
            Text(
                text = stringResource(id = R.string.splash_tagline),
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
                modifier = Modifier.widthIn(max = 260.dp)
            )
        }

        // Bottom Section: Pulsing Loading Dots + Offline Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp)
        ) {
            // Pulsing Loading Dots (qpulse: 1.3s ease-in-out infinite, staggered delays 0s, 0.2s, 0.4s)
            PulsingLoadingDots()

            // Offline Capable Badge Pill
            OfflineBadge()
        }
    }
}

/**
 * Pulsing Loading Dots indicator matching CSS @keyframes qpulse
 * {0%, 100%: opacity .28, scale .85; 50%: opacity 1, scale 1}
 */
@Composable
private fun PulsingLoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "qpulse_transition")

    Row(
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scaleAnim by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 650,
                        delayMillis = index * 200
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "qpulse_scale_$index"
            )

            val alphaAnim by infiniteTransition.animateFloat(
                initialValue = 0.28f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 650,
                        delayMillis = index * 200
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "qpulse_alpha_$index"
            )

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
                    .background(color = Color.White, shape = CircleShape)
            )
        }
    }
}

/**
 * Offline Mode Badge Pill Component
 * Displays checkmark icon and "Bekerja tanpa internet" / "Works offline" text badge
 */
@Composable
private fun OfflineBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 15.dp, vertical = 7.dp)
            .semantics {
                contentDescription = "Offline indicator badge"
            }
    ) {
        // Checkmark Icon Vector (viewBox 0 0 24 24, stroke width 2.4)
        Canvas(modifier = Modifier.size(14.dp)) {
            val scaleFactor = size.width / 24f
            scale(scaleFactor, pivot = Offset.Zero) {
                val checkPath = Path().apply {
                    moveTo(5f, 12.5f)
                    lineTo(10f, 17.5f)
                    lineTo(19f, 7f)
                }
                drawPath(
                    path = checkPath,
                    color = Color.White,
                    style = Stroke(
                        width = 2.4f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Text(
            text = stringResource(id = R.string.splash_offline),
            color = Color.White,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

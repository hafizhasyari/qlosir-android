package com.qlosir.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qlosir.app.ui.theme.AccentCheckmarkAmber
import com.qlosir.app.ui.theme.BrandPrimaryBlue

/**
 * Qlosir Logo Composable - Recreates the official Qlosir brand logo pixel-perfectly.
 * Features the signature Q-shaped magnifying ring in brand blue and golden amber checkmark inside.
 */
@Composable
fun QlosirLogo(
    size: Dp = 112.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color(0x59081E50),
                ambientColor = Color(0x40081E50)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val scaleFactor = this.size.width / 96f

            scale(scaleFactor, pivot = Offset.Zero) {
                // 1. Draw Q Donut Ring (Outer R=27, Inner R=13, Center=(48,48))
                val qPath = Path().apply {
                    fillType = PathFillType.EvenOdd
                    // Outer Circle: center (48, 48), radius 27
                    addOval(
                        androidx.compose.ui.geometry.Rect(
                            left = 21f,
                            top = 21f,
                            right = 75f,
                            bottom = 75f
                        )
                    )
                    // Inner Hole Circle: center (48, 48), radius 13
                    addOval(
                        androidx.compose.ui.geometry.Rect(
                            left = 35f,
                            top = 35f,
                            right = 61f,
                            bottom = 61f
                        )
                    )
                }
                drawPath(path = qPath, color = BrandPrimaryBlue)

                // 2. Draw Q Handle (Rotated rounded rect at bottom-right)
                rotate(degrees = 43f, pivot = Offset(59f, 60f)) {
                    drawRoundRect(
                        color = BrandPrimaryBlue,
                        topLeft = Offset(59f, 60f),
                        size = Size(24f, 11f),
                        cornerRadius = CornerRadius(5.5f, 5.5f)
                    )
                }

                // 3. Draw Checkmark inside Q hole
                val checkmarkPath = Path().apply {
                    moveTo(40.5f, 47.5f)
                    lineTo(46.0f, 53.0f)
                    lineTo(55.5f, 42.0f)
                }
                drawPath(
                    path = checkmarkPath,
                    color = AccentCheckmarkAmber,
                    style = Stroke(
                        width = 5.5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

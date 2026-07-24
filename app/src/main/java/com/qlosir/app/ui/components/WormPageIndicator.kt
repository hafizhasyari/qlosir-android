package com.qlosir.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qlosir.app.ui.theme.BrandPrimaryBlue
import com.qlosir.app.ui.theme.InactiveDot
import com.qlosir.app.ui.theme.QlosirTheme

/**
 * Classic worm page indicator that animates continuously with scroll gesture.
 *
 * At rest, the active indicator is a pill/strip (26dp wide by default), matching
 * the original design. During scroll transitions, the strip stretches with a worm
 * effect — leading edge moves first, trailing edge follows — synced to finger position.
 *
 * @param pageCount Total number of pages
 * @param currentPage Current settled page index from PagerState.currentPage
 * @param pageOffsetFraction Fractional offset from PagerState.currentPageOffsetFraction
 *        Range: -0.5..0.5 (negative = scrolling backward, positive = scrolling forward)
 * @param modifier Modifier for the Canvas
 * @param dotSize Diameter/height of each inactive dot
 * @param activeWidth Width of the active strip/pill at rest
 * @param spacing Gap between indicator elements
 * @param activeColor Color of the worm/strip (active indicator)
 * @param inactiveColor Color of inactive dots
 */
@Composable
fun WormPageIndicator(
    pageCount: Int,
    currentPage: Int,
    pageOffsetFraction: Float,
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp,
    activeWidth: Dp = 26.dp,
    spacing: Dp = 7.dp,
    activeColor: Color = BrandPrimaryBlue,
    inactiveColor: Color = InactiveDot
) {
    // Total width: (pageCount - 1) inactive dots + 1 active strip + spacing between each
    val totalWidth = activeWidth + dotSize * (pageCount - 1) + spacing * (pageCount - 1)

    Canvas(
        modifier = modifier
            .width(totalWidth)
            .height(dotSize)
    ) {
        val dotSizePx = dotSize.toPx()
        val activeWidthPx = activeWidth.toPx()
        val spacingPx = spacing.toPx()
        val dotRadius = dotSizePx / 2f

        // Calculate position for each element's left edge
        val positions = calculateElementPositions(
            pageCount = pageCount,
            currentPage = currentPage,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Draw all inactive dots
        for (i in 0 until pageCount) {
            if (i == currentPage) continue // Skip active — drawn as worm
            val leftX = positions[i]
            drawCircle(
                color = inactiveColor,
                radius = dotRadius,
                center = Offset(leftX + dotRadius, dotRadius)
            )
        }

        // Calculate worm body position based on offset fraction
        val wormPosition = calculateWormPosition(
            currentPage = currentPage,
            pageOffsetFraction = pageOffsetFraction,
            pageCount = pageCount,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Draw worm body as a rounded rect (strip)
        drawRoundRect(
            color = activeColor,
            topLeft = Offset(wormPosition.left, 0f),
            size = Size(wormPosition.right - wormPosition.left, dotSizePx),
            cornerRadius = CornerRadius(dotRadius, dotRadius)
        )
    }
}

/**
 * Represents the left and right edges of the worm body in pixels.
 */
data class WormPosition(
    val left: Float,
    val right: Float
)

/**
 * Calculates the left-edge position of each page's indicator element.
 *
 * Layout: elements are positioned sequentially. The active page element has
 * width = activeWidthPx (strip), inactive elements have width = dotSizePx (circle).
 * All separated by spacingPx.
 */
fun calculateElementPositions(
    pageCount: Int,
    currentPage: Int,
    dotSizePx: Float,
    activeWidthPx: Float,
    spacingPx: Float
): List<Float> {
    val positions = mutableListOf<Float>()
    var x = 0f
    for (i in 0 until pageCount) {
        positions.add(x)
        val elementWidth = if (i == currentPage) activeWidthPx else dotSizePx
        x += elementWidth + spacingPx
    }
    return positions
}

/**
 * Calculates the worm body position (left and right edges) based on page offset.
 *
 * At rest (offset = 0), the worm is a strip of activeWidthPx at the current page position.
 * During scroll, the strip stretches with a worm effect:
 * - Scrolling forward: right edge (leading) moves first toward next position,
 *   left edge (trailing) follows with delay.
 * - Scrolling backward: left edge (leading) moves first toward previous position,
 *   right edge (trailing) follows with delay.
 *
 * The target position accounts for the layout shift: when the active strip moves
 * to the next page, that next page becomes the new strip and the current page
 * becomes a dot.
 */
fun calculateWormPosition(
    currentPage: Int,
    pageOffsetFraction: Float,
    pageCount: Int,
    dotSizePx: Float,
    activeWidthPx: Float,
    spacingPx: Float
): WormPosition {
    // Current positions (with currentPage as active strip)
    val currentPositions = calculateElementPositions(
        pageCount = pageCount,
        currentPage = currentPage,
        dotSizePx = dotSizePx,
        activeWidthPx = activeWidthPx,
        spacingPx = spacingPx
    )

    // Base position of the active strip
    val baseLeft = currentPositions[currentPage]
    val baseRight = baseLeft + activeWidthPx

    val fraction = pageOffsetFraction.coerceIn(-1f, 1f)

    return when {
        fraction >= 0f && currentPage < pageCount - 1 -> {
            // Scrolling forward: strip moves from currentPage toward nextPage
            // Target: positions as if nextPage is active
            val targetPositions = calculateElementPositions(
                pageCount = pageCount,
                currentPage = currentPage + 1,
                dotSizePx = dotSizePx,
                activeWidthPx = activeWidthPx,
                spacingPx = spacingPx
            )
            val targetLeft = targetPositions[currentPage + 1]
            val targetRight = targetLeft + activeWidthPx

            // Leading edge (right) moves faster
            val leadingEased = accelerateEasing(fraction)
            // Trailing edge (left) moves slower
            val trailingEased = decelerateEasing(fraction)

            val right = baseRight + (targetRight - baseRight) * leadingEased
            val left = baseLeft + (targetLeft - baseLeft) * trailingEased

            WormPosition(left = left, right = right)
        }
        fraction < 0f && currentPage > 0 -> {
            // Scrolling backward: strip moves from currentPage toward prevPage
            val absFraction = -fraction

            val targetPositions = calculateElementPositions(
                pageCount = pageCount,
                currentPage = currentPage - 1,
                dotSizePx = dotSizePx,
                activeWidthPx = activeWidthPx,
                spacingPx = spacingPx
            )
            val targetLeft = targetPositions[currentPage - 1]
            val targetRight = targetLeft + activeWidthPx

            // Leading edge (left) moves faster backward
            val leadingEased = accelerateEasing(absFraction)
            // Trailing edge (right) moves slower
            val trailingEased = decelerateEasing(absFraction)

            val left = baseLeft + (targetLeft - baseLeft) * leadingEased
            val right = baseRight + (targetRight - baseRight) * trailingEased

            WormPosition(left = left, right = right)
        }
        else -> {
            // At rest or at boundary (first page scrolling back / last page scrolling forward)
            WormPosition(left = baseLeft, right = baseRight)
        }
    }
}

/**
 * Accelerate easing: moves quickly at the beginning, slows down at the end.
 * Used for the leading edge of the worm.
 * f(x) = 1 - (1 - x)^2  (inverse decelerate)
 */
internal fun accelerateEasing(fraction: Float): Float {
    val clamped = fraction.coerceIn(0f, 1f)
    return 1f - (1f - clamped) * (1f - clamped)
}

/**
 * Decelerate easing: moves slowly at the beginning, speeds up at the end.
 * Used for the trailing edge of the worm (stays behind, then catches up).
 * f(x) = x^2
 */
internal fun decelerateEasing(fraction: Float): Float {
    val clamped = fraction.coerceIn(0f, 1f)
    return clamped * clamped
}

@Preview(showBackground = true)
@Composable
private fun WormPageIndicatorPreviewPage0() {
    QlosirTheme {
        WormPageIndicator(
            pageCount = 3,
            currentPage = 0,
            pageOffsetFraction = 0f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WormPageIndicatorPreviewMidScroll() {
    QlosirTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // At page 0, 25% scrolled to page 1
            WormPageIndicator(
                pageCount = 3,
                currentPage = 0,
                pageOffsetFraction = 0.25f
            )
            // At page 0, 50% scrolled to page 1
            WormPageIndicator(
                pageCount = 3,
                currentPage = 0,
                pageOffsetFraction = 0.5f
            )
            // At page 0, 75% scrolled to page 1
            WormPageIndicator(
                pageCount = 3,
                currentPage = 0,
                pageOffsetFraction = 0.75f
            )
            // At page 1, settled
            WormPageIndicator(
                pageCount = 3,
                currentPage = 1,
                pageOffsetFraction = 0f
            )
            // At page 1, 50% scrolled to page 2
            WormPageIndicator(
                pageCount = 3,
                currentPage = 1,
                pageOffsetFraction = 0.5f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WormPageIndicatorPreviewLastPage() {
    QlosirTheme {
        WormPageIndicator(
            pageCount = 3,
            currentPage = 2,
            pageOffsetFraction = 0f
        )
    }
}

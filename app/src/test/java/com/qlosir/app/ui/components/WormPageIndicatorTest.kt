package com.qlosir.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WormPageIndicatorTest {

    // Test constants matching default values (approximate px at 3x density)
    private val dotSizePx = 24f  // 8.dp
    private val activeWidthPx = 78f // 26.dp
    private val spacingPx = 21f  // 7.dp

    // --- calculateElementPositions tests ---

    @Test
    fun `element positions - active at page 0`() {
        val positions = calculateElementPositions(
            pageCount = 3,
            currentPage = 0,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Page 0 (active strip): starts at 0, width = 78
        assertEquals(0f, positions[0], 0.01f)
        // Page 1 (dot): starts at 78 + 21 = 99
        assertEquals(99f, positions[1], 0.01f)
        // Page 2 (dot): starts at 99 + 24 + 21 = 144
        assertEquals(144f, positions[2], 0.01f)
    }

    @Test
    fun `element positions - active at page 1`() {
        val positions = calculateElementPositions(
            pageCount = 3,
            currentPage = 1,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Page 0 (dot): starts at 0, width = 24
        assertEquals(0f, positions[0], 0.01f)
        // Page 1 (active strip): starts at 24 + 21 = 45
        assertEquals(45f, positions[1], 0.01f)
        // Page 2 (dot): starts at 45 + 78 + 21 = 144
        assertEquals(144f, positions[2], 0.01f)
    }

    @Test
    fun `element positions - active at page 2`() {
        val positions = calculateElementPositions(
            pageCount = 3,
            currentPage = 2,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Page 0 (dot): starts at 0, width = 24
        assertEquals(0f, positions[0], 0.01f)
        // Page 1 (dot): starts at 24 + 21 = 45
        assertEquals(45f, positions[1], 0.01f)
        // Page 2 (active strip): starts at 45 + 24 + 21 = 90
        assertEquals(90f, positions[2], 0.01f)
    }

    // --- calculateWormPosition tests ---

    @Test
    fun `offset 0 at page 0 - worm is strip at first position`() {
        val position = calculateWormPosition(
            currentPage = 0,
            pageOffsetFraction = 0f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        assertEquals(0f, position.left, 0.01f)
        assertEquals(activeWidthPx, position.right, 0.01f)
    }

    @Test
    fun `offset 0 at page 1 - worm is strip at second position`() {
        val position = calculateWormPosition(
            currentPage = 1,
            pageOffsetFraction = 0f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // When page 1 is active: page 0 is dot (24) + spacing (21) = 45
        assertEquals(45f, position.left, 0.01f)
        assertEquals(45f + activeWidthPx, position.right, 0.01f)
    }

    @Test
    fun `offset 0 at last page - worm is strip at last position`() {
        val position = calculateWormPosition(
            currentPage = 2,
            pageOffsetFraction = 0f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // When page 2 is active: page 0 dot (24+21) + page 1 dot (24+21) = 90
        assertEquals(90f, position.left, 0.01f)
        assertEquals(90f + activeWidthPx, position.right, 0.01f)
    }

    @Test
    fun `offset 1 forward from page 0 - worm fully at page 1 position`() {
        val position = calculateWormPosition(
            currentPage = 0,
            pageOffsetFraction = 1f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Target: page 1 active, so strip starts at dot(24) + spacing(21) = 45
        // At fraction 1.0, both easings return 1.0, so position = target
        assertEquals(45f, position.left, 0.01f)
        assertEquals(45f + activeWidthPx, position.right, 0.01f)
    }

    @Test
    fun `offset 0_5 forward - worm stretches between page 0 and page 1`() {
        val position = calculateWormPosition(
            currentPage = 0,
            pageOffsetFraction = 0.5f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Base: left=0, right=78
        // Target (page 1 active): left=45, right=45+78=123
        // accelerateEasing(0.5) = 0.75 -> right moves 75% toward target
        // decelerateEasing(0.5) = 0.25 -> left moves 25% toward target

        val expectedLeft = 0f + (45f - 0f) * 0.25f   // 11.25
        val expectedRight = 78f + (123f - 78f) * 0.75f // 78 + 33.75 = 111.75

        assertEquals(expectedLeft, position.left, 0.01f)
        assertEquals(expectedRight, position.right, 0.01f)
    }

    @Test
    fun `worm stretches during mid-scroll - width larger than active strip`() {
        val position = calculateWormPosition(
            currentPage = 0,
            pageOffsetFraction = 0.5f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        val wormWidth = position.right - position.left
        assertTrue(
            "Worm should be stretched wider than active strip during mid-scroll",
            wormWidth > activeWidthPx
        )
    }

    @Test
    fun `negative offset - worm moves backward from page 1`() {
        val position = calculateWormPosition(
            currentPage = 1,
            pageOffsetFraction = -0.5f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Base (page 1 active): left=45, right=45+78=123
        // Target (page 0 active): left=0, right=78
        // Leading (left) uses accelerateEasing(0.5) = 0.75
        // Trailing (right) uses decelerateEasing(0.5) = 0.25

        val expectedLeft = 45f + (0f - 45f) * 0.75f    // 45 - 33.75 = 11.25
        val expectedRight = 123f + (78f - 123f) * 0.25f // 123 - 11.25 = 111.75

        assertEquals(expectedLeft, position.left, 0.01f)
        assertEquals(expectedRight, position.right, 0.01f)
    }

    @Test
    fun `at boundary - first page scrolling backward stays at rest`() {
        val position = calculateWormPosition(
            currentPage = 0,
            pageOffsetFraction = -0.5f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Should stay at rest position since there's no previous page
        assertEquals(0f, position.left, 0.01f)
        assertEquals(activeWidthPx, position.right, 0.01f)
    }

    @Test
    fun `at boundary - last page scrolling forward stays at rest`() {
        val position = calculateWormPosition(
            currentPage = 2,
            pageOffsetFraction = 0.5f,
            pageCount = 3,
            dotSizePx = dotSizePx,
            activeWidthPx = activeWidthPx,
            spacingPx = spacingPx
        )

        // Should stay at rest position since there's no next page
        assertEquals(90f, position.left, 0.01f)
        assertEquals(90f + activeWidthPx, position.right, 0.01f)
    }

    // --- Easing function tests ---

    @Test
    fun `accelerateEasing returns 0 for input 0`() {
        assertEquals(0f, accelerateEasing(0f), 0.001f)
    }

    @Test
    fun `accelerateEasing returns 1 for input 1`() {
        assertEquals(1f, accelerateEasing(1f), 0.001f)
    }

    @Test
    fun `accelerateEasing at 0_5 is greater than 0_5 (moves fast early)`() {
        val result = accelerateEasing(0.5f)
        assertTrue("accelerateEasing(0.5) should be > 0.5", result > 0.5f)
        assertEquals(0.75f, result, 0.001f)
    }

    @Test
    fun `decelerateEasing returns 0 for input 0`() {
        assertEquals(0f, decelerateEasing(0f), 0.001f)
    }

    @Test
    fun `decelerateEasing returns 1 for input 1`() {
        assertEquals(1f, decelerateEasing(1f), 0.001f)
    }

    @Test
    fun `decelerateEasing at 0_5 is less than 0_5 (moves slow early)`() {
        val result = decelerateEasing(0.5f)
        assertTrue("decelerateEasing(0.5) should be < 0.5", result < 0.5f)
        assertEquals(0.25f, result, 0.001f)
    }

    // --- Behavioral tests ---

    @Test
    fun `leading edge always ahead of trailing edge during forward scroll`() {
        val fractions = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f)

        for (fraction in fractions) {
            val position = calculateWormPosition(
                currentPage = 0,
                pageOffsetFraction = fraction,
                pageCount = 3,
                dotSizePx = dotSizePx,
                activeWidthPx = activeWidthPx,
                spacingPx = spacingPx
            )
            assertTrue(
                "Right edge should always be ahead of left edge at fraction $fraction",
                position.right > position.left
            )
        }
    }

    @Test
    fun `worm width peaks at mid-scroll and returns to strip width at endpoints`() {
        val widthAtStart = run {
            val pos = calculateWormPosition(0, 0f, 3, dotSizePx, activeWidthPx, spacingPx)
            pos.right - pos.left
        }
        val widthAtMid = run {
            val pos = calculateWormPosition(0, 0.5f, 3, dotSizePx, activeWidthPx, spacingPx)
            pos.right - pos.left
        }
        val widthAtEnd = run {
            val pos = calculateWormPosition(0, 1f, 3, dotSizePx, activeWidthPx, spacingPx)
            pos.right - pos.left
        }

        assertEquals("Width at start should be active strip width", activeWidthPx, widthAtStart, 0.01f)
        assertEquals("Width at end should be active strip width", activeWidthPx, widthAtEnd, 0.01f)
        assertTrue("Width at mid-scroll should be larger than strip width", widthAtMid > activeWidthPx)
    }
}

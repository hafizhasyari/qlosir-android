package com.qlosir.app.ui.createpin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qlosir.app.R
import com.qlosir.app.ui.theme.BrandPrimaryBlue
import com.qlosir.app.ui.theme.DarkPrimaryText
import com.qlosir.app.ui.theme.PlusJakartaSansFontFamily
import com.qlosir.app.ui.theme.QlosirTheme
import com.qlosir.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

// Design tokens from the HTML design spec
private val IconBgColor = Color(0xFFE9F1FE)
private val DotFilledColor = Color(0xFF1B62E0)
private val DotEmptyBorderColor = Color(0xFFC4D2E6)
private val KeypadBgColor = Color(0xFFF4F7FB)
private val BackButtonBgColor = Color(0xFFF0F4FA)
private val DeleteIconColor = Color(0xFF5A6B84)
private val ErrorColor = Color(0xFFF04438)

/**
 * Create PIN Screen matching the Qlosir design spec.
 *
 * Layout:
 * - Back button (40dp, rounded 12dp, #F0F4FA background)
 * - Lock icon in rounded box (72dp, #E9F1FE background)
 * - Title "Buat PIN Keamanan" (24sp, ExtraBold, centered)
 * - Subtitle (14sp, Medium, centered)
 * - 6 PIN dots (16dp each, filled blue / empty with border)
 * - Number pad (3x4 grid, 64dp height buttons, 22dp rounded, #F4F7FB bg)
 * - Delete button with backspace icon
 *
 * The screen handles two states:
 * 1. Create PIN (initial) - user enters 6-digit PIN
 * 2. Confirm PIN - user re-enters PIN to verify
 */
@Composable
fun CreatePinScreen(
    onNavigate: (CreatePinNavigationEvent) -> Unit,
    viewModel: CreatePinViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            onNavigate(event)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackButtonBgColor)
                        .clickable(
                            role = Role.Button,
                            onClick = { viewModel.onBackPressed() }
                        )
                        .semantics {
                            contentDescription = "Back"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon()
                }
            }

            // Main content area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Lock icon container
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(IconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isConfirmStep) {
                        ShieldCheckIcon()
                    } else {
                        LockIcon()
                    }
                }

                // Title
                Text(
                    text = if (uiState.isConfirmStep) {
                        stringResource(R.string.create_pin_confirm_title)
                    } else {
                        stringResource(R.string.create_pin_title)
                    },
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = DarkPrimaryText,
                    letterSpacing = (-0.4).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 22.dp)
                )

                // Subtitle
                Text(
                    text = if (uiState.isConfirmStep) {
                        stringResource(R.string.create_pin_confirm_subtitle)
                    } else {
                        stringResource(R.string.create_pin_subtitle)
                    },
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(280.dp)
                )

                // PIN dots
                Row(
                    modifier = Modifier.padding(top = 34.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(CreatePinUiState.PIN_LENGTH) { index ->
                        PinDot(filled = index < uiState.filledDots)
                    }
                }

                // Error message
                if (uiState.errorMessage != null) {
                    Text(
                        text = stringResource(uiState.errorMessage!!),
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = ErrorColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                // Flexible spacer to push keypad to bottom
                Spacer(modifier = Modifier.weight(1f))

                // Number pad
                NumberPad(
                    onDigitPressed = viewModel::onDigitPressed,
                    onDeletePressed = viewModel::onDeletePressed,
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}

/**
 * A single PIN dot indicator.
 * Filled: solid #1B62E0 circle
 * Empty: white circle with 2px #C4D2E6 border
 */
@Composable
private fun PinDot(filled: Boolean) {
    val animatedColor by animateColorAsState(
        targetValue = if (filled) DotFilledColor else Color.White,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "dotFill"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (filled) 1f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "dotScale"
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .then(
                if (filled) {
                    Modifier
                        .clip(CircleShape)
                        .background(animatedColor)
                } else {
                    Modifier
                        .border(2.dp, DotEmptyBorderColor, CircleShape)
                        .background(Color.White, CircleShape)
                }
            )
    )
}

/**
 * 3x4 number pad grid matching design spec.
 * Buttons: 64dp height, rounded 22dp, #F4F7FB background, 26sp bold text.
 * Layout: 1-9, empty, 0, delete
 */
@Composable
private fun NumberPad(
    onDigitPressed: (Int) -> Unit,
    onDeletePressed: () -> Unit,
    enabled: Boolean
) {
    val keys = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
        listOf(-1, 0, -2) // -1 = empty, -2 = delete
    )

    Column(
        modifier = Modifier.width(280.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                row.forEach { key ->
                    when (key) {
                        -1 -> {
                            // Empty space
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                            )
                        }
                        -2 -> {
                            // Delete button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .clickable(
                                        enabled = enabled,
                                        role = Role.Button,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = onDeletePressed
                                    )
                                    .semantics {
                                        contentDescription = "Delete"
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                DeleteIcon()
                            }
                        }
                        else -> {
                            // Digit button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(KeypadBgColor)
                                    .clickable(
                                        enabled = enabled,
                                        role = Role.Button,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onDigitPressed(key) }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key.toString(),
                                    fontFamily = PlusJakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = DarkPrimaryText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Back arrow icon matching design SVG:
 * stroke="#12203A" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"
 * path d="M15 18 9 12l6-6"
 */
@Composable
private fun BackArrowIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val path = Path().apply {
            moveTo(15f * scaleX, 18f * scaleY)
            lineTo(9f * scaleX, 12f * scaleY)
            lineTo(15f * scaleX, 6f * scaleY)
        }
        drawPath(
            path = path,
            color = Color(0xFF12203A),
            style = Stroke(
                width = 2.4f * scaleX,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

/**
 * Lock icon matching design SVG:
 * stroke="#1B62E0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
 * rect x="4" y="10" width="16" height="11" rx="2.5"
 * path "M8 10V7a4 4 0 0 1 8 0v3"
 * circle cx="12" cy="15.5" r="1.4" fill="#1B62E0"
 */
@Composable
private fun LockIcon() {
    Canvas(modifier = Modifier.size(34.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val strokeWidth = 2f * scaleX

        // Lock body (rounded rectangle)
        val bodyPath = Path().apply {
            val left = 4f * scaleX
            val top = 10f * scaleY
            val right = 20f * scaleX
            val bottom = 21f * scaleY
            val rx = 2.5f * scaleX
            val ry = 2.5f * scaleY
            moveTo(left + rx, top)
            lineTo(right - rx, top)
            cubicTo(right, top, right, top, right, top + ry)
            lineTo(right, bottom - ry)
            cubicTo(right, bottom, right, bottom, right - rx, bottom)
            lineTo(left + rx, bottom)
            cubicTo(left, bottom, left, bottom, left, bottom - ry)
            lineTo(left, top + ry)
            cubicTo(left, top, left, top, left + rx, top)
            close()
        }
        drawPath(
            path = bodyPath,
            color = BrandPrimaryBlue,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Lock shackle (arch)
        val shacklePath = Path().apply {
            moveTo(8f * scaleX, 10f * scaleY)
            lineTo(8f * scaleX, 7f * scaleY)
            cubicTo(
                8f * scaleX, 4.8f * scaleY,
                9.8f * scaleX, 3f * scaleY,
                12f * scaleX, 3f * scaleY
            )
            cubicTo(
                14.2f * scaleX, 3f * scaleY,
                16f * scaleX, 4.8f * scaleY,
                16f * scaleX, 7f * scaleY
            )
            lineTo(16f * scaleX, 10f * scaleY)
        }
        drawPath(
            path = shacklePath,
            color = BrandPrimaryBlue,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Keyhole dot
        drawCircle(
            color = BrandPrimaryBlue,
            radius = 1.4f * scaleX,
            center = androidx.compose.ui.geometry.Offset(12f * scaleX, 15.5f * scaleY)
        )
    }
}

/**
 * Shield check icon for the confirm PIN step.
 * SVG: stroke="#1B62E0" stroke-width="2"
 * path d="M12 3 4 6v6c0 5 3.5 8 8 9 4.5-1 8-4 8-9V6l-8-3Z"
 * path d="m9 12 2 2 4-4"
 */
@Composable
private fun ShieldCheckIcon() {
    Canvas(modifier = Modifier.size(34.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val strokeWidth = 2f * scaleX

        // Shield outline
        val shieldPath = Path().apply {
            moveTo(12f * scaleX, 3f * scaleY)
            lineTo(4f * scaleX, 6f * scaleY)
            lineTo(4f * scaleX, 12f * scaleY)
            cubicTo(
                4f * scaleX, 17f * scaleY,
                7.5f * scaleX, 20f * scaleY,
                12f * scaleX, 21f * scaleY
            )
            cubicTo(
                16.5f * scaleX, 20f * scaleY,
                20f * scaleX, 17f * scaleY,
                20f * scaleX, 12f * scaleY
            )
            lineTo(20f * scaleX, 6f * scaleY)
            close()
        }
        drawPath(
            path = shieldPath,
            color = BrandPrimaryBlue,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Checkmark
        val checkPath = Path().apply {
            moveTo(9f * scaleX, 12f * scaleY)
            lineTo(11f * scaleX, 14f * scaleY)
            lineTo(15f * scaleX, 10f * scaleY)
        }
        drawPath(
            path = checkPath,
            color = BrandPrimaryBlue,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Delete/backspace icon matching design SVG:
 * stroke="#5A6B84" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
 * path d="M21 5H8.5L2 12l6.5 7H21a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1Z"
 * path d="M12 9.5 16.5 14M16.5 9.5 12 14"
 */
@Composable
private fun DeleteIcon() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val strokeWidth = 2f * scaleX

        // Backspace shape
        val shapePath = Path().apply {
            moveTo(21f * scaleX, 5f * scaleY)
            lineTo(8.5f * scaleX, 5f * scaleY)
            lineTo(2f * scaleX, 12f * scaleY)
            lineTo(8.5f * scaleX, 19f * scaleY)
            lineTo(21f * scaleX, 19f * scaleY)
            cubicTo(
                22f * scaleX, 19f * scaleY,
                22f * scaleX, 19f * scaleY,
                22f * scaleX, 18f * scaleY
            )
            lineTo(22f * scaleX, 6f * scaleY)
            cubicTo(
                22f * scaleX, 5f * scaleY,
                22f * scaleX, 5f * scaleY,
                21f * scaleX, 5f * scaleY
            )
            close()
        }
        drawPath(
            path = shapePath,
            color = DeleteIconColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // X marks
        val x1Path = Path().apply {
            moveTo(12f * scaleX, 9.5f * scaleY)
            lineTo(16.5f * scaleX, 14f * scaleY)
        }
        drawPath(
            path = x1Path,
            color = DeleteIconColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val x2Path = Path().apply {
            moveTo(16.5f * scaleX, 9.5f * scaleY)
            lineTo(12f * scaleX, 14f * scaleY)
        }
        drawPath(
            path = x2Path,
            color = DeleteIconColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreatePinScreenPreview() {
    QlosirTheme {
        CreatePinScreen(onNavigate = {})
    }
}

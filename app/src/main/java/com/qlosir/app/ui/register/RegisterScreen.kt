package com.qlosir.app.ui.register

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.qlosir.app.R
import com.qlosir.app.ui.theme.BrandPrimaryBlue
import com.qlosir.app.ui.theme.DarkPrimaryText
import com.qlosir.app.ui.theme.PlusJakartaSansFontFamily
import com.qlosir.app.ui.theme.QlosirTheme
import com.qlosir.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

private val HeaderBgColor = Color(0xFFF0F4FA)
private val BorderUnfocusedColor = Color(0xFFD7E0EE)
private val BorderFocusedColor = Color(0xFF1B62E0)
private val BorderErrorColor = Color(0xFFF04438)
private val PlaceholderColor = Color(0xFF9AA7BC)
private val LabelUnfocusedColor = Color(0xFF6B7A91)
private val TermsTextColor = Color(0xFF8593A8)

/**
 * Store Registration Screen Composable matching Qlosir Onboarding design.
 */
@Composable
fun RegisterScreen(
    onNavigate: (RegisterNavigationEvent) -> Unit,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Top Bar with Back Button and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                val backButtonDesc = stringResource(R.string.register_back_button)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HeaderBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { viewModel.onBackClicked() }
                        )
                        .semantics {
                            contentDescription = backButtonDesc
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.625f, size.height * 0.75f)
                            lineTo(size.width * 0.375f, size.height * 0.5f)
                            lineTo(size.width * 0.625f, size.height * 0.25f)
                        }
                        drawPath(
                            path = path,
                            color = DarkPrimaryText,
                            style = Stroke(
                                width = 2.4.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = stringResource(R.string.register_title),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = DarkPrimaryText,
                    letterSpacing = (-0.3).sp
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.register_subtitle),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Form Fields
                FloatingLabelTextField(
                    label = stringResource(R.string.register_field_store_name),
                    value = uiState.storeName,
                    onValueChange = viewModel::onStoreNameChanged,
                    placeholder = stringResource(R.string.register_placeholder_store_name),
                    errorMessage = uiState.storeNameError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FloatingLabelTextField(
                    label = stringResource(R.string.register_field_owner_name),
                    value = uiState.ownerName,
                    onValueChange = viewModel::onOwnerNameChanged,
                    placeholder = stringResource(R.string.register_placeholder_owner_name),
                    errorMessage = uiState.ownerNameError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FloatingLabelTextField(
                    label = stringResource(R.string.register_field_phone),
                    value = uiState.phone,
                    onValueChange = viewModel::onPhoneChanged,
                    placeholder = stringResource(R.string.register_placeholder_phone),
                    errorMessage = uiState.phoneError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                FloatingLabelTextField(
                    label = stringResource(R.string.register_field_email),
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    placeholder = stringResource(R.string.register_placeholder_email),
                    errorMessage = uiState.emailError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                FloatingLabelTextField(
                    label = stringResource(R.string.register_field_password),
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = stringResource(R.string.register_placeholder_password),
                    errorMessage = uiState.passwordError?.let { stringResource(it) },
                    isPassword = true,
                    isPasswordVisible = uiState.isPasswordVisible,
                    onPasswordToggle = viewModel::togglePasswordVisibility,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Terms & Conditions Disclaimer
                Text(
                    text = stringResource(R.string.register_terms),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp,
                    color = TermsTextColor,
                    lineHeight = 17.25.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button ("Lanjut" / "Continue")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = BrandPrimaryBlue.copy(alpha = 0.32f)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandPrimaryBlue)
                        .clickable(
                            enabled = !uiState.isLoading,
                            role = Role.Button,
                            onClick = { viewModel.onSubmit() }
                        )
                        .padding(vertical = 17.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.register_button_continue),
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Custom Input Field styled to match Qlosir floating-label design tokens & design typing indicator.
 */
@Composable
private fun FloatingLabelTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var isFocused by remember { mutableStateOf(false) }

    val currentBorderColor by animateColorAsState(
        targetValue = when {
            errorMessage != null -> BorderErrorColor
            isFocused -> BorderFocusedColor
            else -> BorderUnfocusedColor
        },
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "borderColorAnimation"
    )

    val currentLabelColor by animateColorAsState(
        targetValue = when {
            errorMessage != null -> BorderErrorColor
            isFocused -> BorderFocusedColor
            else -> LabelUnfocusedColor
        },
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "labelColorAnimation"
    )

    // Pulsing blinking animation for active typing indicator bar (2.dp x 20.dp blue bar matching Qlosir Onboarding prototype)
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlinkTransition")
    val typingBarAlpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "typingBarAlpha"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            // Field Border Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.6.dp,
                        color = currentBorderColor,
                        shape = RoundedCornerShape(13.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                color = PlaceholderColor
                            )
                        }

                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isFocused = it.isFocused },
                            textStyle = TextStyle(
                                fontFamily = PlusJakartaSansFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = DarkPrimaryText
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(BorderFocusedColor),
                            visualTransformation = if (isPassword && !isPasswordVisible) {
                                PasswordVisualTransformation()
                            } else {
                                VisualTransformation.None
                            },
                            keyboardOptions = keyboardOptions
                        )
                    }

                    if (isPassword && onPasswordToggle != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedPasswordEyeIcon(
                            isPasswordVisible = isPasswordVisible,
                            onToggle = onPasswordToggle
                        )
                    }
                }
            }

            // Floating Label Overlay — positioned at top:-8px to overlap the border
            Text(
                text = label,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
                fontSize = 12.sp,
                color = currentLabelColor,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 12.dp, y = (-8).dp)
                    .background(Color.White)
                    .padding(horizontal = 6.dp)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = BorderErrorColor,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Animated Password Eye Icon with scale spring rebound and animated slash-through line morphing.
 */
@Composable
private fun AnimatedPasswordEyeIcon(
    isPasswordVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animated slash progress: 0f when visible (no slash), 1f when hidden (slash drawn across)
    val slashProgress by animateFloatAsState(
        targetValue = if (isPasswordVisible) 0f else 1f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "eyeSlashProgress"
    )

    // Animated click scale rebound effect
    var isPressed by remember { mutableStateOf(false) }
    val iconScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "eyeClickScale"
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = iconScale
                scaleY = iconScale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = true
                    onToggle()
                }
            )
            .semantics {
                contentDescription = "Toggle password visibility"
            },
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }

        Canvas(modifier = Modifier.size(20.dp)) {
            val strokeWidth = 2.dp.toPx()
            val color = TextSecondary

            // Outer eye arc path
            val eyePath = Path().apply {
                moveTo(size.width * 0.083f, size.height * 0.5f)
                cubicTo(
                    size.width * 0.23f, size.height * 0.21f,
                    size.width * 0.5f, size.height * 0.21f,
                    size.width * 0.917f, size.height * 0.5f
                )
                cubicTo(
                    size.width * 0.77f, size.height * 0.79f,
                    size.width * 0.5f, size.height * 0.79f,
                    size.width * 0.083f, size.height * 0.5f
                )
            }
            drawPath(
                path = eyePath,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Eye Pupil Circle
            drawCircle(
                color = color,
                radius = 3.dp.toPx(),
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Animated Slash-Through Line (drawn from top-left to bottom-right based on slashProgress)
            if (slashProgress > 0f) {
                val slashPath = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.18f)
                    lineTo(size.width * 0.82f, size.height * 0.82f)
                }
                val pathMeasure = PathMeasure().apply {
                    setPath(slashPath, false)
                }
                val partialPath = Path()
                pathMeasure.getSegment(0f, pathMeasure.length * slashProgress, partialPath, true)

                drawPath(
                    path = partialPath,
                    color = color,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    QlosirTheme {
        RegisterScreen(onNavigate = {})
    }
}

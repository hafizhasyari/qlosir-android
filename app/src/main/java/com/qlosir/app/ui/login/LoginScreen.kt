package com.qlosir.app.ui.login

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qlosir.app.R
import com.qlosir.app.ui.theme.BrandPrimaryBlue
import com.qlosir.app.ui.theme.DarkPrimaryText
import com.qlosir.app.ui.theme.PlusJakartaSansFontFamily
import com.qlosir.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

// Design tokens from the HTML design spec
private val GradientStart = Color(0xFF3288F6)
private val GradientEnd = Color(0xFF124FC2)
private val BorderUnfocusedColor = Color(0xFFD7E0EE)
private val BorderFocusedColor = Color(0xFF1B62E0)
private val BorderErrorColor = Color(0xFFF04438)
private val PlaceholderColor = Color(0xFF9AA7BC)
private val LabelUnfocusedColor = Color(0xFF6B7A91)
private val EyeIconColor = Color(0xFF8593A8)
private val AccentAmber = Color(0xFFFFB020)

/**
 * Login Screen matching the Qlosir Onboarding design spec.
 *
 * Layout:
 * - Qlosir logo (60dp, gradient blue, rounded 18dp)
 * - Title "Masuk ke Warungmu" (26sp, ExtraBold, centered)
 * - Subtitle (14sp, Medium, centered)
 * - Phone floating-label text field
 * - Password floating-label text field with eye toggle
 * - "Lupa password?" link (right-aligned, 13.5sp, Bold, blue)
 * - Login button (blue, rounded 16dp, shadow)
 * - "Belum punya akun? Daftar" at bottom
 */
@Composable
fun LoginScreen(
    onNavigate: (LoginNavigationEvent) -> Unit,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                .padding(horizontal = 28.dp, vertical = 20.dp)
        ) {
            // Top spacing + Logo (centered)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Qlosir Logo - 60dp with gradient background and icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0xFF123C8C).copy(alpha = 0.28f)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(GradientStart, GradientEnd),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        )
                        .semantics { contentDescription = "Logo Qlosir" },
                    contentAlignment = Alignment.Center
                ) {
                    QlosirLogoIcon(modifier = Modifier.size(40.dp))
                }
            }

            // Title
            Text(
                text = stringResource(R.string.login_title),
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = DarkPrimaryText,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp)
            )

            // Subtitle
            Text(
                text = stringResource(R.string.login_subtitle),
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Phone Number Field
            LoginFloatingLabelTextField(
                label = stringResource(R.string.login_field_phone),
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChanged,
                placeholder = stringResource(R.string.login_placeholder_phone),
                errorMessage = uiState.phoneError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            LoginFloatingLabelTextField(
                label = stringResource(R.string.login_field_password),
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = stringResource(R.string.login_placeholder_password),
                errorMessage = uiState.passwordError?.let { stringResource(it) },
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordToggle = viewModel::togglePasswordVisibility,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Forgot Password Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(R.string.login_forgot_password),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = BrandPrimaryBlue,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { viewModel.onForgotPasswordClicked() }
                    )
                )
            }

            // Login Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
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
                        onClick = { viewModel.onLoginClicked() }
                    )
                    .padding(vertical = 17.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            // Spacer to push register link to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Register Link at bottom
            val annotatedRegisterString = buildAnnotatedString {
                append(stringResource(R.string.login_no_account))
                append(" ")
                withStyle(
                    style = SpanStyle(
                        fontFamily = PlusJakartaSansFontFamily,
                        color = BrandPrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(stringResource(R.string.login_register))
                }
            }
            Text(
                text = annotatedRegisterString,
                fontFamily = PlusJakartaSansFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { viewModel.onRegisterClicked() }
                    )
                    .padding(bottom = 8.dp)
            )
        }
    }
}

/**
 * Qlosir logo white icon drawn via Canvas (magnifying glass + checkmark).
 */
@Composable
private fun QlosirLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.width
        val scale = s / 96f

        scale(scale, pivot = Offset.Zero) {
            // Outer ring
            drawCircle(
                color = Color.White,
                radius = 27f,
                center = Offset(48f, 48f),
                style = Stroke(width = 7f)
            )

            // Inner hole (clear via white ring on top)
            drawCircle(
                color = Color.White,
                radius = 13f,
                center = Offset(48f, 48f),
                style = Stroke(width = 5f)
            )

            // Search handle
            val handlePath = Path().apply {
                moveTo(64f, 64f)
                lineTo(78f, 78f)
            }
            drawPath(
                path = handlePath,
                color = Color.White,
                style = Stroke(width = 11f, cap = StrokeCap.Round)
            )

            // Checkmark
            val checkPath = Path().apply {
                moveTo(40.5f, 47.5f)
                lineTo(46f, 53f)
                lineTo(55.5f, 42f)
            }
            drawPath(
                path = checkPath,
                color = AccentAmber,
                style = Stroke(
                    width = 5.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

/**
 * Floating-label text field styled to match the Login screen design spec.
 */
@Composable
private fun LoginFloatingLabelTextField(
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
        label = "borderColorAnim"
    )

    val currentLabelColor by animateColorAsState(
        targetValue = when {
            errorMessage != null -> BorderErrorColor
            isFocused -> BorderFocusedColor
            else -> LabelUnfocusedColor
        },
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "labelColorAnim"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            // Field container
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
                        LoginPasswordEyeIcon(
                            isPasswordVisible = isPasswordVisible,
                            onToggle = onPasswordToggle
                        )
                    }
                }
            }

            // Floating label
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = label,
                    fontFamily = PlusJakartaSansFontFamily,
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = currentLabelColor,
                    modifier = Modifier
                        .background(Color.White)
                        .padding(horizontal = 6.dp)
                )
            }
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
 * Password eye icon with animated slash-through for visibility toggle.
 */
@Composable
private fun LoginPasswordEyeIcon(
    isPasswordVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slashProgress by animateFloatAsState(
        targetValue = if (isPasswordVisible) 0f else 1f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "eyeSlash"
    )

    var isPressed by remember { mutableStateOf(false) }
    val iconScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "eyeScale"
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
            .semantics { contentDescription = "Toggle password visibility" },
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

            // Eye outline
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
                color = EyeIconColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Pupil
            drawCircle(
                color = EyeIconColor,
                radius = 3.dp.toPx(),
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Slash
            if (slashProgress > 0f) {
                val slashPath = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.18f)
                    lineTo(size.width * 0.82f, size.height * 0.82f)
                }
                val pathMeasure = PathMeasure().apply { setPath(slashPath, false) }
                val partialPath = Path()
                pathMeasure.getSegment(0f, pathMeasure.length * slashProgress, partialPath, true)
                drawPath(
                    path = partialPath,
                    color = EyeIconColor,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

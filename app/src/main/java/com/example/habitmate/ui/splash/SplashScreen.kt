package com.example.habitmate.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Light theme color palette (matching home screen)
private val BackgroundLight = Color(0xFFF8FAFC) // Slate-50
private val AccentBlue = Color(0xFF2563EB) // Blue-600
private val AccentCyan = Color(0xFF06B6D4) // Cyan-500
private val TextDark = Color(0xFF1E293B) // Slate-800
private val TextMuted = Color(0xFF64748B) // Slate-500

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
        val textToType = "HabitMate"
        var displayedText by remember { mutableStateOf("") }
        var startLogoAnimation by remember { mutableStateOf(false) }
        var showTagline by remember { mutableStateOf(false) }

        // Scale animation for logo
        val logoScale by
                animateFloatAsState(
                        targetValue = if (startLogoAnimation) 1f else 0.6f,
                        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
                        label = "logoScale"
                )

        // Alpha animation for logo
        val logoAlpha by
                animateFloatAsState(
                        targetValue = if (startLogoAnimation) 1f else 0f,
                        animationSpec = tween(durationMillis = 600),
                        label = "logoAlpha"
                )

        // Tagline alpha animation
        val taglineAlpha by
                animateFloatAsState(
                        targetValue = if (showTagline) 1f else 0f,
                        animationSpec = tween(durationMillis = 500),
                        label = "taglineAlpha"
                )

        // Infinite pulse animation for glow ring
        val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
        val glowAlpha by
                infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.5f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(1500, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "glowAlpha"
                )
        val glowScale by
                infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(1500, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "glowScale"
                )

        LaunchedEffect(Unit) {
                startLogoAnimation = true
                delay(500) // Wait for logo animation
                for (i in textToType.indices) {
                        displayedText = textToType.substring(0, i + 1)
                        delay(80) // Faster typing speed
                }
                delay(200)
                showTagline = true
                delay(1000) // Hold for viewing
                onNavigateToHome()
        }

        Box(
                modifier = Modifier.fillMaxSize().background(BackgroundLight),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        // Logo with subtle glow effect
                        Box(contentAlignment = Alignment.Center) {
                                // Subtle glowing ring effect
                                if (startLogoAnimation) {
                                        Canvas(modifier = Modifier.size(200.dp).scale(glowScale)) {
                                                // Outer gradient glow
                                                drawCircle(
                                                        brush =
                                                                Brush.radialGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        AccentBlue
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                glowAlpha *
                                                                                                                        0.3f
                                                                                                ),
                                                                                        AccentCyan
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                glowAlpha *
                                                                                                                        0.2f
                                                                                                ),
                                                                                        Color.Transparent
                                                                                )
                                                                ),
                                                        radius = size.minDimension / 2
                                                )
                                                // Inner ring
                                                drawCircle(
                                                        brush =
                                                                Brush.linearGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        AccentBlue,
                                                                                        AccentCyan
                                                                                )
                                                                ),
                                                        radius = size.minDimension / 2.8f,
                                                        style = Stroke(width = 3.dp.toPx()),
                                                        alpha = glowAlpha
                                                )
                                        }
                                }

                                // Logo image - using splashscreen_logo.PNG
                                Image(
                                        painter =
                                                painterResource(
                                                        id =
                                                                com.example
                                                                        .habitmate
                                                                        .R
                                                                        .drawable
                                                                        .splashscreen_logo
                                                ),
                                        contentDescription = "App Logo",
                                        modifier =
                                                Modifier.size(150.dp)
                                                        .scale(logoScale)
                                                        .alpha(logoAlpha)
                                                        .clip(CircleShape)
                                )
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        // App name with gradient-like appearance
                        Text(
                                text = displayedText,
                                style =
                                        MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 38.sp,
                                                letterSpacing = 0.5.sp,
                                                color = TextDark
                                        )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tagline with fade-in
                        Text(
                                text = "Build better habits, one day at a time",
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = TextMuted,
                                                letterSpacing = 0.3.sp
                                        ),
                                modifier = Modifier.alpha(taglineAlpha)
                        )
                }
        }
}

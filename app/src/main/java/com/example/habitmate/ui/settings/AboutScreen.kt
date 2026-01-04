package com.example.habitmate.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BackgroundColor = Color(0xFFF8FAFC)
private val CardSurface = Color(0xFFFFFFFF)
private val PrimaryBlue = Color(0xFF2563EB)
private val AccentCyan = Color(0xFF06B6D4)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        // Header
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        Brush.horizontalGradient(
                                                colors = listOf(PrimaryBlue, AccentCyan)
                                        )
                                )
                                .padding(top = 48.dp, bottom = 24.dp)
                                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                        onClick = onBack,
                        modifier =
                                Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                        text = "About",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
            }
        }

        // Content
        Column(
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo & Name
            Box(
                    modifier =
                            Modifier.size(100.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                            Brush.linearGradient(
                                                    colors = listOf(PrimaryBlue, AccentCyan)
                                            )
                                    ),
                    contentAlignment = Alignment.Center
            ) { Text(text = "🎯", fontSize = 48.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text = "HabitMate",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
            )

            Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = "Build better habits, one day at a time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Features Card
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                            text = "Features",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                    )

                    FeatureItem(Icons.Default.CheckCircle, "Track daily habits with ease")
                    FeatureItem(Icons.Default.TrendingUp, "Visualize your progress")
                    FeatureItem(Icons.Default.LocalFireDepartment, "Build streaks & stay motivated")
                    FeatureItem(Icons.Default.Notifications, "Get daily reminders")
                    FeatureItem(Icons.Default.Cloud, "Sync across devices")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Credits Card
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                            text = "Made with ❤️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                            text = "HabitMate was built using modern Android technologies:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                    )

                    TechItem("Kotlin")
                    TechItem("Jetpack Compose")
                    TechItem("Firebase Firestore")
                    TechItem("Material Design 3")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Copyright
            Text(
                    text = "© 2026 HabitMate. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, text: String) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun TechItem(name: String) {
    Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentCyan))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = name, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

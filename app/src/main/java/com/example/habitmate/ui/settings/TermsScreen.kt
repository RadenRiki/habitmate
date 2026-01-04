package com.example.habitmate.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val BackgroundColor = Color(0xFFF8FAFC)
private val PrimaryBlue = Color(0xFF2563EB)
private val AccentCyan = Color(0xFF06B6D4)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)

@Composable
fun TermsScreen(onBack: () -> Unit) {
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
                        text = "Terms of Use",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
            }
        }

        // Content
        Column(
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
        ) {
            Text(
                    text = "Last Updated: January 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
            )

            TermsSection(
                    title = "1. Acceptance of Terms",
                    content =
                            "By downloading, installing, or using HabitMate, you agree to be bound by these Terms of Use. If you do not agree to these terms, please do not use the application."
            )

            TermsSection(
                    title = "2. Use of the App",
                    content =
                            "HabitMate is designed to help you build and track daily habits. You may use this app for personal, non-commercial purposes only. You agree not to misuse the app or help anyone else do so."
            )

            TermsSection(
                    title = "3. User Data",
                    content =
                            "Your habit data is stored securely using Firebase cloud services. We do not sell or share your personal data with third parties. You retain ownership of all data you input into the app."
            )

            TermsSection(
                    title = "4. Account Responsibility",
                    content =
                            "You are responsible for maintaining the confidentiality of your device and account. Any activity that occurs under your account is your responsibility."
            )

            TermsSection(
                    title = "5. Intellectual Property",
                    content =
                            "All content, features, and functionality of HabitMate, including but not limited to text, graphics, logos, and software, are the exclusive property of HabitMate and are protected by copyright laws."
            )

            TermsSection(
                    title = "6. Disclaimer",
                    content =
                            "HabitMate is provided \"as is\" without warranties of any kind. We do not guarantee that the app will be error-free or uninterrupted. Use of the app is at your own risk."
            )

            TermsSection(
                    title = "7. Limitation of Liability",
                    content =
                            "In no event shall HabitMate be liable for any indirect, incidental, special, or consequential damages arising out of or in connection with your use of the app."
            )

            TermsSection(
                    title = "8. Changes to Terms",
                    content =
                            "We reserve the right to modify these terms at any time. Continued use of the app after changes indicates your acceptance of the new terms."
            )

            TermsSection(
                    title = "9. Contact",
                    content =
                            "If you have any questions about these Terms of Use, please contact us at support@habitmate.app"
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Justify
        )
    }
}

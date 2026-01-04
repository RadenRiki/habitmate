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
fun PrivacyScreen(onBack: () -> Unit) {
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
                        text = "Privacy Policy",
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
                    text = "Effective Date: January 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
            )

            PolicySection(
                    title = "Introduction",
                    content =
                            "At HabitMate, we take your privacy seriously. This Privacy Policy explains how we collect, use, and protect your personal information when you use our habit tracking application."
            )

            PolicySection(
                    title = "Information We Collect",
                    content =
                            "• Habit Data: The habits you create, track, and complete\n• Usage Data: How you interact with the app (anonymized)\n• Device Information: Basic device info for app optimization\n\nWe do NOT collect:\n• Your name or email (unless you provide it)\n• Location data\n• Contacts or photos"
            )

            PolicySection(
                    title = "How We Use Your Data",
                    content =
                            "Your data is used exclusively to:\n• Provide habit tracking functionality\n• Sync your data across devices\n• Improve app performance\n• Generate anonymized usage statistics"
            )

            PolicySection(
                    title = "Data Storage & Security",
                    content =
                            "Your data is stored securely using Google Firebase, which employs industry-standard encryption. We use:\n• SSL/TLS encryption for data in transit\n• AES-256 encryption for data at rest\n• Regular security audits"
            )

            PolicySection(
                    title = "Data Sharing",
                    content =
                            "We do NOT sell your data to third parties. Your habit data is yours and yours alone. We may share anonymized, aggregated statistics for research purposes only."
            )

            PolicySection(
                    title = "Your Rights",
                    content =
                            "You have the right to:\n• Access your data at any time\n• Export your data\n• Delete your data permanently\n• Opt out of analytics\n\nTo exercise these rights, contact us at privacy@habitmate.app"
            )

            PolicySection(
                    title = "Children's Privacy",
                    content =
                            "HabitMate is not intended for children under 13. We do not knowingly collect data from children under 13 years of age."
            )

            PolicySection(
                    title = "Changes to This Policy",
                    content =
                            "We may update this policy from time to time. We will notify you of any significant changes through the app or via email if provided."
            )

            PolicySection(
                    title = "Contact Us",
                    content =
                            "If you have questions about this Privacy Policy, please contact us at:\n\nprivacy@habitmate.app"
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
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
                textAlign = TextAlign.Start
        )
    }
}

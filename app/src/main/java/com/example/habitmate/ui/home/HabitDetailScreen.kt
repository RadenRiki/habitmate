package com.example.habitmate.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reuse colors from HomeScreen for consistency, or define shared theme object
private val PrimaryColor = Color(0xFF2563EB)
private val SecondaryColor = Color(0xFF06B6D4)
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val OceanGradient = Brush.linearGradient(colors = listOf(PrimaryColor, SecondaryColor))

@Composable
fun HabitDetailScreen(
        habit: HabitUi,
        onClose: () -> Unit,
        onEdit: (String) -> Unit,
        onDelete: (String) -> Unit,
        onToggleCompletion: (String) -> Unit,
        onDecrement: (String) -> Unit // Added callback
) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(CardSurface)
                                .padding(bottom = 32.dp) // Bottom padding for navigation bar
        ) {

                // Header Actions
                Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.End
                ) {
                        IconButton(onClick = { onEdit(habit.id) }) {
                                Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = TextSecondary
                                )
                        }
                        IconButton(onClick = { /* TODO: Confirm Delete */}) {
                                Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = TextSecondary
                                )
                        }
                        IconButton(onClick = onClose) {
                                Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = TextSecondary
                                )
                        }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Main Icon & Title
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Animated Outer Ring
                        Box(
                                contentAlignment = Alignment.Center,
                                modifier =
                                        Modifier.size(100.dp)
                                                .border(4.dp, OceanGradient, CircleShape)
                                                .padding(8.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryColor.copy(alpha = 0.1f))
                        ) {
                                Text(
                                        text = habit.emoji,
                                        style = MaterialTheme.typography.displayMedium
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = habit.title,
                                style =
                                        MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                        )
                        )

                        Text(
                                text = "${habit.timeOfDay.name.lowercase().capitalize()} Routine",
                                style =
                                        MaterialTheme.typography.bodyLarge.copy(
                                                color = TextSecondary,
                                                fontWeight = FontWeight.Medium
                                        )
                        )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Statistics Row
                Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                        StatItem(
                                icon = Icons.Default.LocalFireDepartment,
                                value = "${habit.streak}",
                                label = "Streak",
                                color = Color(0xFFF97316) // Orange
                        )
                        StatItem(
                                icon = Icons.Default.Star,
                                value = "${habit.successRate}%",
                                label = "Success",
                                color = Color(0xFFEAB308) // Yellow
                        )
                        StatItem(
                                icon = Icons.Default.History,
                                value = "${habit.totalCompletions}",
                                label = "Total",
                                color = PrimaryColor
                        )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress & Action
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(BackgroundColor)
                                        .padding(24.dp)
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = "Today's Progress",
                                                style =
                                                        MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextPrimary
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text =
                                                        "${habit.current} / ${habit.target} ${habit.unitLabel}",
                                                style =
                                                        MaterialTheme.typography.displaySmall.copy(
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = PrimaryColor
                                                        )
                                        )
                                }

                                // Actions: Minus and Plus
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Minus Button
                                        Box(
                                                modifier =
                                                        Modifier.size(48.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                        Color.LightGray.copy(
                                                                                alpha = 0.3f
                                                                        )
                                                                )
                                                                .clickable {
                                                                        onDecrement(habit.id)
                                                                },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = "-",
                                                        color = TextSecondary,
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Plus/Check Button
                                        Box(
                                                modifier =
                                                        Modifier.size(64.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                        if (habit.isDoneToday)
                                                                                OceanGradient
                                                                        else
                                                                                Brush.linearGradient(
                                                                                        listOf(
                                                                                                PrimaryColor,
                                                                                                SecondaryColor
                                                                                        )
                                                                                )
                                                                )
                                                                .clickable {
                                                                        onToggleCompletion(habit.id)
                                                                },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                // Simple Checkmark or Plus
                                                Text(
                                                        text = if (habit.isDoneToday) "✓" else "+",
                                                        color = Color.White,
                                                        fontSize = 32.sp,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }
                        }
                }

                // Mini Calendar Mockup
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                        text = "Recent Activity (Last 7 Days)",
                        style =
                                MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                ),
                        modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        // Generate last 7 days names
                        val today = java.time.LocalDate.now()
                        // 0..6 reversed implies: offset 0 is -6 days ago, offset 6 is today
                        // But recentHistory list is ordered from -6 to 0 (oldest to newest)

                        val history = habit.recentHistory

                        (0..6).forEach { index ->
                                // Calculate day letter
                                val date = today.minusDays((6 - index).toLong())
                                val dayLetter = date.dayOfWeek.name.first().toString()

                                val isCompleted = history.getOrElse(index) { false }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                                text = dayLetter,
                                                style =
                                                        MaterialTheme.typography.labelSmall.copy(
                                                                color = TextSecondary
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                                modifier =
                                                        Modifier.size(12.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                        if (isCompleted)
                                                                                PrimaryColor
                                                                        else
                                                                                Color.LightGray
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.3f
                                                                                        )
                                                                )
                                        )
                                }
                        }
                }
        }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, color: Color) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .clip(CircleShape)
                                        .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(24.dp)
                        )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = value,
                        style =
                                MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                )
                )
                Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )
        }
}

// Helper extension
fun String.capitalize(): String {
        return this.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
        }
}

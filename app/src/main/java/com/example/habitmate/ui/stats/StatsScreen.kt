package com.example.habitmate.ui.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.habitmate.ui.home.HabitUi
import java.time.LocalDate

// Colors
private val BackgroundColor = Color(0xFFF8FAFC)
private val SurfaceColor = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryBlue = Color(0xFF2563EB)
private val AccentCyan = Color(0xFF06B6D4)
private val SuccessGreen = Color(0xFF10B981)
private val FireOrange = Color(0xFFF97316)

@Composable
fun StatsScreen(habits: List<HabitUi>, modifier: Modifier = Modifier) {
        // 1. Overall Completion Rate (Average of all habits' success rates)
        val completionRate =
                remember(habits) {
                        if (habits.isEmpty()) 0 else habits.map { it.successRate }.average().toInt()
                }

        // 2. Best Streak (Max streak among all habits)
        val bestStreak = remember(habits) { habits.maxOfOrNull { it.streak } ?: 0 }

        // 3. Consistency Data (Aggregated daily completions for the last 7 days)
        val consistencyData =
                remember(habits) {
                        val daysCount = IntArray(7) { 0 }
                        habits.forEach { habit ->
                                habit.recentHistory.forEachIndexed { index, isDone ->
                                        if (isDone) daysCount[index]++
                                }
                        }
                        daysCount.toList()
                }

        // 4. Top Performers
        val topHabits = remember(habits) { habits.sortedByDescending { it.successRate }.take(5) }

        LazyColumn(
                modifier = modifier.fillMaxSize().background(BackgroundColor),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
                // 1. Overview Cards Row
                item {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                StatCard(
                                        title = "Avg. Completion",
                                        value = "$completionRate%",
                                        icon = Icons.Default.TrendingUp,
                                        color = SuccessGreen,
                                        modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                        title = "Best Active Streak",
                                        value = "$bestStreak days",
                                        icon = Icons.Default.LocalFireDepartment,
                                        color = FireOrange,
                                        modifier = Modifier.weight(1f)
                                )
                        }
                }

                // 2. Weekly Consistency Chart
                item {
                        ConsistencyChartCard(
                                dailyCounts = consistencyData,
                                maxPossible = habits.size
                        )
                }

                // 3. Habit Breakdown (Top Performers)
                item {
                        Text(
                                text = "Top Performers",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                        )
                }

                if (topHabits.isEmpty()) {
                        item {
                                Text(
                                        text = "No habits yet. Start tracking!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                )
                        }
                } else {
                        items(topHabits) { habit -> HabitStatRow(habit) }
                }

                item {
                        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding
                }
        }
}

@Composable
fun StatCard(
        title: String,
        value: String,
        icon: ImageVector,
        color: Color,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier,
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation =
                        CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                        ), // Flat style with border
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        Box(
                                modifier =
                                        Modifier.size(40.dp)
                                                .clip(CircleShape)
                                                .background(color.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(20.dp)
                                )
                        }

                        Column {
                                Text(
                                        text = value,
                                        style = MaterialTheme.typography.headlineSmall, // Big Bold
                                        fontWeight = FontWeight.Black,
                                        color = TextDark
                                )
                                Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                )
                        }
                }
        }
}

@Composable
fun ConsistencyChartCard(dailyCounts: List<Int>, maxPossible: Int) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
                Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = "Consistency",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                )
                                Text(
                                        text = "Last 7 Days",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Custom Chart
                        Box(
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                contentAlignment = Alignment.Center
                        ) { WeeklyBarChart(dailyCounts, maxPossible) }
                }
        }
}

@Composable
fun WeeklyBarChart(dailyCounts: List<Int>, maxPossible: Int) {
        // Generate dynamic day labels (e.g., Mon, Tue)
        val weekDays = remember {
                val today = LocalDate.now()
                (0..6).map { i ->
                        val date = today.minusDays((6 - i).toLong())
                        date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                }
        }

        // Normalize data: count / maxPossible (avoid div by 0)
        val safeMax = if (maxPossible == 0) 1 else maxPossible
        val normalizedData = dailyCounts.map { it.toFloat() / safeMax }

        // Animate bars
        val animatedValues =
                normalizedData.map { target ->
                        val anim = remember(target) { Animatable(0f) }
                        LaunchedEffect(target) {
                                anim.animateTo(
                                        targetValue =
                                                target.coerceAtLeast(
                                                        0.05f
                                                ), // Min height for visibility
                                        animationSpec = tween(1000, delayMillis = 200)
                                )
                        }
                        anim.value
                }

        Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
        ) {
                weekDays.forEachIndexed { index, day ->
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                        ) {
                                // Bar
                                Box(
                                        modifier =
                                                Modifier.width(24.dp)
                                                        .weight(
                                                                1f,
                                                                fill = false
                                                        ) // Allow it to flex up
                                                        .fillMaxHeight(animatedValues[index])
                                                        .clip(
                                                                RoundedCornerShape(
                                                                        topStart = 8.dp,
                                                                        topEnd = 8.dp
                                                                )
                                                        )
                                                        .background(
                                                                Brush.verticalGradient(
                                                                        colors =
                                                                                if (dailyCounts[
                                                                                                index] >
                                                                                                0
                                                                                )
                                                                                        listOf(
                                                                                                AccentCyan,
                                                                                                PrimaryBlue
                                                                                        )
                                                                                else
                                                                                        listOf(
                                                                                                Color.LightGray,
                                                                                                Color.Gray
                                                                                        )
                                                                )
                                                        )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Label
                                Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                )
                        }
                }
        }
}

@Composable
fun HabitStatRow(habit: HabitUi) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Icon
                Box(
                        modifier =
                                Modifier.size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                ) { Text(text = habit.emoji, fontSize = 20.sp) }

                // Info
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = habit.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                        )
                        // Show Success Rate
                        Text(
                                text = "Success Rate: ${habit.successRate}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (habit.successRate >= 80) SuccessGreen else TextSecondary
                        )
                }

                // Mini Streak Badge
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFF1F5F9)) {
                        Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = FireOrange
                                )
                                Text(
                                        text = "${habit.streak}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                )
                        }
                }
        }
}

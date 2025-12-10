package com.example.habitmate.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reusing colors/theme from HomeScreen (ideally should be constantly shared)
private val PrimaryColor = Color(0xFF2563EB)
private val SecondaryColor = Color(0xFF06B6D4)
private val CardSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val OceanGradient = Brush.linearGradient(colors = listOf(PrimaryColor, SecondaryColor))

data class HabitTemplate(val title: String, val emoji: String, val color: Color)

@Composable
fun HabitTemplateSheet(
        onClose: () -> Unit,
        onCreateCustom: () -> Unit,
        onTemplateSelect: (HabitTemplate) -> Unit
) {
    val templates =
            listOf(
                    HabitTemplate("Drink Water", "💧", Color(0xFFE0F2FE)), // Light Sky
                    HabitTemplate("Morning Jog", "🏃", Color(0xFFFFF7ED)), // Light Orange
                    HabitTemplate("Reading", "📚", Color(0xFFF0FDF4)), // Light Green
                    HabitTemplate("Meditation", "🧘", Color(0xFFFAF5FF)), // Light Purple
                    HabitTemplate("Sleep Early", "😴", Color(0xFFEEF2FF)), // Light Indigo
                    HabitTemplate("Journaling", "✍️", Color(0xFFFEF2F2)) // Light Red
            )

    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(CardSurface)
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 32.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "New Habit",
                    style =
                            MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                            )
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Create Custom Button
        Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onCreateCustom() },
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
                border =
                        androidx.compose.foundation.BorderStroke(
                                1.dp,
                                PrimaryColor.copy(alpha = 0.3f)
                        )
        ) {
            Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
            ) {
                Box(
                        modifier =
                                Modifier.size(24.dp)
                                        .background(PrimaryColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                ) {
                    Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = PrimaryColor,
                            modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                        text = "Create Custom Habit",
                        style =
                                MaterialTheme.typography.titleMedium.copy(
                                        color = PrimaryColor,
                                        fontWeight = FontWeight.Bold
                                )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Templates Header
        Text(
                text = "Or choose a template",
                style =
                        MaterialTheme.typography.labelLarge.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                        ),
                modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid
        LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp) // Fixed height for internal scrolling if needed
        ) {
            items(templates) { template ->
                TemplateCard(template, onClick = { onTemplateSelect(template) })
            }
        }
    }
}

@Composable
fun TemplateCard(template: HabitTemplate, onClick: () -> Unit) {
    Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = template.color),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth().height(110.dp).clickable { onClick() }
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
        ) {
            Box(
                    modifier = Modifier.size(36.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
            ) { Text(text = template.emoji, fontSize = 20.sp) }
            Text(
                    text = template.title,
                    style =
                            MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary.copy(alpha = 0.8f)
                            )
            )
        }
    }
}

package com.example.habitmate.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reusing colors from HomeScreen
private val PrimaryColor = Color(0xFF2563EB)
private val SecondaryColor = Color(0xFF06B6D4)
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)
private val OceanGradient = Brush.linearGradient(colors = listOf(PrimaryColor, SecondaryColor))

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CreateHabitScreen(
        onBack: () -> Unit,
        onSave: (HabitUi) -> Unit,
        initialTitle: String = "",
        initialEmoji: String = "💧"
) {
        var title by remember { mutableStateOf(initialTitle) }
        var selectedEmoji by remember { mutableStateOf(initialEmoji) }
        var selectedTime by remember { mutableStateOf(HabitTimeOfDay.ANYTIME) }
        var targetValue by remember { mutableStateOf("1") }
        var unitValue by remember { mutableStateOf("times") }
        var frequency by remember { mutableStateOf("Daily") } // Daily vs Weekly
        // Frequency Logic
        val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
        val selectedDays = remember { mutableStateListOf(true, true, true, true, true, true, true) }
        var daysPerWeek by remember { mutableStateOf(3) }

        val emojis = listOf("💧", "🏃", "📚", "🧘", "😴", "💻", "🍎", "💊", "🎸", "🎨")

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Create Habit", fontWeight = FontWeight.Bold) },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.Default.ArrowBack,
                                                        contentDescription = "Back"
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = BackgroundColor
                                        )
                        )
                },
                containerColor = BackgroundColor
        ) { padding ->
                Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)) {

                        // 1. Name & Icon Input
                        Text(
                                "Habit Name",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                // Icon Selector
                                Box(
                                        modifier =
                                                Modifier.size(56.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(PrimaryColor.copy(alpha = 0.1f))
                                                        .clickable { /* TODO: Open Emoji Picker */},
                                        contentAlignment = Alignment.Center
                                ) { Text(selectedEmoji, fontSize = 28.sp) }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Name Input
                                Box(
                                        modifier =
                                                Modifier.weight(1f)
                                                        .height(56.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(CardSurface)
                                                        .border(
                                                                1.dp,
                                                                Color.Black.copy(alpha = 0.05f),
                                                                RoundedCornerShape(16.dp)
                                                        )
                                                        .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                ) {
                                        BasicTextField(
                                                value = title,
                                                onValueChange = { title = it },
                                                textStyle =
                                                        TextStyle(
                                                                color = TextPrimary,
                                                                fontSize = 18.sp,
                                                                fontWeight = FontWeight.Medium
                                                        ),
                                                decorationBox = { innerTextField ->
                                                        if (title.isEmpty())
                                                                Text(
                                                                        "e.g., Drink Water",
                                                                        color =
                                                                                TextSecondary.copy(
                                                                                        alpha = 0.5f
                                                                                )
                                                                )
                                                        innerTextField()
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Emoji Selection Row
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(emojis) { emoji ->
                                        Box(
                                                modifier =
                                                        Modifier.size(44.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                        if (selectedEmoji == emoji)
                                                                                PrimaryColor
                                                                        else CardSurface
                                                                )
                                                                .border(
                                                                        1.dp,
                                                                        if (selectedEmoji == emoji)
                                                                                Color.Transparent
                                                                        else
                                                                                Color.Black.copy(
                                                                                        alpha =
                                                                                                0.05f
                                                                                ),
                                                                        CircleShape
                                                                )
                                                                .clickable {
                                                                        selectedEmoji = emoji
                                                                },
                                                contentAlignment = Alignment.Center
                                        ) { Text(emoji, fontSize = 20.sp) }
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 2. Frequency
                        Text(
                                "Frequency",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BoxWithConstraints(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(56.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(Color.White)
                                                .border(
                                                        1.dp,
                                                        Color.Black.copy(alpha = 0.05f),
                                                        RoundedCornerShape(50)
                                                )
                        ) {
                                val width = maxWidth
                                val tabWidth = width / 2

                                val indicatorOffset by
                                        androidx.compose.animation.core.animateDpAsState(
                                                targetValue =
                                                        if (frequency == "Daily") 0.dp
                                                        else tabWidth,
                                                animationSpec =
                                                        androidx.compose.animation.core.spring(
                                                                stiffness =
                                                                        androidx.compose.animation
                                                                                .core.Spring
                                                                                .StiffnessMediumLow
                                                        ),
                                                label = "indicator"
                                        )

                                // Animated Sliding Pill
                                Box(
                                        modifier =
                                                Modifier.padding(4.dp)
                                                        .width(tabWidth - 8.dp)
                                                        .fillMaxHeight()
                                                        .offset(x = indicatorOffset)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(PrimaryColor)
                                )

                                // Text Items
                                Row(modifier = Modifier.fillMaxSize()) {
                                        listOf("Daily", "Weekly").forEach { item ->
                                                val isSelected = frequency == item
                                                val textColor by
                                                        androidx.compose.animation
                                                                .animateColorAsState(
                                                                        targetValue =
                                                                                if (isSelected)
                                                                                        Color.White
                                                                                else TextSecondary,
                                                                        animationSpec =
                                                                                androidx.compose
                                                                                        .animation
                                                                                        .core.tween(
                                                                                        durationMillis =
                                                                                                300
                                                                                ),
                                                                        label = "textColor"
                                                                )
                                                val fontWeight =
                                                        if (isSelected) FontWeight.Bold
                                                        else FontWeight.Medium

                                                Box(
                                                        modifier =
                                                                Modifier.weight(1f)
                                                                        .fillMaxHeight()
                                                                        .clickable(
                                                                                interactionSource =
                                                                                        remember {
                                                                                                androidx.compose
                                                                                                        .foundation
                                                                                                        .interaction
                                                                                                        .MutableInteractionSource()
                                                                                        },
                                                                                indication =
                                                                                        null // Disable ripple for cleaner custom animation
                                                                        ) { frequency = item },
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                text = item,
                                                                color = textColor,
                                                                fontWeight = fontWeight,
                                                                fontSize = 16.sp,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (frequency == "Daily") {
                                // Pick Days Row
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        weekDays.forEachIndexed { index, day ->
                                                val isSelected = selectedDays[index]
                                                val color =
                                                        if (isSelected) SecondaryColor
                                                        else Color.Transparent
                                                val textColor =
                                                        if (isSelected) Color.White
                                                        else TextSecondary
                                                val border =
                                                        if (isSelected) null
                                                        else
                                                                BorderStroke(
                                                                        1.dp,
                                                                        Color.Black.copy(
                                                                                alpha = 0.1f
                                                                        )
                                                                )

                                                Box(
                                                        modifier =
                                                                Modifier.size(40.dp)
                                                                        .clip(CircleShape)
                                                                        .background(color)
                                                                        .border(
                                                                                border
                                                                                        ?: BorderStroke(
                                                                                                0.dp,
                                                                                                Color.Transparent
                                                                                        ),
                                                                                CircleShape
                                                                        )
                                                                        .clickable {
                                                                                selectedDays[
                                                                                        index] =
                                                                                        !selectedDays[
                                                                                                index]
                                                                        },
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                text = day,
                                                                color = textColor,
                                                                fontWeight = FontWeight.Bold,
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge
                                                        )
                                                }
                                        }
                                }
                        } else {
                                // Weekly Count
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(56.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(CardSurface)
                                                        .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                "Days per week",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = TextPrimary
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Minus Button
                                                Box(
                                                        modifier =
                                                                Modifier.size(32.dp)
                                                                        .clip(CircleShape)
                                                                        .background(BackgroundColor)
                                                                        .clickable {
                                                                                if (daysPerWeek > 1)
                                                                                        daysPerWeek--
                                                                        },
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                "-",
                                                                fontSize = 20.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextSecondary
                                                        )
                                                }

                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                        "$daysPerWeek",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = PrimaryColor
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))

                                                // Plus Button
                                                Box(
                                                        modifier =
                                                                Modifier.size(32.dp)
                                                                        .clip(CircleShape)
                                                                        .background(BackgroundColor)
                                                                        .clickable {
                                                                                if (daysPerWeek < 7)
                                                                                        daysPerWeek++
                                                                        },
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Text(
                                                                "+",
                                                                fontSize = 20.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextSecondary
                                                        )
                                                }
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Goal
                        Text(
                                "Daily Goal",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                // Target Number
                                Box(
                                        modifier =
                                                Modifier.width(80.dp)
                                                        .height(56.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(CardSurface)
                                                        .border(
                                                                1.dp,
                                                                Color.Black.copy(alpha = 0.05f),
                                                                RoundedCornerShape(16.dp)
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        BasicTextField(
                                                value = targetValue,
                                                onValueChange = {
                                                        if (it.all { char -> char.isDigit() })
                                                                targetValue = it
                                                },
                                                textStyle =
                                                        TextStyle(
                                                                color = TextPrimary,
                                                                fontSize = 20.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                textAlign =
                                                                        androidx.compose.ui.text
                                                                                .style.TextAlign
                                                                                .Center
                                                        )
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Unit
                                Box(
                                        modifier =
                                                Modifier.weight(1f)
                                                        .height(56.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(CardSurface)
                                                        .border(
                                                                1.dp,
                                                                Color.Black.copy(alpha = 0.05f),
                                                                RoundedCornerShape(16.dp)
                                                        )
                                                        .padding(horizontal = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                ) {
                                        BasicTextField(
                                                value = unitValue,
                                                onValueChange = { unitValue = it },
                                                textStyle =
                                                        TextStyle(
                                                                color = TextPrimary,
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Medium
                                                        ),
                                                decorationBox = { innerTextField ->
                                                        if (unitValue.isEmpty())
                                                                Text(
                                                                        "Unit (e.g., cups)",
                                                                        color =
                                                                                TextSecondary.copy(
                                                                                        alpha = 0.5f
                                                                                )
                                                                )
                                                        innerTextField()
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. Time of Day
                        Text(
                                "Time of Day",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Simple Chip Grid
                        Column {
                                val times = HabitTimeOfDay.values()
                                val rows = times.toList().chunked(2)
                                rows.forEach { rowItems ->
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                rowItems.forEach { time ->
                                                        val isSelected = selectedTime == time
                                                        Box(
                                                                modifier =
                                                                        Modifier.weight(1f)
                                                                                .height(44.dp)
                                                                                .clip(
                                                                                        RoundedCornerShape(
                                                                                                12.dp
                                                                                        )
                                                                                )
                                                                                .background(
                                                                                        if (isSelected
                                                                                        )
                                                                                                PrimaryColor
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.1f
                                                                                                        )
                                                                                        else
                                                                                                CardSurface
                                                                                )
                                                                                .border(
                                                                                        1.dp,
                                                                                        if (isSelected
                                                                                        )
                                                                                                PrimaryColor
                                                                                        else
                                                                                                Color.Black
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.05f
                                                                                                        ),
                                                                                        RoundedCornerShape(
                                                                                                12.dp
                                                                                        )
                                                                                )
                                                                                .clickable {
                                                                                        selectedTime =
                                                                                                time
                                                                                },
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                time.name
                                                                                        .lowercase()
                                                                                        .replaceFirstChar {
                                                                                                it.uppercase()
                                                                                        },
                                                                        color =
                                                                                if (isSelected)
                                                                                        PrimaryColor
                                                                                else TextSecondary,
                                                                        fontWeight =
                                                                                FontWeight.Medium
                                                                )
                                                        }
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Create Button
                        Button(
                                onClick = {
                                        val newHabit =
                                                HabitUi(
                                                        id = (10..1000).random(), // Mock ID
                                                        title = title.ifEmpty { "New Habit" },
                                                        emoji = selectedEmoji,
                                                        timeOfDay = selectedTime,
                                                        unitLabel = unitValue,
                                                        current = 0,
                                                        target = targetValue.toIntOrNull() ?: 1,
                                                        isDoneToday = false
                                                )
                                        onSave(newHabit)
                                },
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(56.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) { Text("Create Habit", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
        }
}

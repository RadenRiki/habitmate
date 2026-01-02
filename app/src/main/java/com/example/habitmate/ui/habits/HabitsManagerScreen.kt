package com.example.habitmate.ui.habits

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmate.ui.home.HabitUi
import com.example.habitmate.ui.home.HomeViewModel

// Consistent Colors
private val BackgroundColor = Color(0xFFF8FAFC) // Slate-50
private val SurfaceColor = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryBlue = Color(0xFF2563EB)
private val DangerRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsManagerScreen(
        viewModel: HomeViewModel,
        modifier: Modifier = Modifier,
        onNavigateToEdit: (String) -> Unit
) {
    val allHabits by viewModel.allHabits.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Delete Confirmation State
    var showDeleteDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<HabitUi?>(null) }

    val filteredHabits =
            remember(allHabits, searchQuery) {
                if (searchQuery.isBlank()) allHabits
                else allHabits.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }

    if (showDeleteDialog && habitToDelete != null) {
        AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Habit?") },
                text = {
                    Text(
                            "Are you sure you want to delete '${habitToDelete?.title}'? This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                habitToDelete?.let { viewModel.deleteHabit(it) }
                                showDeleteDialog = false
                                Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                },
                containerColor = SurfaceColor,
                titleContentColor = TextDark,
                textContentColor = TextSecondary
        )
    }

    Column(
            modifier =
                    modifier.fillMaxSize()
                            .background(BackgroundColor)
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp)
    ) {
        // Search Bar
        OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...", color = TextSecondary.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue)
                },
                shape = RoundedCornerShape(16.dp),
                colors =
                        OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceColor,
                                unfocusedContainerColor = SurfaceColor,
                                disabledContainerColor = SurfaceColor,
                                focusedBorderColor = PrimaryBlue.copy(alpha = 0.3f),
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = PrimaryBlue,
                        ),
                singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // List Content
        if (filteredHabits.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text =
                                    if (searchQuery.isNotEmpty()) "No match found"
                                    else "No habits yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredHabits) { habit ->
                    val index = filteredHabits.indexOf(habit)
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(index * 50L) // Staggered entry
                        isVisible = true
                    }

                    AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
                    ) {
                        HabitListItem(
                                habit = habit,
                                onDeleteClick = {
                                    habitToDelete = habit
                                    showDeleteDialog = true
                                },
                                onEditClick = { onNavigateToEdit(habit.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitListItem(habit: HabitUi, onDeleteClick: () -> Unit, onEditClick: () -> Unit) {
    Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceColor,
            shadowElevation = 1.dp, // Subtle shadow for depth
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // Very light gray border
            modifier = Modifier.fillMaxWidth()
    ) {
        Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Bubble
            Box(
                    modifier =
                            Modifier.size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)), // Light blue bg
                    contentAlignment = Alignment.Center
            ) { Text(habit.emoji, fontSize = 24.sp) }

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1
                )

                // Frequency + Streak
                val frequencyText = if (habit.selectedDays.all { it }) "Everyday" else "Custom"
                Text(
                        text = "$frequencyText • ${habit.streak} day streak",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                )
            }

            // Actions Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Edit Button
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                    )
                }

                // Delete Button
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = DangerRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

package com.example.habitmate.ui.habits

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsManagerScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
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
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
        )
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search habits...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                shape = RoundedCornerShape(16.dp),
                colors =
                        OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color.Transparent
                        )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (filteredHabits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            "No habits found",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            "Create a new one from the Home screen!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                    )
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
            ) {
                items(filteredHabits) { habit ->
                    HabitManagerCard(
                            habit = habit,
                            onDeleteClick = {
                                habitToDelete = habit
                                showDeleteDialog = true
                            },
                            onCardClick = {
                                Toast.makeText(
                                                context,
                                                "Edit feature coming soon! 🚧",
                                                Toast.LENGTH_SHORT
                                        )
                                        .show()
                            }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
                }
            }
        }
    }
}

@Composable
fun HabitManagerCard(habit: HabitUi, onDeleteClick: () -> Unit, onCardClick: () -> Unit) {
    Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().clickable { onCardClick() }
    ) {
        Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Big Emoji with background
            Box(
                    modifier =
                            Modifier.size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)), // Light Blue bg
                    contentAlignment = Alignment.Center
            ) { Text(habit.emoji, fontSize = 32.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                    habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Frequency Pill
            val frequencyText = if (habit.selectedDays.all { it }) "Everyday" else "Custom Days"
            Surface(
                    color =
                            if (habit.selectedDays.all { it }) Color(0xFFDCFCE7)
                            else Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(24.dp)
            ) {
                Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Text(
                            frequencyText,
                            style = MaterialTheme.typography.labelSmall,
                            color =
                                    if (habit.selectedDays.all { it }) Color(0xFF166534)
                                    else Color(0xFF6B21A8),
                            fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Row (Mockup for visual richness)
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Streak", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                            "${habit.streak} 🔥",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                    )
                }

                // Delete Button
                IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp).background(Color(0xFFFEE2E2), CircleShape)
                ) {
                    Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

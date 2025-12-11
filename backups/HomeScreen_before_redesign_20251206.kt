package com.example.habitmate.ui.home.backup

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------- COLORS & THEME ----------

private val PrimaryColor = Color(0xFF2563EB) // Blue-600 (Vibrant Blue)
private val SecondaryColor = Color(0xFF06B6D4) // Cyan-500
private val BackgroundColor = Color(0xFFF8FAFC) // Slate-50 (softer)
private val CardSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF111827) // Gray 900
private val TextSecondary = Color(0xFF6B7280) // Gray 500
private val SuccessColor = Color(0xFF10B981) // Emerald 500

// Navbar specific colors
private val NavSurface = Color(0xFFFFFFFF) // White background
private val NavSlate = Color(0xFF1E293B) // Slate-800 for selected pill
private val NavIconInactive = Color(0xFF64748B) // Slate-500 for unselected icons

// Gradient untuk kartu progress - Ocean Blue Theme
private val PrimaryGradient =
        Brush.linearGradient(colors = listOf(Color(0xFF2563EB), Color(0xFF06B6D4)))

// ---------- MODELS ----------

enum class HabitTimeOfDay {
        ANYTIME,
        MORNING,
        AFTERNOON,
        EVENING
}

enum class HabitTimeFilter {
        ALL_DAY,
        MORNING,
        AFTERNOON,
        EVENING
}

data class HabitUi(
        val id: Int,
        val title: String,
        val emoji: String, // Menambahkan emoji/icon
        val timeOfDay: HabitTimeOfDay,
        val unitLabel: String,
        val current: Int,
        val target: Int,
        val isDoneToday: Boolean,
        val streak: Int = 0 // Menambahkan streak dummy
)

enum class HabitMateDestination {
        HOME,
        STATS,
        HABITS,
        PROFILE
}

// ---------- ROOT SCREEN ----------

@Composable
fun HabitMateHomeScreen() {
        val today = remember { LocalDate.now() }
        var selectedDate by remember { mutableStateOf(today) }

        // Logic tanggal tetap sama
        val dateItems = remember {
                val start = today.minusDays(7)
                val end = today.plusDays(14) // Sedikit dikurangi agar tidak terlalu panjang loadnya
                generateSequence(start) { it.plusDays(1) }.takeWhile { !it.isAfter(end) }.toList()
        }

        var currentDestination by remember { mutableStateOf(HabitMateDestination.HOME) }
        var selectedFilter by remember { mutableStateOf(HabitTimeFilter.ALL_DAY) }

        // Dummy habits dengan Emoji
        val habits = remember {
                mutableStateListOf(
                        HabitUi(
                                1,
                                "Drink Water",
                                "💧",
                                HabitTimeOfDay.ANYTIME,
                                "cups",
                                3,
                                8,
                                false,
                                5
                        ),
                        HabitUi(
                                2,
                                "Code Project",
                                "💻",
                                HabitTimeOfDay.MORNING,
                                "modules",
                                0,
                                1,
                                false,
                                12
                        ),
                        HabitUi(
                                3,
                                "Evening Run",
                                "🏃",
                                HabitTimeOfDay.EVENING,
                                "mins",
                                20,
                                30,
                                false,
                                3
                        ),
                        HabitUi(
                                4,
                                "Meditation",
                                "🧘",
                                HabitTimeOfDay.MORNING,
                                "mins",
                                15,
                                15,
                                true,
                                21
                        ),
                        HabitUi(
                                5,
                                "Read Book",
                                "📚",
                                HabitTimeOfDay.EVENING,
                                "pages",
                                0,
                                20,
                                false,
                                0
                        )
                )
        }

        val totalHabitsToday = habits.size
        val doneToday = habits.count { it.isDoneToday }
        val progressToday =
                if (totalHabitsToday == 0) 0f else doneToday.toFloat() / totalHabitsToday

        Scaffold(
                topBar = {
                        ModernTopBar(
                                title = "Hello, Mate! 👋", // Sapaan personal
                                subtitle = dateLabelFor(selectedDate, today),
                                onSettingsClick = { /* TODO */}
                        )
                },
                floatingActionButton = {
                        if (currentDestination == HabitMateDestination.HOME) {
                                FloatingActionButton(
                                        onClick = { /* TODO */},
                                        containerColor = PrimaryColor,
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                                ) {
                                        Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Add",
                                                tint = Color.White
                                        )
                                }
                        }
                },
                bottomBar = {
                        BottomNavBar(
                                selected = currentDestination,
                                onDestinationSelected = { currentDestination = it }
                        )
                },
                containerColor = BackgroundColor
        ) { innerPadding ->
                when (currentDestination) {
                        HabitMateDestination.HOME -> {
                                TodayContent(
                                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                                        today = today,
                                        selectedDate = selectedDate,
                                        onDateSelected = { newDate ->
                                                if (!newDate.isBefore(today.minusDays(7)))
                                                        selectedDate = newDate
                                        },
                                        dateItems = dateItems,
                                        habits = habits,
                                        selectedFilter = selectedFilter,
                                        onFilterSelected = { selectedFilter = it },
                                        progressToday = progressToday,
                                        doneToday = doneToday,
                                        totalHabitsToday = totalHabitsToday,
                                        onToggleHabit = { habitId ->
                                                // Simple toggle logic for UI demo
                                                val index = habits.indexOfFirst { it.id == habitId }
                                                if (index != -1) {
                                                        val h = habits[index]
                                                        habits[index] =
                                                                h.copy(isDoneToday = !h.isDoneToday)
                                                }
                                        }
                                )
                        }
                        else -> PlaceholderScreen(modifier = Modifier.padding(innerPadding))
                }
        }
}

// ---------- TOP BAR & BOTTOM NAV ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(title: String, subtitle: String, onSettingsClick: () -> Unit) {
        TopAppBar(
                title = {
                        Column {
                                Text(
                                        text = title,
                                        style =
                                                MaterialTheme.typography.headlineSmall
                                                        .copy( // Bigger title
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = TextPrimary
                                                        )
                                )
                                Text(
                                        text = subtitle,
                                        style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                        color = TextSecondary,
                                                        fontWeight = FontWeight.Medium
                                                )
                                )
                        }
                },
                actions = {
                        IconButton(
                                onClick = onSettingsClick,
                                modifier =
                                        Modifier.padding(end = 8.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .border(
                                                        1.dp,
                                                        Color.Black.copy(alpha = 0.05f),
                                                        CircleShape
                                                )
                                                .size(44.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(22.dp)
                                )
                        }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
        )
}

// ---------- BOTTOM NAVIGATION (SMOOTH BOTTOM BAR) ----------

data class NavItem(
        val route: HabitMateDestination,
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
)

@Composable
fun BottomNavBar(
        selected: HabitMateDestination,
        onDestinationSelected: (HabitMateDestination) -> Unit
) {
        val items =
                listOf(
                        NavItem(
                                HabitMateDestination.HOME,
                                "Home",
                                Icons.Outlined.Home,
                                Icons.Default.Home
                        ),
                        NavItem(
                                HabitMateDestination.STATS,
                                "Stats",
                                Icons.Outlined.BarChart,
                                Icons.Default.BarChart
                        ),
                        NavItem(
                                HabitMateDestination.HABITS,
                                "Habits",
                                Icons.Outlined.Checklist,
                                Icons.Default.Checklist
                        ),
                        NavItem(
                                HabitMateDestination.PROFILE,
                                "Profile",
                                Icons.Outlined.Person,
                                Icons.Default.Person
                        )
                )

        Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavSurface,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 20.dp,
                tonalElevation = 4.dp
        ) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                        .padding(bottom = 12.dp), // Extra padding for gesture area
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        items.forEach { item ->
                                val isSelected = item.route == selected

                                SmoothNavItem(
                                        item = item,
                                        isSelected = isSelected,
                                        onClick = { onDestinationSelected(item.route) }
                                )
                        }
                }
        }
}

@Composable
fun SmoothNavItem(item: NavItem, isSelected: Boolean, onClick: () -> Unit) {
        // Animate the width of the pill
        val animatedWidth by
                animateDpAsState(
                        targetValue = if (isSelected) 110.dp else 48.dp,
                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
                        label = "pillWidth"
                )

        // Animate background alpha for pill
        val backgroundAlpha by
                animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = tween(durationMillis = 300),
                        label = "bgAlpha"
                )

        // Icon and text colors
        val iconColor = if (isSelected) Color.White else NavIconInactive
        val backgroundColor = NavSlate.copy(alpha = backgroundAlpha)

        Box(
                modifier =
                        Modifier.width(animatedWidth)
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(backgroundColor)
                                .clickable(
                                        interactionSource =
                                                remember {
                                                        androidx.compose.foundation.interaction
                                                                .MutableInteractionSource()
                                                },
                                        indication = null
                                ) { onClick() },
                contentAlignment = Alignment.Center
        ) {
                Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                        Icon(
                                imageVector =
                                        if (isSelected) (item.selectedIcon ?: item.icon)
                                        else item.icon,
                                contentDescription = item.label,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                        )

                        // Only show label when selected with animation
                        androidx.compose.animation.AnimatedVisibility(
                                visible = isSelected,
                                enter =
                                        androidx.compose.animation.fadeIn(
                                                animationSpec = tween(200)
                                        ) +
                                                androidx.compose.animation.expandHorizontally(
                                                        animationSpec = tween(300)
                                                ),
                                exit =
                                        androidx.compose.animation.fadeOut(
                                                animationSpec = tween(200)
                                        ) +
                                                androidx.compose.animation.shrinkHorizontally(
                                                        animationSpec = tween(300)
                                                )
                        ) {
                                Row {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                text = item.label,
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                        ),
                                                maxLines = 1
                                        )
                                }
                        }
                }
        }
}

// ---------- CONTENT ----------

@Composable
fun TodayContent(
        modifier: Modifier,
        today: LocalDate,
        selectedDate: LocalDate,
        onDateSelected: (LocalDate) -> Unit,
        dateItems: List<LocalDate>,
        habits: List<HabitUi>,
        selectedFilter: HabitTimeFilter,
        onFilterSelected: (HabitTimeFilter) -> Unit,
        progressToday: Float,
        doneToday: Int,
        totalHabitsToday: Int,
        onToggleHabit: (Int) -> Unit
) {
        LazyColumn(
                modifier = modifier.background(BackgroundColor),
                contentPadding = PaddingValues(bottom = 100.dp) // Space for FAB
        ) {
                item {
                        ModernDateStrip(
                                today = today,
                                selectedDate = selectedDate,
                                dates = dateItems,
                                onDateSelected = onDateSelected
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                                GradientProgressCard(
                                        progress = progressToday,
                                        done = doneToday,
                                        total = totalHabitsToday
                                )
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                        FilterCapsuleRow(selected = selectedFilter, onSelected = onFilterSelected)
                        Spacer(modifier = Modifier.height(20.dp))
                }

                habitListGrouped(habits = habits, filter = selectedFilter, onToggle = onToggleHabit)
        }
}

// ---------- COMPONENT: DATE STRIP (Capsule Style) ----------

@Composable
fun ModernDateStrip(
        today: LocalDate,
        selectedDate: LocalDate,
        dates: List<LocalDate>,
        onDateSelected: (LocalDate) -> Unit
) {
        LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
                items(dates) { date ->
                        val isSelected = date == selectedDate
                        val isToday = date == today

                        val dayName =
                                date.dayOfWeek
                                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                        .uppercase()
                        val dayNum = date.dayOfMonth.toString()

                        val backgroundColor = if (isSelected) PrimaryColor else CardSurface
                        val contentColor = if (isSelected) Color.White else TextSecondary
                        val border =
                                if (isSelected) null
                                else BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
                        val elevation = if (isSelected) 8.dp else 0.dp

                        Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = backgroundColor,
                                border = border,
                                shadowElevation = elevation,
                                modifier = Modifier.width(60.dp).clickable { onDateSelected(date) }
                        ) {
                                Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 14.dp)
                                ) {
                                        Text(
                                                text = dayName,
                                                style =
                                                        MaterialTheme.typography.labelSmall.copy(
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                color =
                                                        contentColor.copy(
                                                                alpha =
                                                                        if (isSelected) 0.8f
                                                                        else 0.6f
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                                text = dayNum,
                                                style =
                                                        MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Black
                                                        ),
                                                color = contentColor
                                        )
                                        if (isToday && !isSelected) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Box(
                                                        modifier =
                                                                Modifier.size(4.dp)
                                                                        .background(
                                                                                PrimaryColor,
                                                                                CircleShape
                                                                        )
                                                )
                                        }
                                }
                        }
                }
        }
}

// ---------- COMPONENT: PROGRESS CARD (Gradient) ----------

@Composable
fun GradientProgressCard(progress: Float, done: Int, total: Int) {
        // Animasi progress bar
        val animatedProgress by
                animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(durationMillis = 1000),
                        label = "progress"
                )

        // Animasi angka persentase
        val animatedPercentage by
                animateIntAsState(
                        targetValue = (progress * 100).toInt(),
                        animationSpec = tween(durationMillis = 1000),
                        label = "percentage"
                )

        Card(
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.fillMaxWidth()
        ) {
                Box(modifier = Modifier.background(PrimaryGradient).padding(24.dp)) {
                        // Decorative Circles
                        Canvas(modifier = Modifier.matchParentSize()) {
                                drawCircle(
                                        color = Color.White.copy(alpha = 0.1f),
                                        radius = 200f,
                                        center = androidx.compose.ui.geometry.Offset(size.width, 0f)
                                )
                                drawCircle(
                                        color = Color.White.copy(alpha = 0.05f),
                                        radius = 150f,
                                        center =
                                                androidx.compose.ui.geometry.Offset(0f, size.height)
                                )
                        }

                        Column {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Column {
                                                Text(
                                                        text = "Daily Goals",
                                                        style =
                                                                MaterialTheme.typography.titleLarge
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Black,
                                                                                color = Color.White
                                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                        text =
                                                                if (progress >= 1f)
                                                                        "All done! You're on fire! 🔥"
                                                                else "Keep pushing! 🚀",
                                                        style =
                                                                MaterialTheme.typography.bodyMedium
                                                                        .copy(
                                                                                color =
                                                                                        Color.White
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.9f
                                                                                                ),
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium
                                                                        )
                                                )
                                        }
                                        // Persentase bulat
                                        Box(
                                                contentAlignment = Alignment.Center,
                                                modifier =
                                                        Modifier.size(56.dp)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.2f
                                                                        ),
                                                                        CircleShape
                                                                )
                                                                .border(
                                                                        2.dp,
                                                                        Color.White.copy(
                                                                                alpha = 0.3f
                                                                        ),
                                                                        CircleShape
                                                                )
                                        ) {
                                                Text(
                                                        text = "$animatedPercentage%",
                                                        style =
                                                                MaterialTheme.typography.titleMedium
                                                                        .copy(
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                color = Color.White
                                                                        )
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Custom Linear Progress
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .height(12.dp)
                                                        .clip(RoundedCornerShape(100.dp))
                                                        .background(Color.Black.copy(alpha = 0.15f))
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxWidth(
                                                                        animatedProgress
                                                                ) // Pakai animasi
                                                                .fillMaxHeight()
                                                                .clip(RoundedCornerShape(100.dp))
                                                                .background(Color.White)
                                        )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                text = "$done / $total habits",
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.8f
                                                                        ),
                                                                fontWeight = FontWeight.Bold
                                                        )
                                        )
                                        Text(
                                                text = "View Analytics >",
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                modifier = Modifier.clickable { /* TODO */}
                                        )
                                }
                        }
                }
        }
}

// ---------- COMPONENT: FILTER ROW ----------

@Composable
fun FilterCapsuleRow(selected: HabitTimeFilter, onSelected: (HabitTimeFilter) -> Unit) {
        val items =
                listOf(
                        HabitTimeFilter.ALL_DAY to "All",
                        HabitTimeFilter.MORNING to "Morning",
                        HabitTimeFilter.AFTERNOON to "Afternoon",
                        HabitTimeFilter.EVENING to "Evening"
                )

        LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
                items(items) { (filter, label) ->
                        val isSelected = filter == selected
                        val bg = if (isSelected) NavSlate else CardSurface
                        val textColor = if (isSelected) Color.White else TextSecondary
                        val border =
                                if (isSelected) null
                                else BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))

                        Surface(
                                shape = RoundedCornerShape(50),
                                color = bg,
                                border = border,
                                shadowElevation = if (isSelected) 4.dp else 0.dp,
                                modifier = Modifier.clickable { onSelected(filter) }
                        ) {
                                Text(
                                        text = label,
                                        style =
                                                MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = textColor,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 24.dp,
                                                        vertical = 10.dp
                                                )
                                )
                        }
                }
        }
}

// ---------- COMPONENT: HABIT LIST ----------

fun LazyListScope.habitListGrouped(
        habits: List<HabitUi>,
        filter: HabitTimeFilter,
        onToggle: (Int) -> Unit
) {
        val filteredHabits =
                if (filter == HabitTimeFilter.ALL_DAY) {
                        habits
                } else {
                        habits.filter {
                                it.timeOfDay.name == filter.name ||
                                        it.timeOfDay == HabitTimeOfDay.ANYTIME
                        }
                }

        if (filteredHabits.isEmpty()) {
                item {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        "No habits found",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                )
                        }
                }
        } else {
                items(filteredHabits) { habit ->
                        HabitItem(habit = habit, onToggle = { onToggle(habit.id) })
                        Spacer(modifier = Modifier.height(12.dp))
                }
        }
}

@Composable
fun HabitItem(habit: HabitUi, onToggle: () -> Unit) {
        Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier =
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { onToggle() }
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.Gray.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = habit.emoji,
                                        style = MaterialTheme.typography.headlineSmall
                                )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        habit.title,
                                        style =
                                                MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        "${habit.current}/${habit.target} ${habit.unitLabel}",
                                        style =
                                                MaterialTheme.typography.bodySmall.copy(
                                                        color = TextSecondary
                                                )
                                )
                        }
                        Checkbox(checked = habit.isDoneToday, onCheckedChange = { onToggle() })
                }
        }
}

fun dateLabelFor(date: LocalDate, today: LocalDate): String {
        return if (date == today) {
                "Today, ${date.format(DateTimeFormatter.ofPattern("MMM dd"))}"
        } else {
                date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd"))
        }
}

@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                        "Coming Soon",
                        style =
                                MaterialTheme.typography.headlineMedium.copy(
                                        color = TextSecondary.copy(alpha = 0.5f)
                                )
                )
        }
}

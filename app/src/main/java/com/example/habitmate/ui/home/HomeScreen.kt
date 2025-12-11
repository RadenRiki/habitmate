package com.example.habitmate.ui.home

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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.launch

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

private val OceanGradient =
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
        val streak: Int = 0, // Menambahkan streak dummy
        val selectedDays: List<Boolean> = List(7) { true },
        val weeklyTarget: Int = 0 // 0 means specific days, >0 means times per week
)

enum class HabitMateDestination {
        HOME,
        STATS,
        HABITS,
        PROFILE
}

// ---------- ROOT SCREEN ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitMateHomeScreen(
        onNavigateToCreateHabit: (String?, String?) -> Unit = { _, _ -> },
        viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
        val today = remember { LocalDate.now() }
        var selectedDate by remember { mutableStateOf(today) }
        var selectedHabit by remember { mutableStateOf<HabitUi?>(null) }
        var showAddSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()
        val habits by viewModel.uiState.collectAsState()

        val hasAnimated by viewModel.hasAnimated.collectAsState()

        // Trigger animation ONCE on first launch
        LaunchedEffect(Unit) {
                if (!hasAnimated) {
                        kotlinx.coroutines.delay(1500) // Wait for data load & anim
                        viewModel.setAnimated()
                }
        }

        // Logic tanggal - 7 hari ke belakang, 90 hari ke depan
        val dateItems = remember {
                val start = today.minusDays(7)
                val end = today.plusDays(90) // Extended to 90 days for future scrolling
                generateSequence(start) { it.plusDays(1) }.takeWhile { !it.isAfter(end) }.toList()
        }

        // LazyListState for the date strip
        val todayIndex = remember(dateItems, today) { dateItems.indexOf(today).coerceAtLeast(0) }
        val dateStripListState = rememberLazyListState(initialFirstVisibleItemIndex = todayIndex)

        var currentDestination by remember { mutableStateOf(HabitMateDestination.HOME) }
        var selectedFilter by remember { mutableStateOf(HabitTimeFilter.ALL_DAY) }

        // Coroutine scope for animated scrolling
        val coroutineScope = rememberCoroutineScope()

        val totalHabitsToday = habits.size
        val doneToday = habits.count { it.isDoneToday }
        val progressToday =
                if (totalHabitsToday == 0) 0f else doneToday.toFloat() / totalHabitsToday

        Scaffold(
                topBar = {
                        ModernTopBar(
                                title = "Hello, Mate! 👋", // Sapaan personal
                                subtitle = dateLabelFor(selectedDate, today),
                                onSettingsClick = { /* TODO */},
                                onDateClick = {
                                        // Quick return to today with animated scroll
                                        selectedDate = today
                                        coroutineScope.launch {
                                                dateStripListState.animateScrollToItem(todayIndex)
                                        }
                                }
                        )
                },
                floatingActionButton = {
                        if (currentDestination == HabitMateDestination.HOME) {
                                FloatingActionButton(
                                        onClick = { showAddSheet = true },
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
                                                viewModel.toggleHabit(habitId)
                                        },
                                        onResetHabit = { habitId -> viewModel.resetHabit(habitId) },
                                        dateStripListState = dateStripListState,
                                        onHabitClick = { selectedHabit = it },
                                        startAnimation = hasAnimated,
                                        viewModel = viewModel
                                )
                        }
                        HabitMateDestination.HABITS -> {
                                com.example.habitmate.ui.habits.HabitsManagerScreen(
                                        viewModel = viewModel,
                                        modifier = Modifier.padding(innerPadding)
                                )
                        }
                        else -> PlaceholderScreen(modifier = Modifier.padding(innerPadding))
                }
        }

        if (showAddSheet) {
                ModalBottomSheet(
                        onDismissRequest = { showAddSheet = false },
                        sheetState = rememberModalBottomSheetState(),
                        containerColor = CardSurface
                ) {
                        HabitTemplateSheet(
                                onClose = { showAddSheet = false },
                                onCreateCustom = {
                                        showAddSheet = false
                                        onNavigateToCreateHabit(null, null)
                                },
                                onTemplateSelect = { template ->
                                        showAddSheet = false
                                        onNavigateToCreateHabit(template.title, template.emoji)
                                }
                        )
                }
        }

        if (selectedHabit != null) {
                ModalBottomSheet(
                        onDismissRequest = { selectedHabit = null },
                        sheetState = sheetState,
                        containerColor = CardSurface
                ) {
                        HabitDetailScreen(
                                habit = selectedHabit!!,
                                onClose = {
                                        coroutineScope
                                                .launch { sheetState.hide() }
                                                .invokeOnCompletion {
                                                        if (!sheetState.isVisible)
                                                                selectedHabit = null
                                                }
                                },
                                onEdit = { /* TODO: Edit */},
                                onDelete = { /* TODO: Delete */},
                                onToggleCompletion = { id -> viewModel.toggleHabit(id) }
                        )
                }
        }
}

// ---------- TOP BAR & BOTTOM NAV ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(
        title: String,
        subtitle: String,
        onSettingsClick: () -> Unit,
        onDateClick: () -> Unit = {}
) {
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
                                                ),
                                        modifier = Modifier.clickable { onDateClick() }
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
        onToggleHabit: (Int) -> Unit,
        onResetHabit: (Int) -> Unit,
        dateStripListState: androidx.compose.foundation.lazy.LazyListState, // Added parameter
        onHabitClick: (HabitUi) -> Unit,
        startAnimation: Boolean,
        viewModel: HomeViewModel // New param
) {
        // FIX: Local state to trigger entry animation immediately on first launch
        // even if the persistent 'startAnimation' flag is delayed.
        var isContentVisible by remember { mutableStateOf(startAnimation) }

        LaunchedEffect(Unit) {
                if (!startAnimation) {
                        isContentVisible = true
                }
        }

        LazyColumn(
                modifier = modifier.background(BackgroundColor),
                contentPadding = PaddingValues(bottom = 100.dp) // Space for FAB
        ) {
                item {
                        ModernDateStrip(
                                today = today,
                                selectedDate = selectedDate,
                                dates = dateItems,
                                onDateSelected = onDateSelected,
                                listState = dateStripListState
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                        val alpha by
                                animateFloatAsState(
                                        targetValue = if (isContentVisible) 1f else 0f,
                                        animationSpec =
                                                tween(
                                                        durationMillis = 500,
                                                        delayMillis = 0
                                                ), // No delay for first item
                                        label = "cardAlpha"
                                )
                        val slideY by
                                animateDpAsState(
                                        targetValue = if (isContentVisible) 0.dp else 50.dp,
                                        animationSpec =
                                                tween(durationMillis = 500, delayMillis = 0),
                                        label = "cardSlide"
                                )

                        Box(
                                modifier =
                                        Modifier.padding(horizontal = 24.dp).graphicsLayer {
                                                this.alpha = alpha
                                                translationY = slideY.toPx()
                                        }
                        ) {
                                GradientProgressCard(
                                        progress = progressToday,
                                        done = doneToday,
                                        total = totalHabitsToday
                                )
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                        val alpha by
                                animateFloatAsState(
                                        targetValue = if (isContentVisible) 1f else 0f,
                                        animationSpec =
                                                tween(
                                                        durationMillis = 500,
                                                        delayMillis = 150
                                                ), // Cascade delay
                                        label = "filterAlpha"
                                )
                        val slideY by
                                animateDpAsState(
                                        targetValue = if (isContentVisible) 0.dp else 50.dp,
                                        animationSpec =
                                                tween(durationMillis = 500, delayMillis = 150),
                                        label = "filterSlide"
                                )

                        Box(
                                modifier =
                                        Modifier.graphicsLayer {
                                                this.alpha = alpha
                                                translationY = slideY.toPx()
                                        }
                        ) {
                                FilterCapsuleRow(
                                        selected = selectedFilter,
                                        onSelected = onFilterSelected
                                )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                }

                HabitList(
                        habits = habits,
                        filter = selectedFilter,
                        onToggle = onToggleHabit,
                        onReset = onResetHabit,
                        onHabitClick = onHabitClick,
                        startAnimation = startAnimation
                )
        }
}

// ---------- COMPONENT: DATE STRIP (Capsule Style) ----------

@Composable
fun ModernDateStrip(
        today: LocalDate,
        selectedDate: LocalDate,
        dates: List<LocalDate>,
        onDateSelected: (LocalDate) -> Unit,
        listState: androidx.compose.foundation.lazy.LazyListState // Added parameter
) {
        LazyRow(
                state = listState,
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

fun LazyListScope.HabitList(
        habits: List<HabitUi>,
        filter: HabitTimeFilter,
        onToggle: (Int) -> Unit,
        onReset: (Int) -> Unit,
        onHabitClick: (HabitUi) -> Unit,
        startAnimation: Boolean
) {
        val filteredHabits =
                habits.filter {
                        if (filter == HabitTimeFilter.ALL_DAY) true
                        else
                                when (filter) {
                                        HabitTimeFilter.MORNING ->
                                                it.timeOfDay == HabitTimeOfDay.MORNING
                                        HabitTimeFilter.AFTERNOON ->
                                                it.timeOfDay == HabitTimeOfDay.AFTERNOON
                                        HabitTimeFilter.EVENING ->
                                                it.timeOfDay == HabitTimeOfDay.EVENING
                                        else -> true
                                }
                }

        if (filteredHabits.isEmpty()) {
                item {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        "No habits for this time",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextSecondary
                                )
                        }
                }
        } else {
                itemsIndexed(filteredHabits, key = { _, habit -> habit.id }) { index, habit ->
                        // If app just started (startAnimation was false initially, now
                        // transitioning), play animation.
                        // If App has already animated (startAnimation=true from VM), show
                        // immediately (alpha=1f).
                        // BUT, we want animation ONLY if it *wasn't* animated before.
                        // The VM state starts as false.
                        // So we want: target = 1f. Initial = 0f if !hasAnimated.
                        // Actually, we can use a local state that defaults to
                        // `startAnimation` (which is hasAnimated).

                        // FIX: Initialize based on startAnimation (true = already animated, show
                        // immediately)
                        var isVisible by remember { mutableStateOf(startAnimation) }

                        // FIX: Always trigger animation sequence
                        LaunchedEffect(Unit) {
                                if (!startAnimation) {
                                        kotlinx.coroutines.delay(
                                                300L + (index * 100L)
                                        ) // Wait for Card & Filter
                                        isVisible = true
                                }
                        }

                        val alpha by
                                animateFloatAsState(
                                        targetValue = if (isVisible) 1f else 0f,
                                        animationSpec =
                                                tween(
                                                        durationMillis = 500,
                                                        easing =
                                                                androidx.compose.animation.core
                                                                        .FastOutSlowInEasing
                                                ),
                                        label = "alpha"
                                )
                        val slideY by
                                animateDpAsState(
                                        targetValue = if (isVisible) 0.dp else 50.dp,
                                        animationSpec =
                                                tween(
                                                        durationMillis = 500,
                                                        easing =
                                                                androidx.compose.animation.core
                                                                        .FastOutSlowInEasing
                                                ),
                                        label = "slideY"
                                )

                        Box(
                                modifier =
                                        Modifier.graphicsLayer {
                                                        this.alpha = alpha
                                                        translationY = slideY.toPx()
                                                }
                                                .padding(
                                                        bottom = 16.dp
                                                ) // FIX: Add spacing to prevent collision
                        ) {
                                OceanHabitCard(
                                        habit = habit,
                                        onToggle = { onToggle(habit.id) },
                                        onReset = { onReset(habit.id) },
                                        onClick = { onHabitClick(habit) }
                                )
                        }
                }
        }
}

// ---------- COMPONENT: OCEAN HABIT CARD (Redesign) ----------

@Composable
fun OceanHabitCard(habit: HabitUi, onToggle: () -> Unit, onReset: () -> Unit, onClick: () -> Unit) {
        Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardSurface,
                shadowElevation = 8.dp, // Softer shadow
                tonalElevation = 2.dp,
                modifier =
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { onClick() }
        ) {
                // Subtle Gradient Background Overlay
                Box(
                        modifier =
                                Modifier.background(
                                        Brush.linearGradient(
                                                colors =
                                                        listOf(
                                                                Color.White,
                                                                Color(
                                                                        0xFFF0F9FF
                                                                ) // Very light blue tint
                                                        )
                                        )
                                )
                ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                // Vibrant Icon Container
                                Box(
                                        modifier =
                                                Modifier.size(56.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(OceanGradient),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = habit.emoji,
                                                style = MaterialTheme.typography.headlineSmall,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = habit.title,
                                                style =
                                                        MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color = TextPrimary
                                                        )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                                text =
                                                        "${habit.current}/${habit.target} ${habit.unitLabel}",
                                                style =
                                                        MaterialTheme.typography.labelMedium.copy(
                                                                fontWeight = FontWeight.Medium,
                                                                color = TextSecondary
                                                        )
                                        )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Circular Progress Integration
                                CircularProgressButton(
                                        current = habit.current,
                                        target = habit.target,
                                        isDone = habit.isDoneToday,
                                        onIncrement = onToggle,
                                        onReset = onReset
                                )
                        }
                }
        }
}

// ---------- COMPONENT: CIRCULAR PROGRESS BUTTON (Smart Interaction) ----------

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CircularProgressButton(
        current: Int,
        target: Int,
        isDone: Boolean,
        onIncrement: () -> Unit,
        onReset: () -> Unit
) {
        val progress = if (target > 0) current.toFloat() / target else 0f

        // Animation States
        val animatedProgress by
                animateFloatAsState(
                        targetValue = progress,
                        animationSpec =
                                androidx.compose.animation.core.spring(
                                        stiffness =
                                                androidx.compose.animation.core.Spring.StiffnessLow
                                ),
                        label = "progress"
                )

        // Scale animation on press is handled by clickable interactions usually,
        // but here we just animate the checkmark/ring transition.

        Box(
                modifier =
                        Modifier.size(48.dp)
                                .clip(CircleShape)
                                // Combined Clickable & Long Clickable
                                .combinedClickable(onClick = onIncrement, onLongClick = onReset),
                contentAlignment = Alignment.Center
        ) {
                if (isDone) {
                        // Completed State: Filled Gradient Circle with Checkmark
                        Box(
                                modifier = Modifier.fillMaxSize().background(OceanGradient),
                                contentAlignment = Alignment.Center
                        ) {

                                // Or draw a checkmark manually for cleaner look
                                Canvas(modifier = Modifier.size(16.dp)) {
                                        val path =
                                                androidx.compose.ui.graphics.Path().apply {
                                                        moveTo(0f, size.height * 0.5f)
                                                        lineTo(
                                                                size.width * 0.4f,
                                                                size.height * 0.9f
                                                        )
                                                        lineTo(size.width, size.height * 0.2f)
                                                }
                                        drawPath(
                                                path = path,
                                                color = Color.White,
                                                style =
                                                        androidx.compose.ui.graphics.drawscope
                                                                .Stroke(
                                                                        width = 5f,
                                                                        cap =
                                                                                androidx.compose.ui
                                                                                        .graphics
                                                                                        .StrokeCap
                                                                                        .Round,
                                                                        join =
                                                                                androidx.compose.ui
                                                                                        .graphics
                                                                                        .StrokeJoin
                                                                                        .Round
                                                                )
                                        )
                                }
                        }
                } else {
                        // In-Progress State: Ring
                        Canvas(modifier = Modifier.fillMaxSize()) {
                                // Background Track
                                drawCircle(
                                        color = Color.LightGray.copy(alpha = 0.3f),
                                        style =
                                                androidx.compose.ui.graphics.drawscope.Stroke(
                                                        width = 8f
                                                )
                                )

                                // Progress Arc
                                drawArc(
                                        brush = OceanGradient,
                                        startAngle = -90f,
                                        sweepAngle = 360 * animatedProgress,
                                        useCenter = false,
                                        style =
                                                androidx.compose.ui.graphics.drawscope.Stroke(
                                                        width = 8f,
                                                        cap =
                                                                androidx.compose.ui.graphics
                                                                        .StrokeCap.Round
                                                )
                                )
                        }

                        // Text Indicator for steps (Optional, maybe too small? Let's just keep
                        // clean ring)
                        /*
                        Text(
                            text = "${current}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondary
                        )
                        */
                        // Plus icon in center to encourage tapping?
                        if (current == 0) {
                                Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Start",
                                        tint = SecondaryColor,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }
        }
}

// ---------- COMPONENT: CUSTOM ANIMATED CHECKBOX ----------

@Composable
fun AnimatedOceanCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
) {
        // Animate the scale when checked/unchecked
        val scale by
                animateFloatAsState(
                        targetValue = if (checked) 1.0f else 0.9f,
                        animationSpec =
                                androidx.compose.animation.core.spring(
                                        dampingRatio =
                                                androidx.compose.animation.core.Spring
                                                        .DampingRatioMediumBouncy,
                                        stiffness =
                                                androidx.compose.animation.core.Spring.StiffnessLow
                                ),
                        label = "checkboxScale"
                )

        // Animate the checkmark alpha
        val checkmarkAlpha by
                animateFloatAsState(
                        targetValue = if (checked) 1f else 0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "checkmarkAlpha"
                )

        val backgroundColor = if (checked) PrimaryColor else Color.Transparent
        val borderColor = if (checked) PrimaryColor else TextSecondary.copy(alpha = 0.3f)

        Box(
                modifier =
                        modifier.size(28.dp)
                                .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                }
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .border(2.dp, borderColor, CircleShape)
                                .clickable(onClick = { onCheckedChange(!checked) }),
                contentAlignment = Alignment.Center
        ) {
                // Checkmark icon
                if (checked) {
                        Canvas(
                                modifier =
                                        Modifier.size(16.dp).graphicsLayer {
                                                alpha = checkmarkAlpha
                                        }
                        ) {
                                val strokeWidth = 2.5f
                                val checkPath =
                                        androidx.compose.ui.graphics.Path().apply {
                                                moveTo(size.width * 0.2f, size.height * 0.5f)
                                                lineTo(size.width * 0.4f, size.height * 0.7f)
                                                lineTo(size.width * 0.8f, size.height * 0.3f)
                                        }
                                drawPath(
                                        path = checkPath,
                                        color = Color.White,
                                        style =
                                                androidx.compose.ui.graphics.drawscope.Stroke(
                                                        width = strokeWidth,
                                                        cap =
                                                                androidx.compose.ui.graphics
                                                                        .StrokeCap.Round,
                                                        join =
                                                                androidx.compose.ui.graphics
                                                                        .StrokeJoin.Round
                                                )
                                )
                        }
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

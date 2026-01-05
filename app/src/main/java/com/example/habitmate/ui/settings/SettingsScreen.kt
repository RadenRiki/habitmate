package com.example.habitmate.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitmate.notification.NotificationReceiver

// Ocean theme colors
private val BackgroundColor = Color(0xFFF8FAFC)
private val CardSurface = Color(0xFFFFFFFF)
private val PrimaryBlue = Color(0xFF2563EB)
private val AccentCyan = Color(0xFF06B6D4)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val DividerColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
        onBack: () -> Unit,
        onNavigateToTerms: () -> Unit,
        onNavigateToPrivacy: () -> Unit,
        onNavigateToAbout: () -> Unit
) {
        val context = LocalContext.current
        val prefs = remember {
                context.getSharedPreferences("habit_settings", android.content.Context.MODE_PRIVATE)
        }

        // Load saved preferences
        var notificationEnabled by remember {
                mutableStateOf(prefs.getBoolean("notification_enabled", false))
        }
        var notificationHour by remember { mutableStateOf(prefs.getInt("notification_hour", 9)) }
        var notificationMinute by remember {
                mutableStateOf(prefs.getInt("notification_minute", 0))
        }
        var showTimePicker by remember { mutableStateOf(false) }

        // Permission launcher for Android 13+
        val permissionLauncher =
                rememberLauncherForActivityResult(
                        contract =
                                androidx.activity.result.contract.ActivityResultContracts
                                        .RequestPermission()
                ) { granted ->
                        if (granted) {
                                notificationEnabled = true
                                prefs.edit().putBoolean("notification_enabled", true).apply()
                                NotificationReceiver.scheduleNotification(
                                        context,
                                        notificationHour,
                                        notificationMinute
                                )
                        }
                }

        // Function to enable notifications with permission check
        fun enableNotifications() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        if (androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                                // Already have permission
                                notificationEnabled = true
                                prefs.edit().putBoolean("notification_enabled", true).apply()
                                NotificationReceiver.scheduleNotification(
                                        context,
                                        notificationHour,
                                        notificationMinute
                                )
                        } else {
                                // Request permission
                                permissionLauncher.launch(
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                )
                        }
                } else {
                        // No runtime permission needed for Android < 13
                        notificationEnabled = true
                        prefs.edit().putBoolean("notification_enabled", true).apply()
                        NotificationReceiver.scheduleNotification(
                                context,
                                notificationHour,
                                notificationMinute
                        )
                }
        }

        val timePickerState =
                rememberTimePickerState(
                        initialHour = notificationHour,
                        initialMinute = notificationMinute,
                        is24Hour = false
                )

        Column(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
                // Top Bar with gradient
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
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                        ) {
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

                                Column {
                                        Text(
                                                text = "Settings",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                        Text(
                                                text = "Customize your experience",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.8f)
                                        )
                                }
                        }
                }

                // Content
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Notifications Section
                        SettingsSection(title = "Notifications") {
                                // Enable/Disable Toggle
                                SettingsToggleItem(
                                        icon = Icons.Default.Notifications,
                                        title = "Daily Reminder",
                                        subtitle = "Get reminded to check your habits",
                                        checked = notificationEnabled,
                                        onCheckedChange = { enabled ->
                                                if (enabled) {
                                                        // Call function that checks permission
                                                        // first
                                                        enableNotifications()
                                                } else {
                                                        notificationEnabled = false
                                                        prefs.edit()
                                                                .putBoolean(
                                                                        "notification_enabled",
                                                                        false
                                                                )
                                                                .apply()
                                                        NotificationReceiver.cancelNotification(
                                                                context
                                                        )
                                                }
                                        }
                                )

                                if (notificationEnabled) {
                                        HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))

                                        // Time Picker
                                        SettingsClickableItem(
                                                icon = Icons.Default.Schedule,
                                                title = "Reminder Time",
                                                subtitle =
                                                        formatTime(
                                                                notificationHour,
                                                                notificationMinute
                                                        ),
                                                onClick = { showTimePicker = true }
                                        )
                                }
                        }

                        // Legal Section
                        SettingsSection(title = "Legal") {
                                SettingsClickableItem(
                                        icon = Icons.Default.Description,
                                        title = "Terms of Use",
                                        subtitle = "Read our terms and conditions",
                                        onClick = onNavigateToTerms
                                )

                                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f))

                                SettingsClickableItem(
                                        icon = Icons.Default.Shield,
                                        title = "Privacy Policy",
                                        subtitle = "How we protect your data",
                                        onClick = onNavigateToPrivacy
                                )
                        }

                        // About Section
                        SettingsSection(title = "About") {
                                SettingsClickableItem(
                                        icon = Icons.Default.Info,
                                        title = "About HabitMate",
                                        subtitle = "Version, credits, and more",
                                        onClick = onNavigateToAbout
                                )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                }
        }

        // Time Picker Dialog
        if (showTimePicker) {
                AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        title = { Text("Set Reminder Time") },
                        text = { TimePicker(state = timePickerState) },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                notificationHour = timePickerState.hour
                                                notificationMinute = timePickerState.minute
                                                showTimePicker = false

                                                // Save to SharedPreferences
                                                prefs.edit()
                                                        .putInt(
                                                                "notification_hour",
                                                                notificationHour
                                                        )
                                                        .putInt(
                                                                "notification_minute",
                                                                notificationMinute
                                                        )
                                                        .apply()

                                                // Reschedule notification
                                                if (notificationEnabled) {
                                                        NotificationReceiver.scheduleNotification(
                                                                context,
                                                                notificationHour,
                                                                notificationMinute
                                                        )
                                                }
                                        }
                                ) { Text("OK") }
                        },
                        dismissButton = {
                                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                        }
                )
        }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
        Column {
                Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) { Column(modifier = Modifier.padding(vertical = 4.dp), content = content) }
        }
}

@Composable
private fun SettingsClickableItem(
        icon: ImageVector,
        title: String,
        subtitle: String,
        onClick: () -> Unit
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable(onClick = onClick)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier =
                                Modifier.size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                        )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                        )
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                        )
                }

                Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.5f)
                )
        }
}

@Composable
private fun SettingsToggleItem(
        icon: ImageVector,
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        val iconBgColor by
                animateColorAsState(
                        targetValue =
                                if (checked) PrimaryBlue.copy(alpha = 0.1f)
                                else Color.Gray.copy(alpha = 0.1f),
                        animationSpec = tween(300)
                )

        Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Box(
                        modifier =
                                Modifier.size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(iconBgColor),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = if (checked) PrimaryBlue else Color.Gray,
                                modifier = Modifier.size(22.dp)
                        )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                        )
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                        )
                }

                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PrimaryBlue,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                )
        }
}

private fun formatTime(hour: Int, minute: Int): String {
        val period = if (hour < 12) "AM" else "PM"
        val displayHour =
                when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                }
        return String.format("%d:%02d %s", displayHour, minute, period)
}

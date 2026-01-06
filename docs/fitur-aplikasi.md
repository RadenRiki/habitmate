# Dokumentasi Fitur Aplikasi HabitMate

Dokumen ini menjelaskan desain dan cara kerja setiap fitur utama di aplikasi HabitMate, termasuk bagaimana data ditampilkan dan alur datanya.

---

## 📱 1. Home Screen

**File:** [HomeScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeScreen.kt)

### Desain Layout

Home Screen adalah halaman utama aplikasi yang terdiri dari beberapa komponen:

```
┌────────────────────────────────────┐
│  📅 ModernTopBar                   │  → Menampilkan tanggal hari ini
├────────────────────────────────────┤
│  🗓️ ModernDateStrip                │  → Horizontal scroll tanggal (LazyRow)
├────────────────────────────────────┤
│  📊 GradientProgressCard           │  → Ringkasan progress hari ini
├────────────────────────────────────┤
│  🔘 FilterCapsuleRow               │  → Filter: All, Morning, Afternoon, Evening
├────────────────────────────────────┤
│  📋 HabitList (LazyColumn)         │  → Daftar habit cards
│    ├── OceanHabitCard 1            │
│    ├── OceanHabitCard 2            │
│    └── ...                         │
├────────────────────────────────────┤
│  🧭 BottomNavBar                   │  → Navigasi: Today, Stats, Habits
└────────────────────────────────────┘
```

### Cara Data Ditampilkan

**1. Mengambil Data dari ViewModel:**
```kotlin
@Composable
fun HabitMateHomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    // Observe StateFlow sebagai Compose State
    val habits by viewModel.uiState.collectAsState()
    
    // habits akan otomatis update saat data di Firestore berubah
}
```

**2. Filter Berdasarkan Tanggal:**
```kotlin
// User pilih tanggal di DateStrip → update selectedDate di ViewModel
var selectedDate by remember { mutableStateOf(today) }

// Data habits akan otomatis difilter sesuai tanggal yang dipilih
// Ini terjadi di HomeViewModel.uiState yang menggabungkan:
// - allHabits
// - selectedDate  
// - allHistory
```

**3. Progress Card Menghitung Otomatis:**
```kotlin
// Menghitung berapa habit yang sudah selesai hari ini
val done = habits.count { it.isDoneToday }      // Habit dengan isDoneToday = true
val total = habits.size                          // Total habit
val progress = if (total > 0) done.toFloat() / total else 0f

// Ditampilkan di GradientProgressCard
GradientProgressCard(
    done = done,          // 3
    total = total,        // 5
    progress = progress   // 0.6 (60%)
)
```

**4. Filter Waktu (Morning/Afternoon/Evening):**
```kotlin
// User tap filter capsule → update selectedFilter
var selectedFilter by remember { mutableStateOf(HabitTimeFilter.ALL_DAY) }

// Habit difilter berdasarkan timeOfDay
val filteredHabits = habits.filter { habit ->
    when (selectedFilter) {
        HabitTimeFilter.ALL_DAY -> true  // Tampilkan semua
        HabitTimeFilter.MORNING -> habit.timeOfDay == HabitTimeOfDay.MORNING
        HabitTimeFilter.AFTERNOON -> habit.timeOfDay == HabitTimeOfDay.AFTERNOON
        HabitTimeFilter.EVENING -> habit.timeOfDay == HabitTimeOfDay.EVENING
    }
}
```

---

## 🎴 2. Habit Detail Screen (Card Template)

**File:** [HabitDetailScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HabitDetailScreen.kt)

### Desain Layout

Habit Detail Screen adalah bottom sheet yang muncul saat user tap habit card:

```
┌────────────────────────────────────┐
│                    [Edit] [Close]  │  → Action buttons
├────────────────────────────────────┤
│           ┌─────┐                  │
│           │ 💧  │                  │  → Emoji dalam circle dengan gradient border
│           └─────┘                  │
│        Drink Water                 │  → Title
│      Morning Routine               │  → Time of Day
├────────────────────────────────────┤
│   🔥        ⭐         🕐          │
│   5         80%        42         │  → Statistics Row
│  Streak   Success    Total        │
├────────────────────────────────────┤
│  Today's Progress                  │
│  ┌──────────────────────────────┐  │
│  │  2 / 8 cups         [-] [+] │  │  → Progress dengan action buttons
│  └──────────────────────────────┘  │
├────────────────────────────────────┤
│  Recent Activity (Last 7 Days)     │
│   S   M   T   W   T   F   S        │  → Mini calendar
│   ●   ●   ○   ●   ●   ●   ○        │  → ● = selesai, ○ = tidak
└────────────────────────────────────┘
```

### Cara Data Ditampilkan

**1. Menerima Data Habit:**
```kotlin
@Composable
fun HabitDetailScreen(
    habit: HabitUi,                    // Data habit yang akan ditampilkan
    onClose: () -> Unit,
    onEdit: (String) -> Unit,
    onToggleCompletion: (String) -> Unit,
    onDecrement: (String) -> Unit
) {
    // Semua data sudah tersedia di object 'habit'
}
```

**2. Menampilkan Statistik:**
```kotlin
// Statistics Row - data berasal dari HabitUi
Row(horizontalArrangement = Arrangement.SpaceEvenly) {
    StatItem(
        icon = Icons.Default.LocalFireDepartment,
        value = "${habit.streak}",       // Streak (hari berturut-turut)
        label = "Streak",
        color = Color(0xFFF97316)
    )
    StatItem(
        icon = Icons.Default.Star,
        value = "${habit.successRate}%", // Success rate 7 hari terakhir
        label = "Success (7 Days)",
        color = Color(0xFFEAB308)
    )
    StatItem(
        icon = Icons.Default.History,
        value = "${habit.totalCompletions}", // Total completion sepanjang waktu
        label = "Total",
        color = PrimaryColor
    )
}
```

**3. Progress Hari Ini:**
```kotlin
// Menampilkan progress dan tombol +/-
Text(
    text = "${habit.current} / ${habit.target} ${habit.unitLabel}"
    // Contoh: "2 / 8 cups"
)

// Tombol minus
Box(modifier = Modifier.clickable { onDecrement(habit.id) }) {
    Text("-")
}

// Tombol plus/check
Box(modifier = Modifier.clickable { onToggleCompletion(habit.id) }) {
    Text(if (habit.isDoneToday) "✓" else "+")
}
```

**4. Recent Activity (Mini Calendar):**
```kotlin
// Menampilkan history 7 hari terakhir
val today = java.time.LocalDate.now()
val history = habit.recentHistory  // List<Boolean> ukuran 7

(0..6).forEach { index ->
    val date = today.minusDays((6 - index).toLong())
    val dayLetter = date.dayOfWeek.name.first().toString()  // "M", "T", dll
    val isCompleted = history.getOrElse(index) { false }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(dayLetter)                                      // "M"
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    if (isCompleted) PrimaryColor            // Biru = selesai
                    else Color.LightGray                      // Abu = tidak selesai
                )
        )
    }
}
```

---

## ⚙️ 3. Habits Manager Screen (Edit & Delete)

**File:** [HabitsManagerScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/habits/HabitsManagerScreen.kt)

### Desain Layout

```
┌────────────────────────────────────┐
│  🔍 Search...                      │  → Search bar untuk filter
├────────────────────────────────────┤
│  ┌──────────────────────────────┐  │
│  │ 💧 Drink Water               │  │
│  │ Everyday • 5 day streak   ✏️🗑️│  │  → Edit & Delete buttons
│  └──────────────────────────────┘  │
│  ┌──────────────────────────────┐  │
│  │ 🏃 Morning Run               │  │
│  │ Custom • 3 day streak     ✏️🗑️│  │
│  └──────────────────────────────┘  │
│  ...                               │
└────────────────────────────────────┘
```

### Cara Data Ditampilkan

**1. Mengambil Semua Habits:**
```kotlin
@Composable
fun HabitsManagerScreen(
    viewModel: HomeViewModel,
    onNavigateToEdit: (String) -> Unit
) {
    // Mengambil SEMUA habits (tidak difilter tanggal)
    val allHabits by viewModel.allHabits.collectAsState()
}
```

**2. Search/Filter Lokal:**
```kotlin
var searchQuery by remember { mutableStateOf("") }

// Filter berdasarkan search query
val filteredHabits = remember(allHabits, searchQuery) {
    if (searchQuery.isBlank()) {
        allHabits                                           // Tampilkan semua
    } else {
        allHabits.filter { 
            it.title.contains(searchQuery, ignoreCase = true)  // Case-insensitive search
        }
    }
}
```

**3. Menampilkan List dengan LazyColumn:**
```kotlin
LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(filteredHabits) { habit ->
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
```

### Fitur Edit

```kotlin
// Edit button di setiap item
IconButton(onClick = { onNavigateToEdit(habit.id) }) {
    Icon(Icons.Default.Edit, contentDescription = "Edit")
}

// Di MainActivity, navigate ke CreateHabitScreen dengan habitId
onNavigateToEdit = { habitId ->
    navController.navigate("create_habit?habitId=$habitId")
}

// CreateHabitScreen akan load data habit yang ada dan mengisinya ke form
val habitToEdit = allHabits.find { it.id == habitId }
CreateHabitScreen(
    habitToEdit = habitToEdit,  // Data existing untuk di-edit
    onSave = { updatedHabit ->
        if (habitId != null) {
            viewModel.updateHabit(updatedHabit)  // Update existing
        } else {
            viewModel.addHabit(...)              // Create new
        }
    }
)
```

### Fitur Delete

```kotlin
// State untuk dialog konfirmasi
var showDeleteDialog by remember { mutableStateOf(false) }
var habitToDelete by remember { mutableStateOf<HabitUi?>(null) }

// Tampilkan dialog konfirmasi
if (showDeleteDialog && habitToDelete != null) {
    AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Delete Habit?") },
        text = { 
            Text("Are you sure you want to delete '${habitToDelete?.title}'? " +
                 "This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    habitToDelete?.let { viewModel.deleteHabit(it) }  // Hapus via ViewModel
                    showDeleteDialog = false
                    Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
                }
            ) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteDialog = false }) { 
                Text("Cancel") 
            }
        }
    )
}
```

---

## 📊 4. Stats Screen

**File:** [StatsScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/stats/StatsScreen.kt)

### Desain Layout

```
┌────────────────────────────────────┐
│  ┌─────────────┐ ┌─────────────┐   │
│  │ 📈 75%      │ │ 🔥 12 days  │   │  → Overview Cards
│  │ Avg. Comp.  │ │ Best Streak │   │
│  └─────────────┘ └─────────────┘   │
├────────────────────────────────────┤
│  Consistency        Last 7 Days    │
│  ┌──────────────────────────────┐  │
│  │   █                          │  │
│  │   █   █       █              │  │  → Weekly Bar Chart
│  │   █   █   █   █   █          │  │
│  │  Mon Tue Wed Thu Fri Sat Sun │  │
│  └──────────────────────────────┘  │
├────────────────────────────────────┤
│  Top Performers                    │
│  ┌──────────────────────────────┐  │
│  │ 💧 Drink Water    90% 🏆 5   │  │
│  │ 🏃 Morning Run    85% 🏆 3   │  │  → Sorted by success rate
│  │ 📚 Read Book      70% 🏆 2   │  │
│  └──────────────────────────────┘  │
└────────────────────────────────────┘
```

### Cara Data Ditampilkan

**1. Menerima Data Habits:**
```kotlin
@Composable
fun StatsScreen(
    habits: List<HabitUi>,  // Semua habits dengan data statistik
    modifier: Modifier = Modifier
) {
    // Data habits sudah berisi: successRate, streak, recentHistory
}
```

**2. Menghitung Average Completion Rate:**
```kotlin
// Hitung rata-rata success rate semua habits
val completionRate = remember(habits) {
    if (habits.isEmpty()) 0 
    else habits.map { it.successRate }.average().toInt()
}

// Contoh: habits = [{successRate: 80}, {successRate: 60}, {successRate: 90}]
// completionRate = (80 + 60 + 90) / 3 = 76%
```

**3. Mencari Best Streak:**
```kotlin
// Cari streak tertinggi dari semua habits
val bestStreak = remember(habits) { 
    habits.maxOfOrNull { it.streak } ?: 0 
}

// Contoh: habits = [{streak: 5}, {streak: 12}, {streak: 3}]
// bestStreak = 12
```

**4. Menghitung Consistency Data (Bar Chart):**
```kotlin
// Agregasi: berapa habit yang selesai per hari (7 hari terakhir)
val consistencyData = remember(habits) {
    val daysCount = IntArray(7) { 0 }  // Array untuk 7 hari
    
    habits.forEach { habit ->
        habit.recentHistory.forEachIndexed { index, isDone ->
            if (isDone) daysCount[index]++  // Increment jika selesai
        }
    }
    
    daysCount.toList()  // [3, 4, 2, 5, 4, 3, 2]
}

// Contoh dengan 5 habits:
// Hari 0 (6 hari lalu): 3 habits selesai
// Hari 1 (5 hari lalu): 4 habits selesai
// ...
// Hari 6 (hari ini): 2 habits selesai
```

**5. Menampilkan Bar Chart dengan Animasi:**
```kotlin
@Composable
fun WeeklyBarChart(dailyCounts: List<Int>, maxPossible: Int) {
    // Normalize: count / maxPossible
    val normalizedData = dailyCounts.map { it.toFloat() / maxPossible }
    // Contoh: [3, 4, 2, 5, 4, 3, 2] dengan 5 habits
    // → [0.6, 0.8, 0.4, 1.0, 0.8, 0.6, 0.4]
    
    // Animate setiap bar
    val animatedValues = normalizedData.map { target ->
        val anim = remember(target) { Animatable(0f) }
        LaunchedEffect(target) {
            anim.animateTo(
                targetValue = target.coerceAtLeast(0.05f),  // Min height
                animationSpec = tween(1000, delayMillis = 200)
            )
        }
        anim.value
    }
    
    // Render bars
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        weekDays.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .fillMaxHeight(animatedValues[index])  // Height sesuai data
                    .background(gradientBrush)
            )
        }
    }
}
```

**6. Top Performers (Sorted by Success Rate):**
```kotlin
// Sort habits by success rate, ambil top 5
val topHabits = remember(habits) { 
    habits.sortedByDescending { it.successRate }.take(5) 
}

// Tampilkan di list
items(topHabits) { habit ->
    HabitStatRow(habit)  // Menampilkan emoji, title, successRate, streak
}
```

---

## 🔔 5. Notifications System

**Files:** 
- [NotificationHelper.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/notification/NotificationHelper.kt)
- [NotificationReceiver.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/notification/NotificationReceiver.kt)
- [SettingsScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/settings/SettingsScreen.kt)

### Cara Kerja Notification

```
┌─────────────────────────────────────────────────────────────────┐
│                        ALUR NOTIFICATION                         │
└─────────────────────────────────────────────────────────────────┘

1. USER ENABLE NOTIFICATION (SettingsScreen)
   │
   ├─→ Check Android version >= 13?
   │   ├─→ YES: Request POST_NOTIFICATIONS permission
   │   │        ├─→ Granted: Continue
   │   │        └─→ Denied: Stop
   │   └─→ NO: Continue (no permission needed)
   │
   ├─→ Save preference to SharedPreferences
   │   • notification_enabled = true
   │   • notification_hour = 9
   │   • notification_minute = 0
   │
   └─→ Schedule notification via NotificationReceiver
       │
       ▼
2. ALARMMANAGER SETS ALARM
   │
   ├─→ Calculate next trigger time using Calendar
   │   • Set hour & minute from preferences
   │   • If time already passed today, add 1 day
   │
   └─→ alarmManager.setExactAndAllowWhileIdle(
           AlarmManager.RTC_WAKEUP,     // Wake device if sleeping
           triggerTimeMillis,
           pendingIntent                 // Points to NotificationReceiver
       )
       │
       ▼
3. ALARM FIRES AT SCHEDULED TIME
   │
   └─→ System broadcasts Intent to NotificationReceiver
       │
       ▼
4. NOTIFICATIONRECEIVER.onReceive()
   │
   ├─→ NotificationHelper.showReminderNotification(context)
   │   │
   │   ├─→ Check permission (Android 13+)
   │   │
   │   ├─→ Build notification:
   │   │   • Icon: app icon
   │   │   • Title: "HabitMate"
   │   │   • Text: Random motivational message
   │   │   • PendingIntent: Open MainActivity when tapped
   │   │
   │   └─→ NotificationManager.notify() → Shows notification
   │
   └─→ Reschedule for tomorrow (same time)
       │
       └─→ NotificationReceiver.scheduleNotification(context, hour, minute)
           │
           └─→ Loop back to step 2
```

### Kode Penting

**Scheduling Notification:**
```kotlin
// NotificationReceiver.kt
companion object {
    fun scheduleNotification(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            REQUEST_CODE, 
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Calculate trigger time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If time already passed, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        // Set exact alarm (reliable even in Doze mode)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
```

**Showing Notification:**
```kotlin
// NotificationHelper.kt
fun showReminderNotification(context: Context) {
    // Permission check for Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, 
            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return  // Skip if no permission
        }
    }
    
    // Create intent to open app when notification tapped
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(...)
    
    // Build and show notification
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("HabitMate")
        .setContentText(messages.random())  // Random motivational message
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
    
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
}
```

---

## 🔥 6. Firebase Integration

**File:** [FirestoreRepository.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/data/repository/FirestoreRepository.kt)

### Struktur Data di Firestore

```
Firestore Database
│
└── users (collection)
    │
    └── {userId} (document) ← Anonymous Auth ID
        │
        ├── habits (subcollection)
        │   │
        │   ├── {habitId1} (document)
        │   │   ├── id: "abc123"
        │   │   ├── title: "Drink Water"
        │   │   ├── emoji: "💧"
        │   │   ├── timeOfDay: "MORNING"
        │   │   ├── target: 8
        │   │   ├── current: 0
        │   │   ├── streak: 5
        │   │   ├── selectedDays: [true, true, true, true, true, true, true]
        │   │   └── createdDate: 19750
        │   │
        │   └── {habitId2} (document)
        │       └── ...
        │
        └── history (subcollection)
            │
            ├── {historyId1} (document)
            │   ├── habitId: "abc123"
            │   ├── date: 19756  (epoch day)
            │   ├── currentProgress: 8
            │   └── isDone: true
            │
            └── {historyId2} (document)
                └── ...
```

### Alur Data: Menyimpan dan Menampilkan

```
┌─────────────────────────────────────────────────────────────────┐
│                    ALUR FIREBASE DI HABITMATE                    │
└─────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════
                        1. AUTHENTICATION
═══════════════════════════════════════════════════════════════════

App Start (HabitMateApplication.onCreate())
    │
    └─→ repository.ensureAuthenticated()
        │
        ├─→ auth.currentUser == null?
        │   ├─→ YES: auth.signInAnonymously().await()
        │   │        → Firebase generates unique userId
        │   └─→ NO: Use existing userId
        │
        └─→ userId tersimpan di auth.currentUser.uid
            → Digunakan untuk path collection: /users/{userId}/habits

═══════════════════════════════════════════════════════════════════
                     2. MEMBACA DATA (REALTIME)
═══════════════════════════════════════════════════════════════════

HomeScreen dibuka
    │
    └─→ viewModel.uiState.collectAsState()
        │
        └─→ HomeViewModel observes repository.allHabits (Flow)
            │
            └─→ FirestoreRepository.allHabits:
                │
                └─→ callbackFlow {
                        habitsCollection.addSnapshotListener { snapshot, error ->
                            // Setiap ada perubahan di Firestore, callback dipanggil
                            val habits = snapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(Habit::class.java)
                            }
                            trySend(habits)  // Emit ke Flow
                        }
                    }
                    │
                    └─→ Data mengalir ke HomeViewModel → uiState → UI

═══════════════════════════════════════════════════════════════════
                     3. MENAMBAH HABIT BARU
═══════════════════════════════════════════════════════════════════

User tap "Create Habit" → isi form → tap "Save"
    │
    └─→ viewModel.addHabit(title, emoji, ...)
        │
        └─→ viewModelScope.launch {
                repository.insertHabit(Habit(...))
            }
            │
            └─→ FirestoreRepository.insertHabit():
                │
                ├─→ val docRef = habitsCollection.document()  // Auto-generate ID
                │
                ├─→ val habitWithId = habit.copy(id = docRef.id)
                │
                └─→ docRef.set(habitWithId).await()  // Save to Firestore
                    │
                    └─→ Firestore triggers SnapshotListener
                        │
                        └─→ UI otomatis update via Flow

═══════════════════════════════════════════════════════════════════
                     4. UPDATE PROGRESS HABIT
═══════════════════════════════════════════════════════════════════

User tap "+" di habit card
    │
    └─→ viewModel.toggleHabit(habitId)
        │
        └─→ viewModelScope.launch {
                // 1. Update history untuk hari ini
                repository.updateHabitProgress(
                    habitId = "abc123",
                    date = today.toEpochDay(),  // 19756
                    progress = 3,                // current + 1
                    isDone = false               // belum mencapai target
                )
                
                // 2. Recalculate streak
                repository.refreshStreak("abc123")
            }
            │
            └─→ FirestoreRepository.updateHabitProgress():
                │
                ├─→ Query existing history:
                │   historyCollection
                │       .whereEqualTo("habitId", habitId)
                │       .whereEqualTo("date", date)
                │       .get().await()
                │
                ├─→ If exists: Update document
                │   doc.reference.update(
                │       "currentProgress", progress,
                │       "isDone", isDone
                │   )
                │
                └─→ If not exists: Create new document
                    historyCollection.add(HabitHistory(...))

═══════════════════════════════════════════════════════════════════
                        5. DELETE HABIT
═══════════════════════════════════════════════════════════════════

User tap 🗑️ → Confirm delete
    │
    └─→ viewModel.deleteHabit(habit)
        │
        └─→ viewModelScope.launch {
                repository.deleteHabit(habitId)
            }
            │
            └─→ FirestoreRepository.deleteHabit():
                │
                ├─→ 1. Delete habit document:
                │   habitsCollection.document(habitId).delete().await()
                │
                └─→ 2. Delete all related history:
                    val historyDocs = historyCollection
                        .whereEqualTo("habitId", habitId)
                        .get().await()
                    
                    for (doc in historyDocs.documents) {
                        doc.reference.delete().await()
                    }
```

### Kode Penting

**Realtime Listener dengan Flow:**
```kotlin
// FirestoreRepository.kt
val allHabits: Flow<List<Habit>> = callbackFlow {
    val listenerRegistration = habitsCollection
        .addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            
            val habits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Habit::class.java)
            }
            trySend(habits)  // Emit data baru ke Flow
        }
    
    // Cleanup saat Flow tidak diobserve
    awaitClose { listenerRegistration.remove() }
}
```

**Refresh Streak Logic:**
```kotlin
// FirestoreRepository.kt
suspend fun refreshStreak(habitId: String) {
    // Get habit data
    val habitDoc = habitsCollection.document(habitId).get().await()
    val habit = habitDoc.toObject(Habit::class.java) ?: return
    
    // Get all history sorted by date
    val historyDocs = historyCollection
        .whereEqualTo("habitId", habitId)
        .get().await()
    
    // Find last completion
    val completions = historyDocs.documents
        .mapNotNull { it.toObject(HabitHistory::class.java) }
        .filter { it.isDone }
        .map { it.date }
        .sortedDescending()
    
    // Check if streak is broken
    val today = LocalDate.now().toEpochDay()
    var streak = 0
    
    for (date in completions) {
        if (date == today - streak) {
            streak++
        } else {
            break  // Gap found, streak ends
        }
    }
    
    // Update streak in Firestore
    updateStreak(habitId, streak)
}
```

---

## 📋 Ringkasan Alur Data

| Screen | Sumber Data | Cara Display |
|--------|-------------|--------------|
| **HomeScreen** | `viewModel.uiState` (filtered by date) | LazyColumn dengan OceanHabitCard |
| **HabitDetailScreen** | `HabitUi` object dari HomeScreen | Direct display dengan StatItem |
| **HabitsManagerScreen** | `viewModel.allHabits` (semua habits) | LazyColumn dengan HabitListItem |
| **StatsScreen** | `List<HabitUi>` dari HomeScreen | Computed stats + WeeklyBarChart |
| **Notification** | SharedPreferences | AlarmManager → BroadcastReceiver |
| **Firebase** | Firestore realtime listener | Flow → StateFlow → collectAsState |

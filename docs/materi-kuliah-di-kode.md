# Implementasi Materi Kuliah Kotlin di Aplikasi HabitMate

Dokumen ini menjelaskan bagaimana materi kuliah Kotlin/Android yang sudah diajarkan dosen diterapkan di aplikasi HabitMate.

---

## 📚 Materi 02-04: Kotlin Basics, OOP, dan Collections

### 1. Conditional Statements, Looping, and Functions
**File:** [FirestoreRepository.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/data/repository/FirestoreRepository.kt)

**Penjelasan:**
Kode di bawah ini digunakan untuk mengecek apakah streak (hari berturut-turut) user sudah terputus atau belum. Logikanya: looping dari hari terakhir selesai sampai hari ini, lalu cek apakah ada hari yang seharusnya dijadwalkan tapi tidak dikerjakan. Jika ada, maka `isStreakBroken = true` dan streak di-reset ke 0.

```kotlin
// Looping untuk cek setiap hari antara lastCompletion dan today
var isStreakBroken = false
for (d in (lastCompletion + 1) until today) {
    val dateObj = java.time.LocalDate.ofEpochDay(d)
    val dayOfWeek = dateObj.dayOfWeek.value % 7
    
    // Conditional: cek apakah hari ini seharusnya dijadwalkan
    if (selectedDays[dayOfWeek]) {
        isStreakBroken = true    // Ketemu hari yang dilewatkan!
        break                    // Keluar dari loop, tidak perlu cek lagi
    }
}

// Conditional branch: ambil keputusan berdasarkan hasil pengecekan
if (isStreakBroken) {
    updateStreak(habitId, 0)     // Reset streak ke 0
    return                       // Keluar dari function
}
```

---

**File:** [HomeViewModel.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeViewModel.kt)

**Penjelasan:**
Function `mapHabitToUi` ini bertugas mengubah data mentah dari database menjadi format yang siap ditampilkan di UI. Di dalamnya ada looping untuk menghitung berapa hari yang dijadwalkan dalam 7 hari terakhir, lalu menghitung success rate (persentase keberhasilan).

```kotlin
// Function dengan parameter dan return type
private fun mapHabitToUi(
    habit: HabitUi,
    dailyHistory: List<HabitHistory>,
    allHistory: List<HabitHistory>,
    referenceDate: LocalDate
): HabitUi {
    
    // Looping: hitung jumlah hari terjadwal dalam 7 hari terakhir
    var scheduledDaysLast7 = 0
    for (day in sevenDaysAgo..todayEpoch) {
        val dateObj = java.time.LocalDate.ofEpochDay(day)
        val dayOfWeek = dateObj.dayOfWeek.value % 7
        
        // Conditional: cek apakah hari ini termasuk jadwal
        if (habit.selectedDays.getOrElse(dayOfWeek) { true }) {
            scheduledDaysLast7++
        }
    }
    
    // Return: kembalikan habit dengan data yang sudah diolah
    return habit.copy(
        current = dailyRecord?.currentProgress ?: 0,
        successRate = successRate
    )
}
```

---

### 2. Kotlin OOP (Object-Oriented Programming)
**File:** [Habit.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/data/model/Habit.kt)

**Penjelasan:**
`data class` adalah fitur Kotlin untuk membuat class yang fokus menyimpan data. Kotlin otomatis generate `equals()`, `hashCode()`, `toString()`, dan `copy()` untuk kita. Class `Habit` ini merepresentasikan satu kebiasaan yang disimpan di Firestore.

```kotlin
// Data Class: class khusus untuk menyimpan data
data class Habit(
    @DocumentId val id: String = "",         // Annotation dari Firestore
    val title: String = "",                  // Property dengan default value
    val emoji: String = "",
    val timeOfDay: String = HabitTimeOfDay.ANYTIME.name,
    val current: Int = 0,                    // Immutable property (val)
    val target: Int = 1,
    var isDoneToday: Boolean = false,        // Mutable property (var)
    val streak: Int = 0,
    val selectedDays: List<Boolean> = List(7) { true }  // List sebagai property
) {
    // Secondary Constructor: constructor tambahan untuk Firestore
    constructor() : this(id = "")
    
    // Method: function di dalam class
    fun toTimeOfDay(): HabitTimeOfDay {
        return try {
            HabitTimeOfDay.valueOf(timeOfDay)    // Convert String ke Enum
        } catch (e: Exception) {
            HabitTimeOfDay.ANYTIME               // Fallback jika error
        }
    }
}
```

---

**File:** [HomeScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeScreen.kt)

**Penjelasan:**
`enum class` digunakan untuk mendefinisikan sekumpulan nilai konstan yang terbatas. Di sini kita punya `HabitTimeOfDay` untuk waktu pelaksanaan habit (pagi, siang, malam, atau kapan saja).

```kotlin
// Enum Class: mendefinisikan pilihan yang terbatas
enum class HabitTimeOfDay {
    ANYTIME,      // Bisa kapan saja
    MORNING,      // Pagi
    AFTERNOON,    // Siang
    EVENING       // Malam
}

// Data Class untuk UI Model (berbeda dari model database)
data class HabitUi(
    val id: String,
    val title: String,
    val emoji: String,
    val timeOfDay: HabitTimeOfDay,           // Menggunakan enum
    val current: Int,
    val target: Int,
    val isDoneToday: Boolean,
    val streak: Int = 0,                     // Default value
    val selectedDays: List<Boolean> = List(7) { true },
    val totalCompletions: Int = 0,           // Computed field untuk stats
    val successRate: Int = 0,
    val recentHistory: List<Boolean> = emptyList()
)
```

---

### 3. Collections di Kotlin
**File:** [HomeViewModel.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeViewModel.kt)

**Penjelasan:**
Kotlin punya banyak function untuk memanipulasi collection seperti `map`, `filter`, `associate`, dll. Ini membuat kode lebih ringkas dan mudah dibaca dibanding looping manual.

```kotlin
// MAP: transform setiap item dalam list menjadi bentuk lain
repository.allHabits
    .map { entities ->                       
        entities.map { entity ->             // Nested map
            HabitUi(                          // Transform HabitEntity -> HabitUi
                id = entity.id,
                title = entity.title,
                emoji = entity.emoji
            )
        }
    }

// FILTER: ambil hanya item yang memenuhi kondisi
habits
    .filter { it.selectedDays[dayOfWeek] || it.weeklyTarget > 0 }
    
// ASSOCIATE: buat Map dari List
// Hasil: Map<Long, Boolean> -> tanggal ke status selesai/tidak
val historyMap = allHistory
    .filter { it.habitId == habit.id }       // Filter dulu
    .associate { it.date to it.isDone }      // Lalu buat map
```

---

**File:** [NotificationHelper.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/notification/NotificationHelper.kt)

**Penjelasan:**
`listOf()` membuat immutable list (tidak bisa diubah setelah dibuat). `random()` mengambil elemen acak dari list.

```kotlin
// LISTOF: membuat immutable list
private val messages = listOf(
    "Time to check your habits! 🎯",
    "Don't forget to complete your habits today! 💪",
    "Your habits are waiting for you! ✨",
    "Stay consistent - check your habits! 🔥",
    "Build your streak today! 🚀"
)

// RANDOM: ambil satu elemen secara acak
val message = messages.random()
```

---

## 📱 Materi 08-11: UI Design dan Layout

### 4. Designing User Interface (Jetpack Compose)
**File:** [HomeScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeScreen.kt)

**Penjelasan:**
Jetpack Compose menggunakan `@Composable` function untuk membangun UI. State dikelola dengan `remember` dan `mutableStateOf`. Saat state berubah, UI otomatis di-render ulang (recomposition).

```kotlin
@Composable
fun HabitMateHomeScreen(
    onNavigateToCreateHabit: (String?, String?) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    // REMEMBER: simpan state yang survive recomposition
    val today = remember { LocalDate.now() }
    
    // MUTABLESTATEOF: state yang bisa berubah dan trigger recomposition
    var selectedDate by remember { mutableStateOf(today) }
    var showAddSheet by remember { mutableStateOf(false) }
    
    // COLLECTASSTATE: observe Flow/StateFlow dari ViewModel
    val habits by viewModel.uiState.collectAsState()
    
    // SCAFFOLD: layout dasar dengan topBar, bottomBar, FAB
    Scaffold(
        topBar = { ModernTopBar(...) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateHabit(null, null) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        bottomBar = { BottomNavBar(...) }
    ) { innerPadding ->
        // WHEN: conditional rendering berdasarkan destination
        when (currentDestination) {
            HabitMateDestination.HOME -> TodayContent(...)
            HabitMateDestination.STATS -> StatsScreen(...)
            HabitMateDestination.HABITS -> HabitsManagerScreen(...)
        }
    }
}
```

---

### 5. UI Design Patterns: Reusable Components
**File:** [SettingsScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/settings/SettingsScreen.kt)

**Penjelasan:**
Buat komponen yang bisa dipakai ulang dengan parameter. `content: @Composable ColumnScope.() -> Unit` adalah "slot" yang bisa diisi dengan konten apapun - ini disebut **higher-order function**.

```kotlin
// Reusable Component dengan content slot
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit   // Slot untuk isi section
) {
    Column {
        // Title section
        Text(text = title, fontWeight = FontWeight.SemiBold)
        
        // Card berisi content yang dikirim dari luar
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(content = content)            // Render content di sini
        }
    }
}

// Cara pakai: tinggal panggil dan isi content-nya
SettingsSection(title = "Notifications") {
    // Ini adalah "content" yang masuk ke slot
    SettingsToggleItem(
        icon = Icons.Default.Notifications,
        title = "Daily Reminder",
        checked = notificationEnabled,
        onCheckedChange = { ... }
    )
}
```

---

### 6. List and Grid (LazyColumn & LazyRow)
**File:** [HomeScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeScreen.kt)

**Penjelasan:**
`LazyColumn` dan `LazyRow` hanya render item yang terlihat di layar (lazy loading). Ini penting untuk performa saat list panjang. Berbeda dengan `Column` biasa yang render semua item sekaligus.

```kotlin
// LAZYCOLUMN: vertical scrolling list dengan lazy loading
LazyColumn(
    modifier = modifier.background(BackgroundColor),
    contentPadding = PaddingValues(bottom = 100.dp)  // Space untuk FAB
) {
    // ITEM: untuk single item (bukan dari list)
    item {
        ModernDateStrip(...)
        Spacer(modifier = Modifier.height(24.dp))
    }
    
    item {
        GradientProgressCard(progress = progressToday)
    }
    
    // ITEMS: untuk list of items
    HabitList(habits = habits, ...)    // Di dalamnya pakai items()
}

// LAZYROW: horizontal scrolling dengan lazy loading
LazyRow(
    state = listState,                              // State untuk scroll position
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(dates) { date ->                          // Loop setiap tanggal
        Surface(
            modifier = Modifier.clickable { onDateSelected(date) }
        ) {
            Column {
                Text(dayName)
                Text(dayNum)
            }
        }
    }
}
```

---

**File:** [CreateHabitScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/CreateHabitScreen.kt)

**Penjelasan:**
LazyRow cocok untuk horizontal scroll seperti emoji picker. Setiap emoji jadi item yang bisa diklik.

```kotlin
// LazyRow untuk emoji selection horizontal
LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    items(emojis) { emoji ->                     // Loop setiap emoji
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (selectedEmoji == emoji) PrimaryColor  // Highlight selected
                    else CardSurface
                )
                .clickable { selectedEmoji = emoji },         // Update state on click
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
    }
}
```

---

## 🧭 Materi 12-14: Navigation

### 7. Basic Navigation
**File:** [MainActivity.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/MainActivity.kt)

**Penjelasan:**
`NavHost` adalah container untuk semua screen. `startDestination` menentukan screen pertama yang muncul. Setiap `composable()` mendefinisikan satu route/halaman.

```kotlin
// Buat NavController untuk mengatur navigasi
val navController = rememberNavController()

// NavHost: container untuk semua screens
NavHost(
    navController = navController,
    startDestination = "splash"              // Screen pertama
) {
    // Route "splash" -> SplashScreen
    composable("splash") {
        SplashScreen(
            onNavigateToHome = {
                navController.navigate("home") {
                    // popUpTo: hapus splash dari back stack
                    // Jadi user tidak bisa back ke splash
                    popUpTo("splash") { inclusive = true }
                }
            }
        )
    }
    
    // Route "home" -> HomeScreen
    composable("home") {
        HabitMateHomeScreen(...)
    }
}
```

---

### 8. Navigation with Arguments
**File:** [MainActivity.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/MainActivity.kt)

**Penjelasan:**
Kita bisa kirim data antar screen dengan arguments di route. Format: `"route?arg1={arg1}&arg2={arg2}"`. Di destination, ambil argument dari `backStackEntry.arguments`.

```kotlin
// Route dengan multiple arguments (optional)
composable(
    "create_habit?habitId={habitId}&title={title}&emoji={emoji}"
) { backStackEntry ->
    // EXTRACT ARGUMENTS dari navigation
    val habitId = backStackEntry.arguments?.getString("habitId")
    val initialTitle = backStackEntry.arguments?.getString("title") ?: ""
    val initialEmoji = backStackEntry.arguments?.getString("emoji") ?: "💧"
    
    CreateHabitScreen(
        initialTitle = initialTitle,
        initialEmoji = initialEmoji,
        habitToEdit = if (habitId != null) findHabit(habitId) else null
    )
}

// NAVIGATE dengan arguments
val route = if (title != null && emoji != null) {
    "create_habit?title=$title&emoji=$emoji"    // Dengan argument
} else {
    "create_habit"                               // Tanpa argument
}
navController.navigate(route)

// Navigate untuk edit (kirim habitId)
onNavigateToEdit = { habitId ->
    navController.navigate("create_habit?habitId=$habitId")
}
```

---

### 9. Advanced Navigation Pattern (Animated Transitions)
**File:** [MainActivity.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/MainActivity.kt)

**Penjelasan:**
Setiap `composable()` bisa punya animasi enter/exit. `slideIntoContainer` untuk slide masuk, `slideOutOfContainer` untuk slide keluar. `popExitTransition` khusus untuk animasi saat user tekan back.

```kotlin
composable(
    "habits_manager",
    // Animasi saat masuk: slide dari kanan ke kiri
    enterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            tween(400)                           // Durasi 400ms
        )
    },
    // Animasi saat keluar: slide ke kanan
    exitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            tween(400)
        )
    }
) {
    HabitsManagerScreen(...)
}

// Settings dengan animasi khusus untuk back button
composable(
    "settings",
    enterTransition = { slideIntoContainer(...) },
    exitTransition = { fadeOut(animationSpec = tween(200)) },    // Fade out biasa
    // Animasi khusus saat user tekan back
    popExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            tween(400)
        )
    }
) {
    SettingsScreen(...)
}
```

---

## 🏗️ Materi 15: MVVM Architecture

### 10. Model-View-ViewModel Pattern
**File:** [HomeViewModel.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeViewModel.kt)

**Penjelasan:**
MVVM memisahkan kode menjadi 3 layer:
- **Model**: Data dan business logic (`Habit`, `FirestoreRepository`)
- **View**: UI yang menampilkan data (`HomeScreen`)
- **ViewModel**: Penghubung yang menyiapkan data untuk View (`HomeViewModel`)

ViewModel meng-expose data via `StateFlow` yang di-observe oleh View.

```kotlin
// ViewModel: penghubung antara UI dan Data
class HomeViewModel(
    private val repository: FirestoreRepository    // Dependency Injection
) : ViewModel() {

    // StateFlow: data yang di-observe oleh UI
    // Ketika data berubah, UI otomatis update
    val uiState: StateFlow<List<HabitUi>> = 
        combine(allHabits, _selectedDate, repository.allHistory) { ... }
            .stateIn(
                scope = viewModelScope,            // Lifecycle-aware
                started = WhileSubscribed(5000),   // Aktif selama ada subscriber
                initialValue = emptyList()
            )
    
    // Business Logic Methods - dipanggil oleh UI
    fun addHabit(...) { viewModelScope.launch { repository.insertHabit(...) } }
    fun updateHabit(...) { viewModelScope.launch { repository.updateHabit(...) } }
    fun toggleHabit(id: String) { ... }
    fun deleteHabit(habit: HabitUi) { ... }
    
    // Factory untuk create ViewModel dengan dependency
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(...): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return HomeViewModel(
                    (application as HabitMateApplication).repository
                ) as T
            }
        }
    }
}
```

**Cara pakai di View (HomeScreen):**
```kotlin
@Composable
fun HabitMateHomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    // Observe StateFlow sebagai Compose State
    val habits by viewModel.uiState.collectAsState()
    
    // Panggil ViewModel method saat ada user action
    onToggleHabit = { habitId -> viewModel.toggleHabit(habitId) }
}
```

---

## ⏱️ Materi 16: Background Task (Coroutines)

### 11. Coroutines di Android
**File:** [HomeViewModel.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/home/HomeViewModel.kt)

**Penjelasan:**
Coroutines memungkinkan kita menjalankan operasi async (seperti akses database) tanpa blocking UI thread. `viewModelScope.launch` membuat coroutine yang otomatis dibatalkan saat ViewModel destroyed.

```kotlin
// viewModelScope.launch: jalankan coroutine di background
fun addHabit(title: String, emoji: String, ...) {
    viewModelScope.launch {                      // Tidak blocking UI
        repository.insertHabit(                  // Operasi database (suspend)
            Habit(title = title, emoji = emoji, ...)
        )
    }
    // Kode di sini langsung jalan, tidak menunggu insert selesai
}

fun toggleHabit(id: String) {
    val habit = uiState.value.find { it.id == id } ?: return
    
    viewModelScope.launch {
        val newProgress = if (habit.current < habit.target) 
            habit.current + 1 else habit.current
        val isDone = newProgress >= habit.target
        
        // Dua operasi async berurutan
        repository.updateHabitProgress(habit.id, date, newProgress, isDone)
        repository.refreshStreak(habit.id)       // Tunggu yang atas selesai
    }
}
```

---

**File:** [FirestoreRepository.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/data/repository/FirestoreRepository.kt)

**Penjelasan:**
`suspend` function hanya bisa dipanggil dari coroutine. `.await()` mengubah Firestore Task menjadi suspend function. `Flow` dengan `callbackFlow` membungkus callback-based API menjadi reactive stream.

```kotlin
// SUSPEND FUNCTION: harus dipanggil dari coroutine
suspend fun insertHabit(habit: Habit): String {
    val docRef = habitsCollection.document()
    val habitWithId = habit.copy(id = docRef.id)
    docRef.set(habitWithId).await()              // await = tunggu selesai
    return docRef.id
}

// FLOW dengan callbackFlow: convert callback ke Flow
val allHabits: Flow<List<Habit>> = callbackFlow {
    // Setup listener (callback-based)
    val listener = habitsCollection.addSnapshotListener { snapshot, error ->
        val habits = snapshot?.documents?.mapNotNull { ... }
        trySend(habits)                          // Emit ke Flow
    }
    
    // Cleanup saat Flow tidak diobserve lagi
    awaitClose { listener.remove() }
}
```

---

**File:** [HabitMateApplication.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/HabitMateApplication.kt)

**Penjelasan:**
`CoroutineScope` dengan `SupervisorJob` memungkinkan child coroutine gagal tanpa membatalkan parent. Cocok untuk app-level initialization.

```kotlin
class HabitMateApplication : Application() {
    // Scope untuk app-level coroutines
    private val applicationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main       // SupervisorJob = failure isolation
    )

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        // Launch coroutine saat app start
        applicationScope.launch {
            val success = repository.ensureAuthenticated()
            Log.d("HabitMateApp", "Auth result: $success")
        }
    }
}
```

---

## 📊 Materi 17: Processing Data and Notification

### 12. Notification Implementation
**File:** [NotificationHelper.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/notification/NotificationHelper.kt)

**Penjelasan:**
Android 8+ memerlukan NotificationChannel. `NotificationCompat.Builder` untuk membangun notifikasi. `PendingIntent` untuk menentukan apa yang terjadi saat notifikasi diklik.

```kotlin
// Object: Singleton pattern di Kotlin
object NotificationHelper {
    
    private const val CHANNEL_ID = "habit_reminder_channel"
    private const val NOTIFICATION_ID = 1001
    
    // Buat Notification Channel (wajib untuk Android 8+)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to complete your habits"
            }
            
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    fun showReminderNotification(context: Context) {
        // PendingIntent: apa yang terjadi saat notif diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build notifikasi
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("HabitMate")
            .setContentText(messages.random())         // Pesan random
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)           // Action saat diklik
            .setAutoCancel(true)                       // Hilang setelah diklik
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
```

---

**File:** [NotificationReceiver.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/notification/NotificationReceiver.kt)

**Penjelasan:**
`BroadcastReceiver` menerima event dari sistem (seperti alarm). `AlarmManager` untuk menjadwalkan notifikasi di waktu tertentu. Setelah notifikasi tampil, langsung jadwalkan untuk besok (daily reminder).

```kotlin
// BroadcastReceiver: terima event dari AlarmManager
class NotificationReceiver : BroadcastReceiver() {
    
    // Dipanggil saat alarm berbunyi
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showReminderNotification(context)
        
        // Jadwalkan untuk besok (daily reminder)
        val prefs = context.getSharedPreferences("habit_settings", Context.MODE_PRIVATE)
        val hour = prefs.getInt("notification_hour", 9)
        val minute = prefs.getInt("notification_minute", 0)
        scheduleNotification(context, hour, minute)
    }
    
    companion object {
        // Schedule notifikasi pakai AlarmManager
        fun scheduleNotification(context: Context, hour: Int, minute: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(...)
            
            // Set waktu dengan Calendar
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                
                // Jika waktu sudah lewat hari ini, jadwalkan besok
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            
            // setExactAndAllowWhileIdle: alarm tepat waktu meskipun device idle
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,             // Bangunkan device jika perlu
                calendar.timeInMillis,
                pendingIntent
            )
        }
        
        fun cancelNotification(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)       // Batalkan alarm
        }
    }
}
```

---

## 🌐 Materi 18: Integration with External Databases and APIs

### 13. Firebase Firestore Integration
**File:** [FirestoreRepository.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/data/repository/FirestoreRepository.kt)

**Penjelasan:**
Firestore adalah cloud database NoSQL dari Firebase. Data disimpan dalam struktur: `/users/{userId}/habits/{habitId}`. Anonymous Auth memberi setiap device user ID unik tanpa perlu login.

```kotlin
class FirestoreRepository {
    
    // Initialize Firebase instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // Dynamic collection reference berdasarkan user ID
    private val habitsCollection
        get() = db.collection("users")
                  .document(userId)
                  .collection("habits")
    
    private val historyCollection
        get() = db.collection("users")
                  .document(userId)
                  .collection("history")
    
    // Anonymous Authentication (user tidak perlu login manual)
    suspend fun ensureAuthenticated(): Boolean {
        return try {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()     // Sign in otomatis
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // CRUD Operations
    suspend fun insertHabit(habit: Habit): String {
        val docRef = habitsCollection.document()     // Auto-generate ID
        val habitWithId = habit.copy(id = docRef.id)
        docRef.set(habitWithId).await()              // Simpan ke Firestore
        return docRef.id
    }
    
    suspend fun updateHabit(habit: Habit) {
        habitsCollection.document(habit.id)
            .set(habit).await()                      // Overwrite dokumen
    }
    
    suspend fun deleteHabit(habitId: String) {
        // Hapus habit
        habitsCollection.document(habitId).delete().await()
        
        // Hapus semua history terkait
        val historyDocs = historyCollection
            .whereEqualTo("habitId", habitId)        // Query
            .get().await()
        
        for (doc in historyDocs.documents) {
            doc.reference.delete().await()
        }
    }
}
```

---

## 🔐 Materi 20: Handling Permissions and Device Services

### 14. Runtime Permission Handling
**File:** [SettingsScreen.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/ui/settings/SettingsScreen.kt)

**Penjelasan:**
Android 13+ memerlukan runtime permission untuk notifikasi. Alur: cek permission → jika belum ada, minta permission → handle hasil (granted/denied).

```kotlin
@Composable
fun SettingsScreen(...) {
    // Permission Launcher: handle hasil request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // User izinkan → aktifkan notifikasi
            notificationEnabled = true
            prefs.edit().putBoolean("notification_enabled", true).apply()
            NotificationReceiver.scheduleNotification(context, hour, minute)
        }
        // Jika denied, tidak perlu handle khusus
    }

    fun enableNotifications() {
        // Cek Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
            // Cek apakah sudah punya permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Sudah punya permission → langsung aktifkan
                notificationEnabled = true
                NotificationReceiver.scheduleNotification(...)
            } else {
                // Belum punya → minta permission
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android < 13: tidak perlu runtime permission
            notificationEnabled = true
            NotificationReceiver.scheduleNotification(...)
        }
    }
}
```

---

**File:** [NotificationHelper.kt](/AndroidStudioProjects/habitmate/app/src/main/java/com/example/habitmate/notification/NotificationHelper.kt)

**Penjelasan:**
Sebelum tampilkan notifikasi, selalu cek permission dulu. Jika tidak punya permission, skip saja (return early).

```kotlin
fun showReminderNotification(context: Context) {
    // Guard clause: cek permission untuk Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return   // Tidak punya permission, skip
        }
    }
    
    // Lanjut tampilkan notifikasi...
}
```

---

**File:** [AndroidManifest.xml](/AndroidStudioProjects/habitmate/app/src/main/AndroidManifest.xml)

**Penjelasan:**
Semua permission harus dideklarasikan di manifest. Untuk Android 13+, `POST_NOTIFICATIONS` juga perlu diminta saat runtime.

```xml
<!-- Deklarasi Permission di Manifest -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

---

## 📋 Ringkasan Materi per File

| Materi | File Utama | Konsep yang Diterapkan |
|--------|------------|----------------------|
| **02-04** Kotlin Basic & OOP | `Habit.kt`, `HabitHistory.kt`, `HomeScreen.kt` | data class, enum, functions, loops, conditionals |
| **04** Collections | `HomeViewModel.kt`, `FirestoreRepository.kt` | List, Map, filter, map, associate |
| **08-10** UI Design | `HomeScreen.kt`, `SettingsScreen.kt`, `CreateHabitScreen.kt` | Composables, Material3, State management |
| **11** List & Grid | `HomeScreen.kt`, `CreateHabitScreen.kt` | LazyColumn, LazyRow |
| **12** Basic Navigation | `MainActivity.kt` | NavHost, composable routes |
| **13** Navigation Args | `MainActivity.kt` | Route parameters, backStackEntry |
| **14** Advanced Navigation | `MainActivity.kt` | Animated transitions, popExitTransition |
| **15** MVVM | `HomeViewModel.kt`, `FirestoreRepository.kt` | ViewModel, StateFlow, Repository pattern |
| **16** Background Task | `HomeViewModel.kt`, `HabitMateApplication.kt` | Coroutines, viewModelScope, suspend functions |
| **17** Notification | `NotificationHelper.kt`, `NotificationReceiver.kt` | NotificationChannel, AlarmManager, BroadcastReceiver |
| **18** External Database | `FirestoreRepository.kt` | Firebase Firestore, Firebase Auth |
| **20** Permissions | `SettingsScreen.kt`, `AndroidManifest.xml` | Runtime permissions, ActivityResultContracts |

---

## 📖 Tips Belajar

1. **Mulai dari Model** - Pahami `Habit.kt` dan `HabitHistory.kt` untuk memahami struktur data
2. **Lihat Repository** - `FirestoreRepository.kt` menunjukkan bagaimana data disimpan/diambil
3. **Pahami ViewModel** - `HomeViewModel.kt` adalah jantung logika bisnis
4. **Eksplorasi UI** - `HomeScreen.kt` menunjukkan cara compose UI dengan data
5. **Cek Navigation** - `MainActivity.kt` menunjukkan routing antar screen
6. **Pelajari Background** - `NotificationHelper.kt` dan `NotificationReceiver.kt` untuk scheduled tasks

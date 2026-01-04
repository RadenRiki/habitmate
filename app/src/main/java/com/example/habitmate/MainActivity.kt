package com.example.habitmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habitmate.ui.habits.HabitsManagerScreen
import com.example.habitmate.ui.home.CreateHabitScreen
import com.example.habitmate.ui.home.HabitMateHomeScreen
import com.example.habitmate.ui.home.HomeViewModel
import com.example.habitmate.ui.settings.AboutScreen
import com.example.habitmate.ui.settings.PrivacyScreen
import com.example.habitmate.ui.settings.SettingsScreen
import com.example.habitmate.ui.settings.TermsScreen
import com.example.habitmate.ui.splash.SplashScreen
import com.example.habitmate.ui.theme.HabitmateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitmateTheme {
                val navController = rememberNavController()
                val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                        )
                    }
                    composable(
                            "home",
                            enterTransition = { fadeIn(animationSpec = tween(700)) },
                            exitTransition = { fadeOut(animationSpec = tween(700)) }
                    ) {
                        HabitMateHomeScreen(
                                onNavigateToCreateHabit = { title, emoji ->
                                    val route =
                                            if (title != null && emoji != null) {
                                                "create_habit?title=$title&emoji=$emoji"
                                            } else {
                                                "create_habit"
                                            }
                                    navController.navigate(route)
                                },
                                onNavigateToHabitsManager = {
                                    navController.navigate("habits_manager")
                                },
                                onNavigateToEdit = { habitId ->
                                    navController.navigate("create_habit?habitId=$habitId")
                                },
                                onNavigateToSettings = { navController.navigate("settings") },
                                viewModel = viewModel
                        )
                    }

                    composable(
                            "habits_manager",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(400)
                                )
                            }
                    ) {
                        HabitsManagerScreen(
                                viewModel = viewModel,
                                onNavigateToEdit = { habitId ->
                                    navController.navigate("create_habit?habitId=$habitId")
                                }
                        )
                    }

                    composable(
                            "create_habit?habitId={habitId}&title={title}&emoji={emoji}",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(400)
                                )
                            }
                    ) { backStackEntry ->
                        val habitId = backStackEntry.arguments?.getString("habitId")
                        val initialTitle = backStackEntry.arguments?.getString("title") ?: ""
                        val initialEmoji = backStackEntry.arguments?.getString("emoji") ?: "💧"

                        // If finding existing habit for edit
                        val allHabits = viewModel.allHabits.collectAsState().value
                        val habitToEdit =
                                if (!habitId.isNullOrEmpty()) allHabits.find { it.id == habitId }
                                else null

                        CreateHabitScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { newHabit ->
                                    if (habitToEdit != null) {
                                        // UPDATE
                                        viewModel.updateHabit(
                                                newHabit.copy(
                                                        id = habitToEdit.id,
                                                        streak = habitToEdit.streak,
                                                        current = habitToEdit.current,
                                                        isDoneToday = habitToEdit.isDoneToday
                                                )
                                        )
                                    } else {
                                        // CREATE
                                        viewModel.addHabit(
                                                title = newHabit.title,
                                                emoji = newHabit.emoji,
                                                target = newHabit.target,
                                                unit = newHabit.unitLabel,
                                                timeOfDay = newHabit.timeOfDay,
                                                selectedDays = newHabit.selectedDays,
                                                weeklyTarget = newHabit.weeklyTarget
                                        )
                                    }
                                    navController.popBackStack()
                                },
                                initialTitle = habitToEdit?.title ?: initialTitle,
                                initialEmoji = habitToEdit?.emoji ?: initialEmoji,
                                habitToEdit = habitToEdit
                        )
                    }

                    // Settings Screen
                    composable(
                            "settings",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(400)
                                )
                            }
                    ) {
                        SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToTerms = { navController.navigate("terms") },
                                onNavigateToPrivacy = { navController.navigate("privacy") },
                                onNavigateToAbout = { navController.navigate("about") }
                        )
                    }

                    // Terms Screen
                    composable(
                            "terms",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(400)
                                )
                            }
                    ) { TermsScreen(onBack = { navController.popBackStack() }) }

                    // Privacy Screen
                    composable(
                            "privacy",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(400)
                                )
                            }
                    ) { PrivacyScreen(onBack = { navController.popBackStack() }) }

                    // About Screen
                    composable(
                            "about",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        tween(400)
                                )
                            }
                    ) { AboutScreen(onBack = { navController.popBackStack() }) }
                }
            }
        }
    }
}

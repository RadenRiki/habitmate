package com.example.habitmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habitmate.ui.home.CreateHabitScreen
import com.example.habitmate.ui.home.HabitMateHomeScreen
import com.example.habitmate.ui.splash.SplashScreen
import com.example.habitmate.ui.theme.HabitmateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitmateTheme {
                val navController = rememberNavController()
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
                                }
                        )
                    }
                    composable(
                            "create_habit?title={title}&emoji={emoji}",
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
                        val initialTitle = backStackEntry.arguments?.getString("title") ?: ""
                        val initialEmoji = backStackEntry.arguments?.getString("emoji") ?: "💧"

                        CreateHabitScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { newHabit ->
                                    // TODO: Save to ViewModel/Database
                                    navController.popBackStack()
                                },
                                initialTitle = initialTitle,
                                initialEmoji = initialEmoji
                        )
                    }
                }
            }
        }
    }
}

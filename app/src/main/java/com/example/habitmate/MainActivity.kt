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
                                onNavigateToCreateHabit = { navController.navigate("create_habit") }
                        )
                    }
                    composable(
                            "create_habit",
                            enterTransition = {
                                slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Left,
                                        animationSpec = tween(500)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Right,
                                        animationSpec = tween(500)
                                )
                            }
                    ) {
                        CreateHabitScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { newHabit ->
                                    // TODO: Persist habit (ViewModel)
                                    // For now just navigate back
                                    navController.popBackStack()
                                }
                        )
                    }
                }
            }
        }
    }
}

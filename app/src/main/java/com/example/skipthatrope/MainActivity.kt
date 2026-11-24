package com.example.skipthatrope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.skipthatrope.ui.theme.SkipThatRopeTheme

// The activities we have for now, might change in the next milestone depending on how the app looks in the finished version
enum class Screen { Login, Home, Workout }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Saves the user's info and last workout on the phone
        val storage = AppStorage(applicationContext)

        setContent {
            SkipThatRopeTheme {
                var screen by rememberSaveable { mutableStateOf(Screen.Login) }
                var userName by remember { mutableStateOf(storage.userName) }
                var weightKg by remember { mutableStateOf(storage.weightKg) }
                var lastWorkout by remember { mutableStateOf(storage.lastWorkout()) }

                // Back during a workout returns to Home (DISCARD WORKOUT)
                BackHandler(enabled = screen == Screen.Workout) {
                    screen = Screen.Home
                }

                when (screen) {
                    Screen.Login -> LoginScreen(
                        onEnterApp = { screen = Screen.Home },
                    )

                    Screen.Home -> HomeScreen(
                        userName = userName,
                        weightKg = weightKg,
                        lastWorkout = lastWorkout,
                        onSaveProfile = { name, weight ->
                            userName = name
                            weightKg = weight
                            storage.userName = name
                            storage.weightKg = weight
                        },
                        onStartWorkout = { screen = Screen.Workout },
                    )

                    Screen.Workout -> WorkoutScreen(
                        weightKg = weightKg,
                        onFinish = { result ->
                            storage.saveWorkout(result)
                            lastWorkout = result
                            screen = Screen.Home
                        },
                        onCancel = { screen = Screen.Home },
                    )
                }
            }
        }
    }
}
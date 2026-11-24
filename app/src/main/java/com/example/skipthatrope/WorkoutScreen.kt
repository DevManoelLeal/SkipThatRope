package com.example.skipthatrope


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WorkoutScreen(
    weightKg: Float,
    onFinish: (WorkoutResult) -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current

    var seconds by remember { mutableStateOf(0) }
    var jumps by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(true) }

    // The detector calls this every time it detects a jump
    val detector = remember { JumpDetector(context) { jumps++ } }
    val calories = estimateCalories(jumps, seconds, weightKg)

    // Keep the screen on so the workout isn't interrupted
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // Timer: ticks once per second while running
    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    // Sensors: listen only while the workout is running (saves battery, as per our class on the 17th of Nov)
    DisposableEffect(running) {
        if (running) detector.start()
        onDispose { detector.stop() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 8.dp)) {
            HamburgerMenu(listOf("Cancel workout" to onCancel))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Timer circle (also shows live jumps and calories)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(SlateGray, CircleShape)
                    // TESTING ONLY, NOT TO BE KEPT IN THE FINAL VERSION: long-press adds a jump.  ------->>>> ATTENTION MANOEL
                    .pointerInput(Unit) { detectTapGestures(onLongPress = { jumps++ }) },
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatTime(seconds), fontFamily = DisplayFont, fontSize = 52.sp, color = Color.Black)
                    Text("$jumps JUMPS", fontFamily = DisplayFont, fontSize = 22.sp, color = Color.Black)
                    Text("$calories KCAL", fontFamily = DisplayFont, fontSize = 22.sp, color = Color.Black)
                }
            }

            if (!detector.isAvailable) {
                Text(
                    text = "No accelerometer found on this device. Sorry about this buddy, maybe try again with your next phone.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
            }

            CircleButton(
                text = if (running) "PAUSE" else "RESUME",
                size = 140.dp,
                onClick = { running = !running },
            )

            CircleButton(
                text = "FINISH",
                size = 140.dp,
                onClick = {
                    running = false
                    onFinish(
                        WorkoutResult(
                            seconds = seconds,
                            calories = calories,
                            jumps = jumps,
                            timestamp = System.currentTimeMillis(),
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun CircleButton(text: String, size: Dp, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(size),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SlateGray, contentColor = Color.Black),
    ) {
        Text(text, fontFamily = DisplayFont, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun WorkoutScreenPreview() {
    WorkoutScreen(weightKg = 70f, onFinish = {}, onCancel = {})
}
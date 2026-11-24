package com.example.skipthatrope

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DateFormat
import java.util.Date

@Composable
fun HomeScreen(
    userName: String,
    weightKg: Float,
    lastWorkout: WorkoutResult?,
    onSaveProfile: (name: String, weightKg: Float) -> Unit,
    onStartWorkout: () -> Unit,
) {
    // With no login, ask for name and weight once, the first time the app opens
    var showProfileDialog by remember { mutableStateOf(userName.isBlank()) }
    var showMoreInfo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hamburger menu (top-left)
        Row(Modifier.fillMaxWidth().padding(start = 8.dp)) {
            HamburgerMenu(listOf("Edit profile" to { showProfileDialog = true }))
        }

        // Photo placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(SlateGray, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "USER’S PHOTO",
                fontFamily = DisplayFont,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = userName.ifBlank { "YOUR NAME" }.uppercase(),
            fontFamily = DisplayFont,
            fontSize = 40.sp,
            color = Color.Black,
        )

        Spacer(Modifier.height(32.dp))

        // Last workout summary
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "LAST WORKOUT:",
                fontFamily = DisplayFont,
                fontSize = 32.sp,
                color = Color.Black,
            )

            val rows = listOf(
                "TIME" to (lastWorkout?.let { formatTime(it.seconds) } ?: "--"),
                "CALORIES BURNT" to (lastWorkout?.let { "${it.calories} KCAL" } ?: "--"),
                "NUMBER OF JUMPS" to (lastWorkout?.jumps?.toString() ?: "--"),
            )
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.padding(start = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(10.dp).background(Color.Black, CircleShape))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "$label: $value",
                        fontFamily = DisplayFont,
                        fontSize = 24.sp,
                        color = Color.Black,
                    )
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { showMoreInfo = true },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateGray, contentColor = Color.Black),
                ) {
                    Text("MORE INFO", fontFamily = BodyFont)
                }
            }
        }

        // Push button towards the bottom
        Spacer(Modifier.weight(1f))

        Button(
            onClick = onStartWorkout,
            modifier = Modifier.size(width = 220.dp, height = 200.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SlateGray, contentColor = Color.Black),
        ) {
            Text(
                text = "START NEW WORKOUT",
                fontFamily = DisplayFont,
                fontSize = 34.sp,
                lineHeight = 36.sp,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    if (showProfileDialog) {
        ProfileDialog(
            initialName = userName,
            initialWeight = weightKg,
            onSave = { name, weight ->
                onSaveProfile(name, weight)
                showProfileDialog = false
            },
            onDismiss = { showProfileDialog = false },
        )
    }

    if (showMoreInfo) {
        MoreInfoDialog(
            lastWorkout = lastWorkout,
            weightKg = weightKg,
            onDismiss = { showMoreInfo = false },
        )
    }
}

@Composable
private fun ProfileDialog(
    initialName: String,
    initialWeight: Float,
    onSave: (String, Float) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var weight by remember {
        mutableStateOf(if (initialWeight % 1f == 0f) initialWeight.toInt().toString() else initialWeight.toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Text(
                    text = "Your weight is used to estimate the calories you burn.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedWeight = weight.replace(',', '.').toFloatOrNull() ?: initialWeight
                onSave(name.trim(), parsedWeight.coerceIn(20f, 250f))
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun MoreInfoDialog(
    lastWorkout: WorkoutResult?,
    weightKg: Float,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Last workout details") },
        text = {
            if (lastWorkout == null) {
                Text("No workouts yet. Start your first one!")
            } else {
                val pace = if (lastWorkout.seconds > 0) lastWorkout.jumps * 60f / lastWorkout.seconds else 0f
                val date = DateFormat.getDateTimeInstance().format(Date(lastWorkout.timestamp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Date: $date")
                    Text("Average pace: ${pace.toInt()} jumps per minute")
                    Text("Weight used for calories: ${weightKg.toInt()} kg")
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        userName = "Me",
        weightKg = 80f,
        lastWorkout = WorkoutResult(seconds = 323, calories = 52, jumps = 640, timestamp = 0L),
        onSaveProfile = { _, _ -> },
        onStartWorkout = {},
    )
}
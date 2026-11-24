package com.example.skipthatrope

import android.content.Context
import kotlin.math.roundToInt

/** One finished workout. */
data class WorkoutResult(
    val seconds: Int,
    val calories: Int,
    val jumps: Int,
    val timestamp: Long,
)

/**
 * Calorie estimate using the MET formula:  kcal = MET x weight(kg) x hours --> https://en.wikipedia.org/wiki/Metabolic_equivalent_of_task > Reference
 * The MET value depends on how fast the user skipped (jumps per minute),
 * using approximate values from the Compendium of Physical Activities: --> https://pacompendium.com/ > Reference
 *   under 100/min -> 8.3,  100-120/min -> 11.8,  over 120/min -> 12.3
 */
fun estimateCalories(jumps: Int, seconds: Int, weightKg: Float): Int {
    if (jumps == 0 || seconds == 0) return 0
    val jumpsPerMinute = jumps * 60f / seconds
    val met = when {
        jumpsPerMinute < 100f -> 8.8f
        jumpsPerMinute <= 120f -> 11.8f
        else -> 12.3f
    }
    return (met * weightKg * (seconds / 3600f)).roundToInt()
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

// Saves the user's info and last workout on the phone (no account needed for now, as explained in the documentation)
class AppStorage(context: Context) {

    private val prefs = context.getSharedPreferences("skip_that_rope", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString(KEY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var weightKg: Float
        get() = prefs.getFloat(KEY_WEIGHT, 70f)
        set(value) = prefs.edit().putFloat(KEY_WEIGHT, value).apply()

    fun saveWorkout(workout: WorkoutResult) {
        prefs.edit()
            .putInt(KEY_SECONDS, workout.seconds)
            .putInt(KEY_CALORIES, workout.calories)
            .putInt(KEY_JUMPS, workout.jumps)
            .putLong(KEY_TIMESTAMP, workout.timestamp)
            .apply()
    }

    fun lastWorkout(): WorkoutResult? {
        if (!prefs.contains(KEY_TIMESTAMP)) return null
        return WorkoutResult(
            seconds = prefs.getInt(KEY_SECONDS, 0),
            calories = prefs.getInt(KEY_CALORIES, 0),
            jumps = prefs.getInt(KEY_JUMPS, 0),
            timestamp = prefs.getLong(KEY_TIMESTAMP, 0L),
        )
    }

    private companion object {
        const val KEY_NAME = "user_name"
        const val KEY_WEIGHT = "weight_kg"
        const val KEY_SECONDS = "last_seconds"
        const val KEY_CALORIES = "last_calories"
        const val KEY_JUMPS = "last_jumps"
        const val KEY_TIMESTAMP = "last_timestamp"
    }
}

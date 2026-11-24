package com.example.skipthatrope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import kotlin.math.sqrt

/**
 * Counts jumps using the phone's sensors.
 *
 * Accelerometer: when the user lands, the total acceleration spikes well above
 * normal gravity (1 g). Each spike is counted as one jump.
 * Gyroscope: measures how fast the phone is rotating. If it is spinning fast
 * (the phone is being handled or waved around, not carried by a jumping body),
 * the spike is ignored. I need to prevent cheating or regular unwanted inputs.
 *
 * Works best with the phone in a pocket or strapped to the armband type of thingy, as originally mentioned to you on email.
 * The thresholds below are starting values, I need to test and change as required.
 */
class JumpDetector(
    context: Context,
    private val onJump: () -> Unit,
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    val isAvailable: Boolean get() = accelerometer != null

    private var smoothedG = 1f          // acceleration in g,  smoothed
    private var rotationRate = 0f       // rad/s from the gyroscope
    private var armed = true            // ready to detect the next landing
    private var lastJumpTime = 0L

    fun start() {
        smoothedG = 1f
        rotationRate = 0f
        armed = true
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val (x, y, z) = event.values.let { Triple(it[0], it[1], it[2]) }
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> rotationRate = sqrt(x * x + y * y + z * z)
            Sensor.TYPE_ACCELEROMETER -> {
                val g = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
                handleAcceleration(g)
            }
        }
    }

    private fun handleAcceleration(g: Float) {
        // Accelerometers can produce fluctuating readings due to vibrations, small movements and sensor noise.
        // Using every raw measurement directly, small fluctuations could cause the app to incorrectly identify a landing
        // So Exponential smoothing reduces these fluctuations by combining the latest measurement with the previous smoothed measurement.
        smoothedG = SMOOTHING * g + (1f - SMOOTHING) * smoothedG

        if (armed && smoothedG > LANDING_THRESHOLD_G) {
            val now = SystemClock.elapsedRealtime()
            val longEnoughSinceLast = now - lastJumpTime > MIN_JUMP_INTERVAL_MS
            val phoneNotSpinning = rotationRate < MAX_ROTATION_RAD_S
            if (longEnoughSinceLast && phoneNotSpinning) {
                lastJumpTime = now
                onJump()
            }
            armed = false // wait until the acceleration settles before the next jump
        } else if (!armed && smoothedG < RESET_THRESHOLD_G) {
            armed = true
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private companion object {
        const val SMOOTHING = 0.5f
        const val LANDING_THRESHOLD_G = 1.8f   // spike that counts as a landing
        const val RESET_THRESHOLD_G = 1.2f     // must drop below this to re-arm
        const val MIN_JUMP_INTERVAL_MS = 250L  // max ~240 jumps per minute
        const val MAX_ROTATION_RAD_S = 6f      // ignore spikes while spinning fast
    }
}
package com.example.throwme

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var speedDispay: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        speedDispay = findViewById(R.id.text_Counter)

        val rollButton: Button = findViewById(R.id.button)
        rollButton.setOnClickListener {
            rollButton.setText("WOO")
            countDownTimer(5000)
            val toast = Toast.makeText(this, "Waaiit...!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun doSensorMagic() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    //Timer Funktion
    private fun countDownTimer(milliSekUntilDone: Long) {
        val numberForCounter: TextView = findViewById(R.id.text_Counter)
        object : CountDownTimer(milliSekUntilDone, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                numberForCounter.text = (millisUntilFinished / 1000).toString()
            }

            // when the time is up
            override fun onFinish() {
                numberForCounter.text = "Thow!"
                doSensorMagic()
            }
        }.start()


    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val left_right_tilt = p0.values[0]
            val up_down_tilt = p0.values[1]

            speedDispay.apply {
                rotationX = up_down_tilt * 3f
                rotationY = left_right_tilt * 3f
                rotation = -left_right_tilt
                translationX = left_right_tilt * -10
                translationY = up_down_tilt * 10
            }

            val color =
                if (up_down_tilt.toInt() == 0 && left_right_tilt.toInt() == 0) Color.GREEN else Color.MAGENTA
            speedDispay.setTextColor(color)
            speedDispay.text = "X:${up_down_tilt.toInt()}\nY:${left_right_tilt.toInt()}"


        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

}

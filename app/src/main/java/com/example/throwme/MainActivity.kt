package com.example.throwme

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
//import android.widget.Toast -> Include for pop up mssges (Use for Disclaimer)
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var speedDispay: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //Setting Homescreen to MainActivity

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        speedDispay = findViewById(R.id.text_Counter)

        val rollButton: Button = findViewById(R.id.button)
        rollButton.setOnClickListener {
            rollButton.setBackgroundColor(Color.YELLOW)
            rollButton.setText("CountDown")
            //Counting Down and starting doSensorMagic() when done -> 4sek
            countDownTimer(4000)
        }
    }

    private fun doSensorMagic() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST, //Fastest Sensor Delay (Display Numbers need to be refreshed fast)
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    //Timer Funktion (3sec/+1 to compensate for loading times)
    private fun countDownTimer(milliSekUntilDone: Long) {
        val numberForCounter: TextView = findViewById(R.id.text_Counter)
        object : CountDownTimer(milliSekUntilDone, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                numberForCounter.text = (millisUntilFinished / 1000).toString()
            }

            // when the time is up
            override fun onFinish() {
                val rollButton: Button = findViewById(R.id.button)
                rollButton.setText("Throw!")
                rollButton.setBackgroundColor(Color.GREEN)
                doSensorMagic()
            }
        }.start()
    }

    //If Sensor Input Changes -> Do Stuff
    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val left_right_tilt = p0.values[0]
            val up_down_tilt = p0.values[1]

            //Change Dimensions of Text to represent Tilt ()
            speedDispay.apply {
                rotationX = up_down_tilt * 3f
                rotationY = left_right_tilt * 3f
                rotation = -left_right_tilt
                translationX = left_right_tilt * -10
                translationY = up_down_tilt * 10
            }

            val color =
                if (up_down_tilt.toInt() == 0 && left_right_tilt.toInt() == 0) Color.GREEN else Color.RED //Set NuberColor based on Tilt
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

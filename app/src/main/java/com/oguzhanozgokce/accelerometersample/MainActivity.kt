package com.oguzhanozgokce.accelerometersample

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oguzhanozgokce.accelerometersample.ui.theme.AccelerometerSampleTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var color = false
    private var xVal by mutableFloatStateOf(0f)
    private var yVal by mutableFloatStateOf(0f)
    private var zVal by mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        enableEdgeToEdge()
        setContent {
            AccelerometerSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SensorScreen(
                        xVal,
                        yVal,
                        zVal,
                        color,
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                getGyroscopeData(event)
            }
        }
    }

    private fun getGyroscopeData(event: SensorEvent) {
        xVal = event.values[0]  // X ekseni üzerindeki rotasyon hızı
        yVal = event.values[1]  // Y ekseni üzerindeki rotasyon hızı
        zVal = event.values[2]  // Z ekseni üzerindeki rotasyon hızı

        // Eğer cihaz belirli bir hızda hareket ediyorsa, renk değişikliği ve hareket algılama
        val rotationThreshold = Math.sqrt(
            (xVal * xVal + yVal * yVal + zVal * zVal).toDouble()
        )

        if (rotationThreshold >= 3) {
            Toast.makeText(this, "Cihaz hareket etti!", Toast.LENGTH_SHORT).show()
            color = !color
        }
    }


    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }
}

@Composable
fun SensorScreen(
    xVal: Float,
    yVal: Float,
    zVal: Float,
    color: Boolean,
    name: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (color) Color.Yellow else MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SensorValuesDisplay(xVal, yVal, zVal)
    }
}

@Composable
fun SensorValuesDisplay(xVal: Float, yVal: Float, zVal: Float) {
    Text(text = "X Değeri: $xVal")
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Y Değeri: $yVal")
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Z Değeri: $zVal")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AccelerometerSampleTheme {
        SensorScreen(0f, 0f, 0f, false, "Android")

    }
}
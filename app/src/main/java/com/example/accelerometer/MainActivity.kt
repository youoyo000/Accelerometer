package com.example.accelerometer


import android.content.Context.SENSOR_SERVICE
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.accelerometer.ui.theme.AccelerometerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccelerometerTheme {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                AppScreen()
            }
        }
    }
}

@Composable
fun AppScreen() {
    var showSecond by remember { mutableStateOf(false) } // 單一狀態控制畫面切換

    if (showSecond) {
        SecondScreen { showSecond = false } // 顯示第二畫面並提供返回功能
    } else {
        FirstScreen { showSecond = true } // 顯示第一畫面並提供跳轉功能
    }
}

@Composable
fun FirstScreen(onNavigateToSecond: () -> Unit) {
    var msg by remember { mutableStateOf("加速感應器實例") }
    var msg2 by remember { mutableStateOf("") }
    var xTilt by remember { mutableStateOf(0f) }
    var yTilt by remember { mutableStateOf(0f) }
    var zTilt by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                xTilt = event.values[0]
                yTilt = event.values[1]
                zTilt = event.values[2]
                msg = String.format(
                    "加速感應器實例\nx軸: %1.2f\ny軸: %1.2f\nz軸: %1.2f",
                    xTilt, yTilt, zTilt
                )
                msg2 = when {
                    Math.abs(xTilt) < 1 && Math.abs(yTilt) < 1 && zTilt < -9 -> "朝下平放"
                    Math.abs(xTilt) + Math.abs(yTilt) + Math.abs(zTilt) > 32 -> "手機搖晃"
                    else -> ""
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    LaunchedEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    val offsetX = (-xTilt * 10).dp
    val offsetY = (yTilt * 10).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(msg)
        Text(msg2)

        Image(
            painter = painterResource(id = R.drawable.penguin),
            contentDescription = "Penguin",
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .padding(16.dp)
        )

        Button(onClick = onNavigateToSecond) { // 點擊跳轉到第二畫面
            Text("跳轉畫面2")
        }
    }
}

@Composable
fun SecondScreen(onNavigateToFirst: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Text("這是第二個畫面")
        Button(onClick = onNavigateToFirst) { // 點擊返回第一畫面
            Text("返回畫面1")
        }
    }
}

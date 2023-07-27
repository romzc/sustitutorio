package com.romzc.sustitutorio

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.romzc.sustitutorio.model.SensorData
import com.romzc.sustitutorio.screens.HomeScreen
import com.romzc.sustitutorio.ui.theme.SustitutorioTheme

class MainActivity : ComponentActivity() {

    private val BROKER_URI = "ssl://a3brjmyw4c304l-ats.iot.us-east-2.amazonaws.com"
    private val TOPIC = "mobile/mensajes"
    private val CLIENT_ID = "SensorLab"
    private val QOS = 1


    private lateinit var mqttManager: MQTTManager

    /**
     *
     * En ocasiones la aplicación demora en iniciar, esto se debe a que se esta conectando
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mqttManager = MQTTManager(BROKER_URI,CLIENT_ID,QOS,TOPIC, applicationContext)
        mqttManager.connect()

        setContent {
            SustitutorioTheme {

                HomeScreen(
                    /* Maneja el envio de mensajes */
                    onHandledSubmit = { timestamp, value, unit, notes ->
                        val sensorData = SensorData(timestamp, value, unit, notes)
                        mqttManager.publish(sensorData)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttManager.disconnect()
    }
}
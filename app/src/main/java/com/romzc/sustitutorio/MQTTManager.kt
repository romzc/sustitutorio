package com.romzc.sustitutorio

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.romzc.sustitutorio.model.SensorData
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.InputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext


class MQTTManager(
    private val mqttServerUri: String,
    private val clientId: String,
    private val qos: Int,
    private val topic: String,
    private val applicationContext: Context
) {

    private lateinit var mqttClient: MqttClient

    fun connect() {
        try {
            val certificateFactory = CertificateFactory.getInstance("x.509")
            val certificateInputStream: InputStream = applicationContext.assets.open("cert.der")
            val certificate = certificateFactory.generateCertificate(certificateInputStream) as X509Certificate

            // cargar la clave privada desde el archivo
            val keyInputStream : InputStream = applicationContext.assets.open("private.der")
            val privateKeyBytes = keyInputStream.readBytes()
            val keyFactory = KeyFactory.getInstance("RSA")
            val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
            val privateKey: PrivateKey = keyFactory.generatePrivate(privateKeySpec)

            // Crear el contexto SSL
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            keyStore.setCertificateEntry("alias", certificate)
            keyStore.setKeyEntry("alias", privateKey, null, arrayOf(certificate))

            val sslContext = SSLContext.getInstance("TLSv1.2")
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, null)
            sslContext.init(keyManagerFactory.keyManagers, null, null)

            // Configurar las opciones de conexión MQTT
            val connectionOptions = MqttConnectOptions()
            connectionOptions.socketFactory = sslContext.socketFactory

            // Crear el cliente MQTT y establecer la conexión
            mqttClient = MqttClient(mqttServerUri, clientId, MemoryPersistence())
            mqttClient.connect(connectionOptions)

            // nos suscribimos para escuchar respuestas.
            subscribeTopic()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        mqttClient.disconnect()
    }

    fun publish(sensorData: SensorData ) {
        if (mqttClient.isConnected) {
            val mqttMessage = MqttMessage()
            val gson = Gson()
            val jsonData = gson.toJson(sensorData)
            mqttMessage.payload = jsonData.toByteArray()
            Log.d("APP", jsonData)
            mqttClient.publish(topic, mqttMessage)
        }
    }

    private fun subscribeTopic() {
        if (mqttClient.isConnected) {
            mqttClient.subscribe(topic, qos) { _, message ->
                val mess = message.payload.toString(Charsets.UTF_8)
                Log.d("RESPONSE_MQTT", mess)
            }
        }
    }
}
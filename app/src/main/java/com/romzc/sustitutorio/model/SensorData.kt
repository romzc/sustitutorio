package com.romzc.sustitutorio.model

import com.google.gson.annotations.SerializedName

data class SensorData(
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("Value")
    val sensorValue: String,
    @SerializedName("Unit")
    val sensorUnit: String,
    @SerializedName("Notes")
    val notes: String
) {}
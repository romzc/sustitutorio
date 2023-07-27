package com.romzc.sustitutorio.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onHandledSubmit: (String, String, String, String) -> Unit
) {
    val items = listOf("°C", "°F", "K")

    val sensorValue = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf(items[0])}


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Enviar Mensaje",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TimeStamp automático",
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = sensorValue.value,
            onValueChange = { sensorValue.value = it },
            label = { Text(text = "Valor sensor") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column() {
            Box(
                modifier = Modifier
                    .background(color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .width(180.dp)
                    .height(50.dp)
                    .clickable { expanded.value = true }
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize()
                ) {
                    Text(
                        text = "Unidad: ${selectedItem.value}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        tint = Color.White,
                    )
                }
                DropdownMenu(
                    expanded = expanded.value,
                    modifier = Modifier.width(180.dp),
                    onDismissRequest = { expanded.value = false }
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(onClick = {
                            selectedItem.value = item
                            expanded.value = false
                        }) {
                            Text(text = item)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            label = { Text(text = "Comentarios") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                onHandledSubmit(
                    date.toString(),
                    sensorValue.value,
                    selectedItem.value,
                    notes.value
                )

                sensorValue.value = ""
                notes.value = ""
            }
        ) {
            Text("Enviar")
        }
    }
}

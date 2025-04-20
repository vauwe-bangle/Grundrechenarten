package com.softopus.grundrechenarten

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun DivSettingScreen() {
    var number1Min by remember { mutableStateOf(2) }
    var number1Max by remember { mutableStateOf(9) }
    var number2Min by remember { mutableStateOf(2) }
    var number2Max by remember { mutableStateOf(9) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Einstellungen für Zahl 1
        OutlinedTextField(
            value = number1Min.toString(),
            onValueChange = { number1Min = it.toIntOrNull() ?: 2 },
            label = { Text("Ergebnis Untergrenze") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = number1Max.toString(),
            onValueChange = { number1Max = it.toIntOrNull() ?: 9 },
            label = { Text("Ergebnis Obergrenze") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Einstellungen für Zahl 2
        OutlinedTextField(
            value = number2Min.toString(),
            onValueChange = { number2Min = it.toIntOrNull() ?: 2 },
            label = { Text("Teiler (Divisor) Untergrenze") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = number2Max.toString(),
            onValueChange = { number2Max = it.toIntOrNull() ?: 9 },
            label = { Text("Teiler (Divisor) Obergrenze") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

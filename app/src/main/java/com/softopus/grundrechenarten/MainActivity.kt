package com.softopus.grundrechenarten

import android.content.Context
//import android.content.Intent
//import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
//import androidx.core.content.FileProvider
//import java.io.File

data class MathSettings(
    var numberOfTasks: Int = 10,
    var number1Min: Int = 2,
    var number1Max: Int = 9,
    var number2Min: Int = 2,
    var number2Max: Int = 9
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrundrechenartenApp()
        }
    }
}

@Composable
fun GrundrechenartenApp() {
    var selectedScreen by remember { mutableStateOf("Home") }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Home", "ADD", "SUB", "MUL", "DIV", "Einstellungen").forEach { screen ->
                        TextButton(
                            onClick = { selectedScreen = screen },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (selectedScreen == screen) Color.Black else Color.Blue,
                                containerColor = if (selectedScreen == screen) Color.Yellow else Color.White
                            )
                        ) {
                            Text(screen)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedScreen) {
                "Home" -> HomeScreen()
                "ADD" -> AddScreen()
                "SUB" -> SubScreen()
                "MUL" -> MulScreen()
                "DIV" -> DivScreen()
                "Einstellungen" -> EinstellungenScreen(onSettingsSaved = { selectedScreen = "Home" })
            }
        }
    }
}

@Composable
fun HomeScreen() {
    //val context = LocalContext.current

    val uriHandler = LocalUriHandler.current

    // Long-Click-Handler für den Menüpunkt "Home"
    val onHomeLongClick = {
        uriHandler.openUri(
            uri = "https://wikik.de/PDF/gra-manual.pdf",
        )

        //openPdf(context, "home_instructions.pdf")
    }

    Image(
        painter = painterResource(id = R.drawable.gra),
        contentDescription = "Start Image",
        modifier = Modifier
            .size(300.dp)
            .clickable {
                // Handle normal click if needed
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onHomeLongClick() }
                )
            }
    )
}

@Composable
fun EinstellungenScreen(onSettingsSaved: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("math_settings", Context.MODE_PRIVATE)

    var selectedOperation by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

    var settings by remember {
        mutableStateOf(
            MathSettings(
                numberOfTasks = 10,
                number1Min = 2,
                number1Max = 9,
                number2Min = 2,
                number2Max = 9
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "EINSTELLUNGEN",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (!showSettings) {
            Text(
                text = "Wähle die Rechenart:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            listOf(
                "ADD" to "Addition",
                "SUB" to "Subtraktion",
                "MUL" to "Multiplikation",
                "DIV" to "Division"
            ).forEach { (op, label) ->
                ElevatedButton(
                    onClick = {
                        selectedOperation = op
                        val prefix = op.lowercase()
                        settings = MathSettings(
                            numberOfTasks = sharedPreferences.getInt("${prefix}_numberOfTasks", 10),
                            number1Min = sharedPreferences.getInt("${prefix}_number1Min", 2),
                            number1Max = sharedPreferences.getInt("${prefix}_number1Max", 9),
                            number2Min = sharedPreferences.getInt("${prefix}_number2Min", 2),
                            number2Max = sharedPreferences.getInt("${prefix}_number2Max", 9)
                        )
                        showSettings = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(label)
                }
            }
        }

        if (showSettings) {
            Text(
                text = "Einstellungen für ${
                    when (selectedOperation) {
                        "ADD" -> "Addition"
                        "SUB" -> "Subtraktion"
                        "MUL" -> "Multiplikation"
                        "DIV" -> "Division"
                        else -> selectedOperation
                    }
                }",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = settings.numberOfTasks.toString(),
                onValueChange = {
                    settings = settings.copy(numberOfTasks = it.toIntOrNull() ?: settings.numberOfTasks)
                },
                label = { Text("Anzahl der Aufgaben") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.number1Min.toString(),
                onValueChange = {
                    settings = settings.copy(number1Min = it.toIntOrNull() ?: settings.number1Min)
                },
                label = {
                    Text(
                        if (selectedOperation == "DIV") "Ergebnis Untergrenze"
                        else "Zahl1 Untergrenze (UG1)"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.number1Max.toString(),
                onValueChange = {
                    settings = settings.copy(number1Max = it.toIntOrNull() ?: settings.number1Max)
                },
                label = {
                    Text(
                        if (selectedOperation == "DIV") "Ergebnis Obergrenze"
                        else "Zahl1 Obergrenze (OG1)"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.number2Min.toString(),
                onValueChange = {
                    settings = settings.copy(number2Min = it.toIntOrNull() ?: settings.number2Min)
                },
                label = {
                    Text(
                        if (selectedOperation == "DIV") "Teiler (Divisor) Untergrenze"
                        else "Zahl2 Untergrenze (UG2)"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.number2Max.toString(),
                onValueChange = {
                    settings = settings.copy(number2Max = it.toIntOrNull() ?: settings.number2Max)
                },
                label = {
                    Text(
                        if (selectedOperation == "DIV") "Teiler (Divisor) Obergrenze"
                        else "Zahl2 Obergrenze (OG2)"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        showSettings = false
                        selectedOperation = ""
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Zurück")
                }

                Button(
                    onClick = {
                        val prefix = selectedOperation.lowercase()
                        with(sharedPreferences.edit()) {
                            putInt("${prefix}_numberOfTasks", settings.numberOfTasks)
                            putInt("${prefix}_number1Min", settings.number1Min)
                            putInt("${prefix}_number1Max", settings.number1Max)
                            putInt("${prefix}_number2Min", settings.number2Min)
                            putInt("${prefix}_number2Max", settings.number2Max)
                            apply()
                        }
                        onSettingsSaved()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Einstellungen übernehmen")
                }
            }
        }
    }
}
package com.softopus.grundrechenarten

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@Composable
fun AddScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("math_settings", Context.MODE_PRIVATE)

    // Einstellungen für die Addition laden

    val addSettings = remember {
        MathSettings(
            numberOfTasks = sharedPreferences.getInt("add_numberOfTasks", 10),
            number1Min = sharedPreferences.getInt("add_number1Min", 2),
            number1Max = sharedPreferences.getInt("add_number1Max", 9),
            number2Min = sharedPreferences.getInt("add_number2Min", 2),
            number2Max = sharedPreferences.getInt("add_number2Max", 9)
        )
    }


    var currentTask by remember { mutableStateOf(1) }
    var number1 by remember { mutableStateOf(0) }
    var number2 by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var exerciseResults by remember { mutableStateOf<List<ExerciseResult>>(emptyList()) }
    var showTable by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var showDialog by remember { mutableStateOf(false) }

    // Zufallszahlen generieren
    LaunchedEffect(currentTask) {
        number1 = Random.nextInt(addSettings.number1Min, addSettings.number1Max + 1)
        number2 = Random.nextInt(addSettings.number2Min, addSettings.number2Max + 1)
    }

    // Zeitmessung starten
    val startTime = remember { System.currentTimeMillis() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFD1E7DD)), // Helles Blau
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ADDITION",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Übungsnummer und Anzahl der Aufgaben
        Text(
            text = "$currentTask/${addSettings.numberOfTasks}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Zufallszahlen anzeigen
        Text(
            text = "$number1 + $number2 = ?",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Textfeld für die Antwort des Benutzers
        val textFieldColors = if (isAnswerCorrect) {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.Green,
                cursorColor = Color.Green
            )
        } else {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Red,
                cursorColor = Color.Red
            )
        }

        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            label = { Text("Antwort eingeben") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Button "Überprüfen"
        Button(
            onClick = {
                val correctAnswer = number1 + number2
                val isCorrect = userAnswer.toIntOrNull() == correctAnswer
                exerciseResults = exerciseResults + ExerciseResult(
                    taskNumber = currentTask,
                    exercise = "$number1 + $number2",
                    correctAnswer = correctAnswer,
                    userInput = userAnswer,
                    isCorrect = isCorrect
                )

                if (isCorrect) {
                    resultMessage = "Richtig!"
                    isAnswerCorrect = true
                } else {
                    resultMessage = "Falsch! Die richtige Antwort war $correctAnswer."
                    isAnswerCorrect = false
                }

                // Nächste Aufgabe vorbereiten
                if (currentTask < addSettings.numberOfTasks) {
                    currentTask++
                    userAnswer = ""
                } else {
                    resultMessage += " Alle Aufgaben abgeschlossen!"
                    showTable = true

                    // Zeitmessung stoppen
                    elapsedTime = System.currentTimeMillis() - startTime

                    // Dialog anzeigen
                    showDialog = true
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Überprüfen")
        }

        // Ergebnismeldung anzeigen
        if (resultMessage.isNotEmpty()) {
            Text(
                text = resultMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isAnswerCorrect) Color.Green else Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Tabelle anzeigen, wenn alle Aufgaben abgeschlossen sind
        if (showTable) {
            ExerciseResultsTable(exerciseResults = exerciseResults)

            // Gesamtergebnis anzeigen
            val correctCount = exerciseResults.count { it.isCorrect }
            Text(
                text = "$correctCount von ${exerciseResults.size} Aufgaben waren richtig.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Du hast ${elapsedTime / 1000} Sekunden benötigt.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Dialog anzeigen
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Ergebnisse speichern") },
            text = { Text("Sollen die Ergebnisse im Ordner Download gespeichert werden?") },
            confirmButton = {
                Button(onClick = {
                    saveResultsToFile(context, exerciseResults, elapsedTime)
                    showDialog = false
                }) {
                    Text("Ja")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Nein")
                }
            }
        )
    }
}

@Composable
fun ExerciseResultsTable(exerciseResults: List<ExerciseResult>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Tabellenkopf
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Aufgabe Nr.", modifier = Modifier.weight(1f))
            Text("Aufgabe", modifier = Modifier.weight(1f))
            Text("Richtiges Ergebnis", modifier = Modifier.weight(1f))
            Text("Eingabe", modifier = Modifier.weight(1f))
            Text("Ergebnis", modifier = Modifier.weight(1f))
        }

        // Tabelleninhalt
        exerciseResults.forEach { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(result.taskNumber.toString(), modifier = Modifier.weight(1f))
                Text(result.exercise, modifier = Modifier.weight(1f))
                Text(result.correctAnswer.toString(), modifier = Modifier.weight(1f))
                Text(result.userInput, modifier = Modifier.weight(1f))
                Text(if (result.isCorrect) "R" else "F", modifier = Modifier.weight(1f))
            }
        }
    }
}

fun saveResultsToFile(context: Context, exerciseResults: List<ExerciseResult>, elapsedTime: Long) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
    val timestamp = dateFormat.format(Date())
    val fileName = "ADD-$timestamp.csv"
    val downloadDir = File("/storage/emulated/0/Download")
    val file = File(downloadDir, fileName)
    val writer = FileWriter(file)
    try {
        writer.append("Aufgabe Nr., Aufgabe, Richtiges Ergebnis, Eingabe, Ergebnis\n")
        exerciseResults.forEach { result ->
            writer.append("${result.taskNumber}, ${result.exercise}, ${result.correctAnswer}, ${result.userInput}, ${if (result.isCorrect) "R" else "F"}\n")
        }
        // Zusammenfassung hinzufügen
        val correctCount = exerciseResults.count { it.isCorrect }
        writer.append("\n$correctCount von ${exerciseResults.size} Aufgaben waren richtig.")
        writer.append("\nDu hast ${elapsedTime / 1000} Sekunden benötigt.")
        writer.flush()
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

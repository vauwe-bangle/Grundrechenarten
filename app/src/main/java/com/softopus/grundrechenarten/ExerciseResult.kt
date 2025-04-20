package com.softopus.grundrechenarten

data class ExerciseResult(
    val taskNumber: Int,
    val exercise: String,
    val correctAnswer: Int,
    val userInput: String,
    val isCorrect: Boolean
)

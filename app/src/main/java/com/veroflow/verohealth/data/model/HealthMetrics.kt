package com.veroflow.verohealth.data.model

enum class MedicationStatus { ACTIVE, DUE_SOON, MISSED, COMPLETED, PAUSED }

data class MedicationReminder(
    val id: String,
    val medicineName: String,
    val dosage: String,
    val frequencyPerDay: Int,
    val durationDays: Int,
    val daysElapsed: Int,
    val remindersEnabled: Boolean = true,
    val notificationTime: String = "09:00 AM",
    val status: MedicationStatus,
    val dosesTakenToday: Int = 0
) {
    val remainingDays: Int get() = (durationDays - daysElapsed).coerceAtLeast(0)
    val completionPercent: Int get() = if (durationDays == 0) 0 else
        (((daysElapsed.toFloat() / durationDays) * 100).toInt()).coerceIn(0, 100)
}

enum class HealthMetricType(val label: String, val unit: String) {
    HEART_RATE("Heart Rate", "bpm"),
    BLOOD_PRESSURE("Blood Pressure", "mmHg"),
    BLOOD_SUGAR("Blood Sugar", "mg/dL"),
    OXYGEN("Oxygen Level", "%"),
    TEMPERATURE("Body Temperature", "°F"),
    WEIGHT("Weight", "kg")
}

data class HealthReading(
    val id: String,
    val type: HealthMetricType,
    val value: String, // free-form to allow "120/80" for BP
    val dateTime: String,
    val notes: String = ""
)

data class HealthInsightItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String
)

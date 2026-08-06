package com.veroflow.verohealth.data.model

enum class Specialty(val label: String) {
    CARDIOLOGY("Cardiology"),
    DERMATOLOGY("Dermatology"),
    PEDIATRICS("Pediatrics"),
    ORTHOPEDICS("Orthopedics"),
    NEUROLOGY("Neurology"),
    GYNECOLOGY("Gynecology"),
    GENERAL_MEDICINE("General Medicine"),
    ENT("ENT"),
    OPHTHALMOLOGY("Ophthalmology"),
    PSYCHIATRY("Psychiatry"),
    DENTISTRY("Dentistry"),
    PULMONOLOGY("Pulmonology")
}

data class Hospital(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val rating: Double
)

/** A single weekly availability window, e.g. Monday 09:00-13:00. */
data class AvailabilityWindow(
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String
)

data class Doctor(
    val id: String,
    val name: String,
    val specialty: Specialty,
    val hospital: Hospital,
    val qualification: String,
    val experienceYears: Int,
    val rating: Double,
    val reviewCount: Int,
    val consultationFee: Double,
    val gender: Gender,
    val biography: String,
    val languages: List<String>,
    val weeklyAvailability: List<AvailabilityWindow>,
    val isAvailableToday: Boolean
)

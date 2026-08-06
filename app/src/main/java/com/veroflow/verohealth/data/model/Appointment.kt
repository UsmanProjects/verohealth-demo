package com.veroflow.verohealth.data.model

enum class ConsultationType(val label: String) {
    VIRTUAL("Virtual Consultation"),
    IN_PERSON("In-Person Consultation")
}

enum class AppointmentStatus { UPCOMING, COMPLETED, CANCELLED }

enum class PaymentStatus { PENDING, PAID, FAILED }

data class TimeSlot(
    val time: String,       // e.g. "09:00 AM"
    val period: String,     // "Morning" | "Afternoon" | "Evening"
    val isAvailable: Boolean
)

data class Appointment(
    val id: String,
    val patientId: String,
    val doctor: Doctor,
    val date: String,           // ISO yyyy-MM-dd
    val time: String,           // "09:00 AM"
    val consultationType: ConsultationType,
    val symptoms: String = "",
    val consultationFee: Double,
    val hospitalFee: Double,
    val tax: Double,
    val discount: Double = 0.0,
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING
) {
    val totalAmount: Double get() = consultationFee + hospitalFee + tax - discount
}

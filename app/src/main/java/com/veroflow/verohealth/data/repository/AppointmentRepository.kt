package com.veroflow.verohealth.data.repository

import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.model.AppointmentStatus
import com.veroflow.verohealth.data.model.ConsultationType
import com.veroflow.verohealth.data.model.Doctor
import com.veroflow.verohealth.data.model.PaymentStatus
import com.veroflow.verohealth.data.model.TimeSlot
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

sealed class PaymentResult {
    data class Success(val appointment: Appointment) : PaymentResult()
    data class Failure(val message: String, val retryable: Boolean = true) : PaymentResult()
}

/**
 * In-memory appointment calendar + booking "backend". Availability is derived
 * deterministically (seeded by doctor id + date) so the same doctor/date pair
 * always produces the same fully-booked/slot state across app runs — again,
 * useful ground truth for VeroFlow to validate against.
 */
object AppointmentRepository {

    private val bookedAppointments = mutableListOf<Appointment>()
    private val allSlotTimes: List<Pair<String, String>> = listOf(
        "09:00 AM" to "Morning", "09:30 AM" to "Morning",
        "10:00 AM" to "Morning", "10:30 AM" to "Morning",
        "01:00 PM" to "Afternoon", "01:30 PM" to "Afternoon", "02:00 PM" to "Afternoon",
        "05:00 PM" to "Evening", "05:30 PM" to "Evening", "06:00 PM" to "Evening"
    )

    /** Deterministic per doctor+date "fully booked" flag (roughly 1 in 6 dates). */
    fun isDateFullyBooked(doctor: Doctor, date: LocalDate): Boolean {
        val seed = (doctor.id + date.toString()).hashCode()
        return Random(seed).nextInt(0, 6) == 0
    }

    fun isDateInPast(date: LocalDate): Boolean = date.isBefore(LocalDate.now())

    /** Returns time slots for a doctor/date with some deterministically disabled. */
    fun slotsFor(doctor: Doctor, date: LocalDate): List<TimeSlot> {
        if (isDateFullyBooked(doctor, date)) {
            return allSlotTimes.map { (time, period) -> TimeSlot(time, period, isAvailable = false) }
        }
        val seed = (doctor.id + date.toString() + "slots").hashCode()
        val rng = Random(seed)
        return allSlotTimes.map { (time, period) ->
            TimeSlot(time, period, isAvailable = rng.nextInt(0, 10) > 2)
        }
    }

    suspend fun confirmAppointment(appointment: Appointment): Appointment {
        delay(300)
        bookedAppointments.add(appointment)
        return appointment
    }

    /**
     * Simulated payment processing.
     * Deterministic testability hooks:
     *  - Card number ending in "0000" always fails (declined).
     *  - Any other complete, well-formed input succeeds.
     */
    suspend fun processPayment(
        appointment: Appointment,
        cardNumber: String
    ): PaymentResult {
        delay(1200)
        if (cardNumber.replace(" ", "").endsWith("0000")) {
            return PaymentResult.Failure("Payment declined by issuing bank. Please try another card.")
        }
        val paid = appointment.copy(paymentStatus = PaymentStatus.PAID)
        bookedAppointments.add(paid)
        return PaymentResult.Success(paid)
    }

    fun appointmentsForPatient(patientId: String): List<Appointment> =
        bookedAppointments.filter { it.patientId == patientId }

    fun appointmentById(id: String): Appointment? =
        bookedAppointments.firstOrNull { it.id == id }

    fun cancelAppointment(id: String) {
        val index = bookedAppointments.indexOfFirst { it.id == id }
        if (index >= 0) {
            bookedAppointments[index] = bookedAppointments[index].copy(status = AppointmentStatus.CANCELLED)
        }
    }

    fun nextAppointmentId(): String = "APT-${(100000..999999).random()}"

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
}

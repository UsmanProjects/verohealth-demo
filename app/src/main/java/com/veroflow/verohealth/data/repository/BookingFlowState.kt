package com.veroflow.verohealth.data.repository

import androidx.compose.runtime.mutableStateOf
import com.veroflow.verohealth.data.model.ConsultationType
import com.veroflow.verohealth.data.model.Doctor
import java.time.LocalDate

/**
 * Transient, in-memory state for a booking currently in progress. Simpler than
 * passing every field through NavHost arguments across 5 screens — each
 * booking screen reads/writes here, and PaymentScreen clears it once the
 * appointment is confirmed (or the user cancels out of the flow).
 */
object BookingFlowState {
    var selectedDoctor = mutableStateOf<Doctor?>(null)
    var selectedDate = mutableStateOf<LocalDate?>(null)
    var selectedTime = mutableStateOf<String?>(null)
    var consultationType = mutableStateOf(ConsultationType.IN_PERSON)
    var symptomsNotes = mutableStateOf("")

    fun reset() {
        selectedDoctor.value = null
        selectedDate.value = null
        selectedTime.value = null
        consultationType.value = ConsultationType.IN_PERSON
        symptomsNotes.value = ""
    }
}

package com.veroflow.verohealth.data.repository

import androidx.compose.runtime.mutableStateOf
import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.model.LabReport

/**
 * Holds the "currently selected" complex object for screens reached from a
 * list (Doctor Details, Appointment Details, Lab Report Viewer, Video
 * Consultation). Simpler than serializing whole objects through NavHost
 * string arguments; the previous screen sets it right before navigating.
 */
object SelectionState {
    var selectedAppointment = mutableStateOf<Appointment?>(null)
    var selectedLabReport = mutableStateOf<LabReport?>(null)
}

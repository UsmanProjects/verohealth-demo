package com.veroflow.verohealth.data.repository

import androidx.compose.runtime.mutableStateOf
import com.veroflow.verohealth.data.model.Patient
import com.veroflow.verohealth.ui.theme.ThemeMode

/**
 * Minimal app-wide session holder. Deliberately simple (no DI framework) so the
 * project stays easy to read module-by-module; can be swapped for a proper
 * repository/DataStore-backed implementation later without touching UI code,
 * since screens only ever read Session.currentPatient / call Session.* methods.
 */
object Session {
    var currentPatient = mutableStateOf<Patient?>(null)
        private set

    /** True the very first time the app is launched on this "device" (process). */
    var isFirstLaunch = mutableStateOf(true)

    var rememberMe = mutableStateOf(false)
    var themeMode = mutableStateOf(ThemeMode.SYSTEM)

    fun signIn(patient: Patient) {
        currentPatient.value = patient
    }

    fun updatePatient(patient: Patient) {
        currentPatient.value = patient
    }

    fun signOut() {
        currentPatient.value = null
    }

    val isLoggedIn: Boolean get() = currentPatient.value != null
}

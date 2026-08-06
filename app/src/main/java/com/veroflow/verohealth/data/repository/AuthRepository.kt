package com.veroflow.verohealth.data.repository

import com.veroflow.verohealth.data.model.BloodGroup
import com.veroflow.verohealth.data.model.Gender
import com.veroflow.verohealth.data.model.Patient
import kotlinx.coroutines.delay
import java.util.UUID

sealed class AuthResult {
    data class Success(val patient: Patient) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}

/**
 * Fully offline auth "backend". Holds patients in memory for the lifetime of the
 * process. A fixed demo account is always present so VeroFlow (or a human tester)
 * has a deterministic, documented login path in addition to whatever accounts
 * get created via Registration.
 *
 * Deterministic testability hooks:
 *  - demo.patient@verohealth.test / Passw0rd! always logs in successfully.
 *  - Any email containing "locked" always returns AuthResult.Failure (simulates a
 *    disabled account) — lets VeroFlow exercise the error path on demand.
 */
object AuthRepository {

    private val patients = mutableListOf<Patient>()

    const val DEMO_EMAIL = "demo.patient@verohealth.test"
    const val DEMO_PASSWORD = "Passw0rd!"

    init {
        patients.add(
            Patient(
                id = "P-000001",
                firstName = "Ayesha",
                lastName = "Khan",
                dateOfBirth = "1994-03-12",
                gender = Gender.FEMALE,
                email = DEMO_EMAIL,
                phone = "+923001234567",
                nationalId = "35202-1234567-1",
                passwordHash = mockHash(DEMO_PASSWORD),
                country = "Pakistan",
                bloodGroup = BloodGroup.O_POS,
                receiveHealthTips = true,
                address = "House 12, Street 4, Lahore",
                emergencyContactName = "Bilal Khan",
                emergencyContactPhone = "+923007654321",
                allergies = listOf("Penicillin"),
                chronicConditions = listOf("Hypertension")
            )
        )
    }

    private fun mockHash(raw: String): String = "h_${raw.hashCode()}"

    fun findByEmail(email: String): Patient? =
        patients.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }

    suspend fun register(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: Gender,
        email: String,
        phone: String,
        nationalId: String,
        password: String,
        country: String,
        bloodGroup: BloodGroup,
        receiveHealthTips: Boolean
    ): AuthResult {
        delay(600) // simulate processing latency -> loading indicator
        if (findByEmail(email) != null) {
            return AuthResult.Failure("An account with this email already exists")
        }
        val patient = Patient(
            id = "P-${(100000..999999).random()}",
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth,
            gender = gender,
            email = email.trim(),
            phone = phone,
            nationalId = nationalId,
            passwordHash = mockHash(password),
            country = country,
            bloodGroup = bloodGroup,
            receiveHealthTips = receiveHealthTips
        )
        patients.add(patient)
        return AuthResult.Success(patient)
    }

    suspend fun login(email: String, password: String): AuthResult {
        delay(500)
        if (email.contains("locked", ignoreCase = true)) {
            return AuthResult.Failure("This account has been disabled. Contact support.")
        }
        val patient = findByEmail(email)
            ?: return AuthResult.Failure("No account found with this email")
        if (patient.passwordHash != mockHash(password)) {
            return AuthResult.Failure("Incorrect email or password")
        }
        return AuthResult.Success(patient)
    }

    suspend fun sendPasswordReset(email: String): AuthResult {
        delay(700)
        val patient = findByEmail(email)
            ?: return AuthResult.Failure("No account found with this email")
        return AuthResult.Success(patient)
    }
}

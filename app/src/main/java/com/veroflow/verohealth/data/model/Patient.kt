package com.veroflow.verohealth.data.model

enum class Gender { MALE, FEMALE, OTHER }

enum class BloodGroup(val label: String) {
    A_POS("A+"), A_NEG("A-"),
    B_POS("B+"), B_NEG("B-"),
    AB_POS("AB+"), AB_NEG("AB-"),
    O_POS("O+"), O_NEG("O-")
}

/**
 * Local mock patient/account record. Password is stored as a simple mock hash
 * (never plaintext) purely so the demo app can simulate authentication offline.
 */
data class Patient(
    val id: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String, // ISO yyyy-MM-dd
    val gender: Gender,
    val email: String,
    val phone: String,
    val nationalId: String,
    val passwordHash: String,
    val country: String,
    val bloodGroup: BloodGroup,
    val receiveHealthTips: Boolean = false,
    val registrationDateMillis: Long = System.currentTimeMillis(),
    val profilePhotoUri: String? = null,
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList()
) {
    val fullName: String get() = "$firstName $lastName"
}

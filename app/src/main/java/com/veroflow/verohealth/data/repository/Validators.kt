package com.veroflow.verohealth.data.repository

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Centralized, reusable validation rules. Kept framework-agnostic (no Android imports)
 * so they're easy to unit test and easy for VeroFlow to reason about via error text.
 */
object Validators {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val PHONE_REGEX = Regex("^\\+?[0-9]{7,15}$")

    fun emailError(value: String): String? = when {
        value.isBlank() -> "Email is required"
        !EMAIL_REGEX.matches(value.trim()) -> "Enter a valid email address"
        else -> null
    }

    fun phoneError(value: String): String? = when {
        value.isBlank() -> "Phone number is required"
        !PHONE_REGEX.matches(value.trim()) -> "Enter a valid phone number"
        else -> null
    }

    fun requiredError(value: String, fieldName: String): String? =
        if (value.isBlank()) "$fieldName is required" else null

    fun passwordError(value: String): String? = when {
        value.isBlank() -> "Password is required"
        value.length < 8 -> "Password must be at least 8 characters"
        !value.any { it.isDigit() } -> "Password must include at least one number"
        !value.any { it.isLetter() } -> "Password must include at least one letter"
        else -> null
    }

    fun confirmPasswordError(password: String, confirm: String): String? = when {
        confirm.isBlank() -> "Please confirm your password"
        password != confirm -> "Passwords do not match"
        else -> null
    }

    fun dateOfBirthError(value: String): String? {
        if (value.isBlank()) return "Date of birth is required"
        val date = runCatching {
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
        }.getOrNull() ?: return "Enter a valid date (YYYY-MM-DD)"
        val today = LocalDate.now()
        if (date.isAfter(today)) return "Date of birth cannot be in the future"
        if (date.isBefore(today.minusYears(120))) return "Enter a valid date of birth"
        return null
    }

    fun isPastOrInvalid(value: String): Boolean = try {
        LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).isBefore(LocalDate.now())
    } catch (e: DateTimeParseException) {
        true
    }
}

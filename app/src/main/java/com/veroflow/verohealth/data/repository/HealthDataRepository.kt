package com.veroflow.verohealth.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.veroflow.verohealth.data.mock.MockDataProvider
import com.veroflow.verohealth.data.model.AppNotification
import com.veroflow.verohealth.data.model.ChatConversation
import com.veroflow.verohealth.data.model.ChatMessage
import com.veroflow.verohealth.data.model.DocumentCategory
import com.veroflow.verohealth.data.model.EmergencyContact
import com.veroflow.verohealth.data.model.HealthInsightItem
import com.veroflow.verohealth.data.model.HealthMetricType
import com.veroflow.verohealth.data.model.HealthReading
import com.veroflow.verohealth.data.model.InsuranceInfo
import com.veroflow.verohealth.data.model.MedicalDocument
import com.veroflow.verohealth.data.model.MedicationReminder
import com.veroflow.verohealth.data.model.MedicationStatus
import com.veroflow.verohealth.data.model.MessageSender
import com.veroflow.verohealth.data.model.MessageType
import com.veroflow.verohealth.data.model.NotificationCategory

/**
 * Single in-memory store for everything in Phase 4/5's "advanced modules".
 * All lists are mutableStateListOf so Compose screens recompose automatically
 * on add/update/remove — no separate ViewModel layer needed for a demo app
 * this size, and it keeps state mutations easy for VeroFlow to trigger and
 * for a human reviewer to trace in one place.
 */
object HealthDataRepository {

    val medications = mutableStateListOf(
        MedicationReminder("MED-1", "Amlodipine 5mg", "1 tablet", 1, 30, 6, status = MedicationStatus.ACTIVE),
        MedicationReminder("MED-2", "Aspirin 75mg", "1 tablet", 1, 30, 6, status = MedicationStatus.DUE_SOON, notificationTime = "08:00 AM"),
        MedicationReminder("MED-3", "Betamethasone Cream", "Thin layer", 2, 14, 14, status = MedicationStatus.COMPLETED),
        MedicationReminder("MED-4", "Paracetamol 500mg", "1-2 tablets", 4, 5, 1, status = MedicationStatus.MISSED)
    )

    fun markMedicationTaken(id: String) {
        val idx = medications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val current = medications[idx]
            medications[idx] = current.copy(
                dosesTakenToday = (current.dosesTakenToday + 1).coerceAtMost(current.frequencyPerDay),
                status = MedicationStatus.ACTIVE
            )
        }
    }

    fun toggleMedicationReminder(id: String) {
        val idx = medications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            medications[idx] = medications[idx].let { it.copy(remindersEnabled = !it.remindersEnabled) }
        }
    }

    fun addMedicationFromPrescription(name: String, dosage: String, frequency: String, durationDays: Int) {
        medications.add(
            MedicationReminder(
                id = "MED-${(1000..9999).random()}",
                medicineName = name,
                dosage = dosage,
                frequencyPerDay = frequency.filter { it.isDigit() }.toIntOrNull() ?: 1,
                durationDays = durationDays,
                daysElapsed = 0,
                status = MedicationStatus.ACTIVE
            )
        )
    }

    // ---- Health readings ----

    val healthReadings = mutableStateListOf(
        HealthReading("HR-1", HealthMetricType.HEART_RATE, "76", "2026-08-01 08:00"),
        HealthReading("HR-2", HealthMetricType.BLOOD_PRESSURE, "128/82", "2026-08-01 08:00"),
        HealthReading("HR-3", HealthMetricType.BLOOD_SUGAR, "104", "2026-08-01 08:00"),
        HealthReading("HR-4", HealthMetricType.WEIGHT, "78", "2026-07-28 07:30"),
        HealthReading("HR-5", HealthMetricType.HEART_RATE, "72", "2026-07-25 08:00"),
        HealthReading("HR-6", HealthMetricType.BLOOD_PRESSURE, "122/79", "2026-07-25 08:00")
    )

    fun addHealthReading(reading: HealthReading) {
        healthReadings.add(0, reading)
    }

    fun latestReading(type: HealthMetricType): HealthReading? =
        healthReadings.filter { it.type == type }.maxByOrNull { it.dateTime }

    fun readingsFor(type: HealthMetricType): List<HealthReading> =
        healthReadings.filter { it.type == type }.sortedBy { it.dateTime }

    // ---- Emergency contacts ----

    val emergencyContacts = mutableStateListOf(
        EmergencyContact("EC-1", "Bilal Khan", "Spouse", "+923007654321"),
        EmergencyContact("EC-2", "Nadia Khan", "Sister", "+923219876543")
    )

    fun addEmergencyContact(name: String, relationship: String, phone: String) {
        emergencyContacts.add(EmergencyContact("EC-${(100..999).random()}", name, relationship, phone))
    }

    fun deleteEmergencyContact(id: String) {
        emergencyContacts.removeAll { it.id == id }
    }

    // ---- Chat ----

    val conversations = mutableStateListOf<ChatConversation>().apply {
        val doctors = MockDataProvider.doctors.take(3)
        addAll(
            doctors.mapIndexed { i, doctor ->
                ChatConversation(
                    id = "CONV-${i + 1}",
                    doctor = doctor,
                    messages = mutableListOf(
                        ChatMessage("M1", MessageSender.PATIENT, MessageType.TEXT, "Hello Doctor, I wanted to follow up on my last visit.", "09:12 AM"),
                        ChatMessage("M2", MessageSender.DOCTOR, MessageType.TEXT, "Hi! How have you been feeling since starting the medication?", "09:15 AM", isRead = i != 0)
                    )
                )
            }
        )
    }

    fun sendMessage(conversationId: String, content: String) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) {
            val convo = conversations[idx]
            val newMessage = ChatMessage(
                id = "M-${(1000..9999).random()}",
                sender = MessageSender.PATIENT,
                type = MessageType.TEXT,
                content = content,
                timestamp = "Now"
            )
            conversations[idx] = convo.copy(messages = convo.messages + newMessage)
        }
    }

    fun markConversationRead(conversationId: String) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) {
            val convo = conversations[idx]
            conversations[idx] = convo.copy(messages = convo.messages.map { it.copy(isRead = true) })
        }
    }

    fun conversationForDoctor(doctor: com.veroflow.verohealth.data.model.Doctor): ChatConversation {
        val existing = conversations.firstOrNull { it.doctor.id == doctor.id }
        if (existing != null) return existing
        val created = ChatConversation(id = "CONV-${conversations.size + 1}", doctor = doctor, messages = emptyList())
        conversations.add(created)
        return created
    }

    // ---- Documents & insurance ----

    val documents = mutableStateListOf(
        MedicalDocument("DOC-1", "CBC_Report_June2026.pdf", DocumentCategory.LAB_REPORTS, "2026-06-15", 340),
        MedicalDocument("DOC-2", "Prescription_Cardiology.pdf", DocumentCategory.PRESCRIPTIONS, "2026-06-14", 120),
        MedicalDocument("DOC-3", "Vaccination_Card.jpg", DocumentCategory.VACCINATION, "2025-11-02", 890)
    )

    fun addDocument(fileName: String, category: DocumentCategory) {
        documents.add(0, MedicalDocument("DOC-${(1000..9999).random()}", fileName, category, "2026-08-05", (50..2000).random()))
    }

    fun deleteDocument(id: String) {
        documents.removeAll { it.id == id }
    }

    val insurance = mutableStateListOf(
        InsuranceInfo("StateLife Health", "SL-88213-PK", "In-patient & Out-patient", "2027-03-31")
    )

    // ---- Notifications ----

    val notifications = mutableStateListOf(
        AppNotification("N-1", NotificationCategory.APPOINTMENTS, "Upcoming Appointment", "Your appointment with Dr. Ahmed Khan is tomorrow at 10:00 AM.", "Today, 08:00 AM"),
        AppNotification("N-2", NotificationCategory.MEDICATION, "Medication Reminder", "Time to take Aspirin 75mg.", "Today, 08:00 AM"),
        AppNotification("N-3", NotificationCategory.LAB_REPORTS, "Lab Report Ready", "Your Lipid Profile report is now available.", "Yesterday, 04:30 PM"),
        AppNotification("N-4", NotificationCategory.HEALTH_TIPS, "Health Tip", "Stay hydrated — aim for 8 glasses of water a day.", "2 days ago"),
        AppNotification("N-5", NotificationCategory.PAYMENTS, "Payment Confirmation", "Payment of Rs. 2,200 for your last consultation was successful.", "3 days ago", isRead = true)
    )

    fun markNotificationRead(id: String) {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) notifications[idx] = notifications[idx].copy(isRead = true)
    }

    fun deleteNotification(id: String) {
        notifications.removeAll { it.id == id }
    }

    fun clearAllNotifications() {
        notifications.clear()
    }

    // ---- Health insights ----

    val insights = mutableStateListOf(
        HealthInsightItem("IN-1", "Blood pressure trending down", "Your average BP this week is lower than last week. Keep it up!", "Progress"),
        HealthInsightItem("IN-2", "Medication adherence: 85%", "You've taken most of your scheduled doses this month.", "Adherence"),
        HealthInsightItem("IN-3", "Upcoming check-up recommended", "It's been 6 months since your last general check-up.", "Recommendation")
    )
}

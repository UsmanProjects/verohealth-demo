package com.veroflow.verohealth.data.model

data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phone: String
)

enum class MessageSender { PATIENT, DOCTOR }
enum class MessageType { TEXT, IMAGE, DOCUMENT, VOICE }

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val type: MessageType,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = true
)

data class ChatConversation(
    val id: String,
    val doctor: Doctor,
    val messages: List<ChatMessage>
) {
    val lastMessage: ChatMessage? get() = messages.lastOrNull()
    val unreadCount: Int get() = messages.count { it.sender == MessageSender.DOCTOR && !it.isRead }
}

enum class DocumentCategory(val label: String) {
    PRESCRIPTIONS("Prescriptions"), LAB_REPORTS("Laboratory Reports"),
    INSURANCE("Insurance"), VACCINATION("Vaccination"), OTHER("Other")
}

data class MedicalDocument(
    val id: String,
    val fileName: String,
    val category: DocumentCategory,
    val uploadDate: String,
    val sizeKb: Int
)

data class InsuranceInfo(
    val provider: String,
    val policyNumber: String,
    val coverage: String,
    val expirationDate: String // ISO yyyy-MM-dd
)

enum class NotificationCategory(val label: String) {
    APPOINTMENTS("Upcoming Appointments"), MEDICATION("Medication Reminders"),
    LAB_REPORTS("Laboratory Reports"), PRESCRIPTIONS("Prescriptions"),
    PAYMENTS("Payments"), HEALTH_TIPS("Health Tips")
}

data class AppNotification(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

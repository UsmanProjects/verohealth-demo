package com.veroflow.verohealth.ui.navigation

/**
 * Single source of truth for route names across the app. Screens that need a
 * complex selected object (Doctor, Appointment, LabReport) read it from
 * BookingFlowState / SelectionState rather than a serialized nav argument —
 * simpler than round-tripping whole objects through NavHost for a demo app
 * this size. Only DoctorChat takes a real nav argument (conversationId),
 * since that's a plain string.
 */
object Routes {
    // Phase 1 — Splash / Onboarding / Auth / Dashboard
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val WELCOME = "welcome"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val DASHBOARD = "dashboard"

    // Phase 2 — Doctor discovery & booking
    const val FIND_DOCTOR = "find_doctor"
    const val DOCTOR_DETAILS = "doctor_details"
    const val APPOINTMENT_CALENDAR = "appointment_calendar"
    const val TIME_SLOT_SELECTION = "time_slot_selection"
    const val APPOINTMENT_SUMMARY = "appointment_summary"
    const val PAYMENT = "payment"
    const val APPOINTMENT_CONFIRMATION = "appointment_confirmation"

    // Phase 3 — Appointments & records
    const val UPCOMING_APPOINTMENTS = "upcoming_appointments"
    const val APPOINTMENT_DETAILS = "appointment_details"
    const val MEDICAL_RECORDS = "medical_records"
    const val LAB_REPORTS = "lab_reports"
    const val LAB_REPORT_VIEWER = "lab_report_viewer"
    const val DIGITAL_PRESCRIPTION = "digital_prescription"

    // Phase 4 — Advanced health modules
    const val MEDICATION_REMINDER = "medication_reminder"
    const val HEALTH_DASHBOARD = "health_dashboard"
    const val ADD_HEALTH_READING = "add_health_reading"
    const val EMERGENCY_SERVICES = "emergency_services"
    const val DOCTOR_CHAT_LIST = "doctor_chat_list"
    const val DOCTOR_CHAT = "doctor_chat/{conversationId}"
    const val VIDEO_CONSULTATION = "video_consultation"
    const val DOCUMENT_CENTER = "document_center"
    const val INSURANCE_INFO = "insurance_info"
    const val NOTIFICATIONS = "notifications"
    const val HEALTH_INSIGHTS = "health_insights"

    // Phase 5 — Profile & settings
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val SETTINGS = "settings"
    const val HELP_SUPPORT = "help_support"
    const val ABOUT = "about"

    fun doctorChatRoute(conversationId: String) = "doctor_chat/$conversationId"
}

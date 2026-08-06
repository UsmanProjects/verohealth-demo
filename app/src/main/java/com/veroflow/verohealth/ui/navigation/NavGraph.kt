package com.veroflow.verohealth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.model.AppointmentStatus
import com.veroflow.verohealth.data.model.ConsultationType
import com.veroflow.verohealth.data.model.PaymentStatus
import com.veroflow.verohealth.data.repository.AppointmentRepository
import com.veroflow.verohealth.data.repository.BookingFlowState
import com.veroflow.verohealth.data.repository.HealthDataRepository
import com.veroflow.verohealth.data.repository.SelectionState
import com.veroflow.verohealth.data.repository.Session
import com.veroflow.verohealth.ui.screens.appointment.AppointmentCalendarScreen
import com.veroflow.verohealth.ui.screens.appointment.AppointmentConfirmationScreen
import com.veroflow.verohealth.ui.screens.appointment.AppointmentDetailsScreen
import com.veroflow.verohealth.ui.screens.appointment.AppointmentSummaryScreen
import com.veroflow.verohealth.ui.screens.appointment.PaymentScreen
import com.veroflow.verohealth.ui.screens.appointment.TimeSlotSelectionScreen
import com.veroflow.verohealth.ui.screens.appointment.UpcomingAppointmentsScreen
import com.veroflow.verohealth.ui.screens.auth.ForgotPasswordScreen
import com.veroflow.verohealth.ui.screens.auth.LoginScreen
import com.veroflow.verohealth.ui.screens.auth.RegisterScreen
import com.veroflow.verohealth.ui.screens.chat.DoctorChatListScreen
import com.veroflow.verohealth.ui.screens.chat.DoctorChatScreen
import com.veroflow.verohealth.ui.screens.dashboard.DashboardScreen
import com.veroflow.verohealth.ui.screens.doctor.DoctorDetailsScreen
import com.veroflow.verohealth.ui.screens.doctor.FindDoctorScreen
import com.veroflow.verohealth.ui.screens.documents.DocumentCenterScreen
import com.veroflow.verohealth.ui.screens.documents.InsuranceInfoScreen
import com.veroflow.verohealth.ui.screens.emergency.EmergencyServicesScreen
import com.veroflow.verohealth.ui.screens.health.AddHealthReadingScreen
import com.veroflow.verohealth.ui.screens.health.HealthDashboardScreen
import com.veroflow.verohealth.ui.screens.health.HealthInsightsScreen
import com.veroflow.verohealth.ui.screens.health.MedicationReminderScreen
import com.veroflow.verohealth.ui.screens.notifications.NotificationsScreen
import com.veroflow.verohealth.ui.screens.onboarding.OnboardingScreen
import com.veroflow.verohealth.ui.screens.profile.EditProfileScreen
import com.veroflow.verohealth.ui.screens.profile.ProfileScreen
import com.veroflow.verohealth.ui.screens.records.DigitalPrescriptionScreen
import com.veroflow.verohealth.ui.screens.records.LabReportViewerScreen
import com.veroflow.verohealth.ui.screens.records.LabReportsScreen
import com.veroflow.verohealth.ui.screens.records.MedicalRecordsScreen
import com.veroflow.verohealth.ui.screens.settings.AboutScreen
import com.veroflow.verohealth.ui.screens.settings.HelpSupportScreen
import com.veroflow.verohealth.ui.screens.settings.LogoutDialog
import com.veroflow.verohealth.ui.screens.settings.SettingsScreen
import com.veroflow.verohealth.ui.screens.splash.SplashScreen
import com.veroflow.verohealth.ui.screens.video.VideoConsultationScreen
import com.veroflow.verohealth.ui.screens.welcome.WelcomeScreen

/**
 * Full NavGraph across all 36 screens. Screens needing a complex selected
 * object (Doctor, Appointment, LabReport) read it from BookingFlowState /
 * SelectionState, which the previous screen populates just before
 * navigating — see Routes.kt for the rationale.
 */
@Composable
fun VeroHealthNavGraph(navController: NavHostController = rememberNavController()) {
    val patient by Session.currentPatient
    val themeMode by Session.themeMode

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                isFirstLaunch = Session.isFirstLaunch.value,
                onFinished = { goToOnboarding ->
                    val destination = if (goToOnboarding) Routes.ONBOARDING else Routes.WELCOME
                    navController.navigate(destination) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onSkip = {
                    Session.isFirstLaunch.value = false
                    navController.navigate(Routes.WELCOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                },
                onGetStarted = {
                    Session.isFirstLaunch.value = false
                    navController.navigate(Routes.WELCOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                }
            )
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onGuestClick = { navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.WELCOME) { inclusive = true } } }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegistered = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } } },
                onBackToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { loggedInPatient ->
                    Session.signIn(loggedInPatient)
                    navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.WELCOME) { inclusive = true } }
                },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onCancel = { navController.popBackStack() },
                onReturnToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            val appointments = patient?.let { AppointmentRepository.appointmentsForPatient(it.id) } ?: emptyList()
            val unread = HealthDataRepository.notifications.count { !it.isRead }
            DashboardScreen(
                patient = patient,
                upcomingAppointments = appointments.filter { it.status == AppointmentStatus.UPCOMING },
                onFindDoctor = { navController.navigate(Routes.FIND_DOCTOR) },
                onUpcomingAppointments = { navController.navigate(Routes.UPCOMING_APPOINTMENTS) },
                onMedicalRecords = { navController.navigate(Routes.MEDICAL_RECORDS) },
                onLabReports = { navController.navigate(Routes.LAB_REPORTS) },
                onPrescriptions = { navController.navigate(Routes.DIGITAL_PRESCRIPTION) },
                onEmergency = { navController.navigate(Routes.EMERGENCY_SERVICES) },
                onNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onMessages = { navController.navigate(Routes.DOCTOR_CHAT_LIST) },
                onProfile = { navController.navigate(Routes.PROFILE) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onHealthDashboard = { navController.navigate(Routes.HEALTH_DASHBOARD) },
                onMedicationReminder = { navController.navigate(Routes.MEDICATION_REMINDER) },
                unreadNotificationCount = unread
            )
        }

        // ---- Phase 2: Doctor discovery & booking ----

        composable(Routes.FIND_DOCTOR) {
            FindDoctorScreen(
                onBack = { navController.popBackStack() },
                onDoctorSelected = { doctor ->
                    BookingFlowState.selectedDoctor.value = doctor
                    navController.navigate(Routes.DOCTOR_DETAILS)
                }
            )
        }

        composable(Routes.DOCTOR_DETAILS) {
            val doctor = BookingFlowState.selectedDoctor.value
            if (doctor != null) {
                DoctorDetailsScreen(
                    doctor = doctor,
                    onBack = { navController.popBackStack() },
                    onBookAppointment = { navController.navigate(Routes.APPOINTMENT_CALENDAR) },
                    onChat = {
                        val convo = HealthDataRepository.conversationForDoctor(doctor)
                        navController.navigate(Routes.doctorChatRoute(convo.id))
                    },
                    onVideoConsult = { navController.navigate(Routes.VIDEO_CONSULTATION) }
                )
            }
        }

        composable(Routes.APPOINTMENT_CALENDAR) {
            val doctor = BookingFlowState.selectedDoctor.value
            if (doctor != null) {
                AppointmentCalendarScreen(
                    doctor = doctor,
                    onBack = { navController.popBackStack() },
                    onDateSelected = { date ->
                        BookingFlowState.selectedDate.value = date
                        navController.navigate(Routes.TIME_SLOT_SELECTION)
                    }
                )
            }
        }

        composable(Routes.TIME_SLOT_SELECTION) {
            val doctor = BookingFlowState.selectedDoctor.value
            val date = BookingFlowState.selectedDate.value
            if (doctor != null && date != null) {
                TimeSlotSelectionScreen(
                    doctor = doctor,
                    date = date,
                    initialConsultationType = BookingFlowState.consultationType.value,
                    onBack = { navController.popBackStack() },
                    onContinue = { time, type ->
                        BookingFlowState.selectedTime.value = time
                        BookingFlowState.consultationType.value = type
                        navController.navigate(Routes.APPOINTMENT_SUMMARY)
                    }
                )
            }
        }

        composable(Routes.APPOINTMENT_SUMMARY) {
            val doctor = BookingFlowState.selectedDoctor.value
            val date = BookingFlowState.selectedDate.value
            val time = BookingFlowState.selectedTime.value
            if (doctor != null && date != null && time != null) {
                AppointmentSummaryScreen(
                    doctor = doctor,
                    date = date,
                    time = time,
                    consultationType = BookingFlowState.consultationType.value,
                    patient = patient,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.popBackStack(Routes.TIME_SLOT_SELECTION, inclusive = false) },
                    onCancel = {
                        BookingFlowState.reset()
                        navController.popBackStack(Routes.DASHBOARD, inclusive = false)
                    },
                    onConfirm = { notes ->
                        BookingFlowState.symptomsNotes.value = notes
                        navController.navigate(Routes.PAYMENT)
                    }
                )
            }
        }

        composable(Routes.PAYMENT) {
            val doctor = BookingFlowState.selectedDoctor.value
            val date = BookingFlowState.selectedDate.value
            val time = BookingFlowState.selectedTime.value
            if (doctor != null && date != null && time != null) {
                val hospitalFee = 500.0
                val tax = doctor.consultationFee * 0.05
                val draft = Appointment(
                    id = AppointmentRepository.nextAppointmentId(),
                    patientId = patient?.id ?: "GUEST",
                    doctor = doctor,
                    date = date.toString(),
                    time = time,
                    consultationType = BookingFlowState.consultationType.value,
                    symptoms = BookingFlowState.symptomsNotes.value,
                    consultationFee = doctor.consultationFee,
                    hospitalFee = hospitalFee,
                    tax = tax,
                    paymentStatus = PaymentStatus.PENDING
                )
                PaymentScreen(
                    draftAppointment = draft,
                    onBack = { navController.popBackStack() },
                    onCancelPayment = {
                        BookingFlowState.reset()
                        navController.popBackStack(Routes.DASHBOARD, inclusive = false)
                    },
                    onPaymentSuccess = { paid ->
                        SelectionState.selectedAppointment.value = paid
                        BookingFlowState.reset()
                        navController.navigate(Routes.APPOINTMENT_CONFIRMATION) {
                            popUpTo(Routes.FIND_DOCTOR) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.APPOINTMENT_CONFIRMATION) {
            val appointment = SelectionState.selectedAppointment.value
            if (appointment != null) {
                AppointmentConfirmationScreen(
                    appointment = appointment,
                    onDownloadReceipt = { },
                    onShare = { },
                    onViewAppointment = { navController.navigate(Routes.APPOINTMENT_DETAILS) },
                    onReturnHome = { navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.DASHBOARD) { inclusive = true } } }
                )
            }
        }

        // ---- Phase 3: Appointments & records ----

        composable(Routes.UPCOMING_APPOINTMENTS) {
            val appointments = patient?.let { AppointmentRepository.appointmentsForPatient(it.id) } ?: emptyList()
            UpcomingAppointmentsScreen(
                appointments = appointments,
                onBack = { navController.popBackStack() },
                onOpenAppointment = { appt ->
                    SelectionState.selectedAppointment.value = appt
                    navController.navigate(Routes.APPOINTMENT_DETAILS)
                }
            )
        }

        composable(Routes.APPOINTMENT_DETAILS) {
            val appointment = SelectionState.selectedAppointment.value
            if (appointment != null) {
                AppointmentDetailsScreen(
                    appointment = appointment,
                    onBack = { navController.popBackStack() },
                    onReschedule = {
                        BookingFlowState.selectedDoctor.value = appointment.doctor
                        navController.navigate(Routes.APPOINTMENT_CALENDAR)
                    },
                    onCancelAppointment = {
                        AppointmentRepository.cancelAppointment(appointment.id)
                        navController.popBackStack()
                    },
                    onChatWithDoctor = {
                        val convo = HealthDataRepository.conversationForDoctor(appointment.doctor)
                        navController.navigate(Routes.doctorChatRoute(convo.id))
                    },
                    onStartVideo = {
                        BookingFlowState.selectedDoctor.value = appointment.doctor
                        navController.navigate(Routes.VIDEO_CONSULTATION)
                    },
                    onDownloadReceipt = { }
                )
            }
        }

        composable(Routes.MEDICAL_RECORDS) {
            MedicalRecordsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.LAB_REPORTS) {
            LabReportsScreen(
                onBack = { navController.popBackStack() },
                onOpenReport = { report ->
                    SelectionState.selectedLabReport.value = report
                    navController.navigate(Routes.LAB_REPORT_VIEWER)
                }
            )
        }

        composable(Routes.LAB_REPORT_VIEWER) {
            val report = SelectionState.selectedLabReport.value
            if (report != null) {
                LabReportViewerScreen(report = report, onBack = { navController.popBackStack() })
            }
        }

        composable(Routes.DIGITAL_PRESCRIPTION) {
            DigitalPrescriptionScreen(onBack = { navController.popBackStack() })
        }

        // ---- Phase 4: Advanced health modules ----

        composable(Routes.MEDICATION_REMINDER) {
            MedicationReminderScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.HEALTH_DASHBOARD) {
            HealthDashboardScreen(
                onBack = { navController.popBackStack() },
                onAddReading = { navController.navigate(Routes.ADD_HEALTH_READING) },
                onViewInsights = { navController.navigate(Routes.HEALTH_INSIGHTS) }
            )
        }

        composable(Routes.ADD_HEALTH_READING) {
            AddHealthReadingScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.EMERGENCY_SERVICES) {
            EmergencyServicesScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DOCTOR_CHAT_LIST) {
            DoctorChatListScreen(
                onBack = { navController.popBackStack() },
                onOpenConversation = { convo -> navController.navigate(Routes.doctorChatRoute(convo.id)) }
            )
        }

        composable(
            route = Routes.DOCTOR_CHAT,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            DoctorChatScreen(conversationId = conversationId, onBack = { navController.popBackStack() })
        }

        composable(Routes.VIDEO_CONSULTATION) {
            val doctor = BookingFlowState.selectedDoctor.value
            if (doctor != null) {
                VideoConsultationScreen(
                    doctor = doctor,
                    onEndCall = { navController.popBackStack() },
                    onOpenChat = {
                        val convo = HealthDataRepository.conversationForDoctor(doctor)
                        navController.navigate(Routes.doctorChatRoute(convo.id))
                    }
                )
            }
        }

        composable(Routes.DOCUMENT_CENTER) {
            DocumentCenterScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.INSURANCE_INFO) {
            InsuranceInfoScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.HEALTH_INSIGHTS) {
            HealthInsightsScreen(onBack = { navController.popBackStack() })
        }

        // ---- Phase 5: Profile & settings ----

        composable(Routes.PROFILE) {
            var showLogout by remember { mutableStateOf(false) }
            ProfileScreen(
                patient = patient,
                onBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                onChangePassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onViewInsurance = { navController.navigate(Routes.INSURANCE_INFO) },
                onViewDocuments = { navController.navigate(Routes.DOCUMENT_CENTER) },
                onHelpSupport = { navController.navigate(Routes.HELP_SUPPORT) },
                onLogout = { showLogout = true }
            )
            if (showLogout) {
                LogoutDialog(
                    onConfirm = {
                        showLogout = false
                        Session.signOut()
                        BookingFlowState.reset()
                        navController.navigate(Routes.WELCOME) { popUpTo(Routes.DASHBOARD) { inclusive = true } }
                    },
                    onDismiss = { showLogout = false }
                )
            }
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                patient = patient,
                onBack = { navController.popBackStack() },
                onSaved = { updated ->
                    Session.updatePatient(updated)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            var showLogout by remember { mutableStateOf(false) }
            SettingsScreen(
                themeMode = themeMode,
                onThemeModeChange = { Session.themeMode.value = it },
                onBack = { navController.popBackStack() },
                onPrivacyPolicy = { },
                onTerms = { },
                onAbout = { navController.navigate(Routes.ABOUT) },
                onLogout = { showLogout = true }
            )
            if (showLogout) {
                LogoutDialog(
                    onConfirm = {
                        showLogout = false
                        Session.signOut()
                        BookingFlowState.reset()
                        navController.navigate(Routes.WELCOME) { popUpTo(Routes.DASHBOARD) { inclusive = true } }
                    },
                    onDismiss = { showLogout = false }
                )
            }
        }

        composable(Routes.HELP_SUPPORT) {
            HelpSupportScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}

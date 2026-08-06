package com.veroflow.verohealth.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.model.MedicationStatus
import com.veroflow.verohealth.data.model.Patient
import com.veroflow.verohealth.data.repository.HealthDataRepository

enum class DashboardTab { HOME, APPOINTMENTS, RECORDS, MESSAGES, PROFILE }

/** Screen 7 — Dashboard (full version). */
@Composable
fun DashboardScreen(
    patient: Patient?,
    upcomingAppointments: List<Appointment>,
    onFindDoctor: () -> Unit,
    onUpcomingAppointments: () -> Unit,
    onMedicalRecords: () -> Unit,
    onLabReports: () -> Unit,
    onPrescriptions: () -> Unit,
    onEmergency: () -> Unit,
    onNotifications: () -> Unit,
    onMessages: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onHealthDashboard: () -> Unit,
    onMedicationReminder: () -> Unit,
    unreadNotificationCount: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VeroHealth") },
                actions = {
                    IconButton(onClick = onNotifications) {
                        BadgedBox(badge = {
                            if (unreadNotificationCount > 0) Badge { Text("$unreadNotificationCount") }
                        }) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }, label = { Text("Home") })
                NavigationBarItem(selected = false, onClick = onUpcomingAppointments, icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Appointments") }, label = { Text("Appointments") })
                NavigationBarItem(selected = false, onClick = onMedicalRecords, icon = { Icon(Icons.Filled.Description, contentDescription = "Records") }, label = { Text("Records") })
                NavigationBarItem(selected = false, onClick = onMessages, icon = { Icon(Icons.Filled.Chat, contentDescription = "Messages") }, label = { Text("Messages") })
                NavigationBarItem(selected = false, onClick = onProfile, icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") }, label = { Text("Profile") })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Hello${patient?.let { ", ${it.firstName}" } ?: ""} \uD83D\uDC4B", style = MaterialTheme.typography.headlineMedium)
                Text("Here's your health overview for today", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
            }

            item {
                val nextAppt = upcomingAppointments.firstOrNull()
                Card(onClick = onUpcomingAppointments, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Upcoming Appointment", style = MaterialTheme.typography.titleMedium)
                        if (nextAppt != null) {
                            Text("${nextAppt.doctor.name} · ${nextAppt.date} at ${nextAppt.time}", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("No upcoming appointments — book one now.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            item {
                val dueMed = HealthDataRepository.medications.firstOrNull {
                    it.status == MedicationStatus.DUE_SOON || it.status == MedicationStatus.MISSED
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Medication Reminder", style = MaterialTheme.typography.titleMedium)
                        if (dueMed != null) {
                            Text("${dueMed.medicineName} — ${dueMed.status.name.lowercase().replace('_', ' ')}", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("All medications on track.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(quickActions(onFindDoctor, onUpcomingAppointments, onMedicalRecords, onLabReports, onPrescriptions, onEmergency, onHealthDashboard, onMedicationReminder)) { action ->
                        QuickActionCard(action)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("Health Tips", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(healthTips) { tip ->
                        Card(modifier = Modifier.padding(bottom = 24.dp)) {
                            Text(tip, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

private data class QuickAction(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String, val onClick: () -> Unit)

private fun quickActions(
    onFindDoctor: () -> Unit,
    onAppointments: () -> Unit,
    onRecords: () -> Unit,
    onLabs: () -> Unit,
    onPrescriptions: () -> Unit,
    onEmergency: () -> Unit,
    onHealthDashboard: () -> Unit,
    onMedicationReminder: () -> Unit
) = listOf(
    QuickAction(Icons.Filled.Search, "Find Doctor", onFindDoctor),
    QuickAction(Icons.Filled.CalendarMonth, "Book Appointment", onAppointments),
    QuickAction(Icons.Filled.Description, "Medical Records", onRecords),
    QuickAction(Icons.Filled.MedicalServices, "Lab Reports", onLabs),
    QuickAction(Icons.Filled.Medication, "Prescriptions", onPrescriptions),
    QuickAction(Icons.Filled.MonitorHeart, "Health Dashboard", onHealthDashboard),
    QuickAction(Icons.Filled.Medication, "Medication Reminder", onMedicationReminder),
    QuickAction(Icons.Filled.Emergency, "Emergency", onEmergency)
)

private val healthTips = listOf(
    "Drink at least 8 glasses of water daily.",
    "Aim for 7-8 hours of sleep each night.",
    "Take a short walk after meals to aid digestion."
)

@Composable
private fun QuickActionCard(action: QuickAction) {
    Card(onClick = action.onClick) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(action.icon, contentDescription = action.label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(action.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

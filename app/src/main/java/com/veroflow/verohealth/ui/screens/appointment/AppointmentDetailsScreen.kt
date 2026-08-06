package com.veroflow.verohealth.ui.screens.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.model.AppointmentStatus

/** Screen 16 — Appointment Details. */
@Composable
fun AppointmentDetailsScreen(
    appointment: Appointment,
    onBack: () -> Unit,
    onReschedule: () -> Unit,
    onCancelAppointment: () -> Unit,
    onChatWithDoctor: () -> Unit,
    onStartVideo: () -> Unit,
    onDownloadReceipt: () -> Unit
) {
    var showCancelConfirm by remember { mutableStateOf(false) }
    val isActionable = appointment.status == AppointmentStatus.UPCOMING

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                Text("ID: ${appointment.id}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text(appointment.doctor.name, style = MaterialTheme.typography.titleLarge)
                Text(appointment.doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
                Text(appointment.doctor.specialty.label, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Text("${appointment.consultationType.label}", style = MaterialTheme.typography.bodyMedium)
                Text("${appointment.date} at ${appointment.time}", style = MaterialTheme.typography.bodyMedium)
                Text("Payment: ${appointment.paymentStatus.name}", style = MaterialTheme.typography.bodyMedium)
                Text("Status: ${appointment.status.name}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                if (appointment.symptoms.isNotBlank()) {
                    Text("Symptoms", style = MaterialTheme.typography.titleMedium)
                    Text(appointment.symptoms, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(16.dp))
                if (isActionable) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onChatWithDoctor, modifier = Modifier.weight(1f)) { Text("Chat") }
                        OutlinedButton(onClick = onStartVideo, modifier = Modifier.weight(1f)) { Text("Video") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onReschedule, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Reschedule") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { showCancelConfirm = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancel Appointment")
                    }
                } else {
                    Text(
                        "This appointment is ${appointment.status.name.lowercase()} and cannot be modified.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onDownloadReceipt, modifier = Modifier.fillMaxWidth()) { Text("Download Receipt") }
            }
        }
    }

    if (showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            title = { Text("Cancel Appointment?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelConfirm = false
                    onCancelAppointment()
                }) { Text("Yes, Cancel") }
            },
            dismissButton = { TextButton(onClick = { showCancelConfirm = false }) { Text("No") } }
        )
    }
}

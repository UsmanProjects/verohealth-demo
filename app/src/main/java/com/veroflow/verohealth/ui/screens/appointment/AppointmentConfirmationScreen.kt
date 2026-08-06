package com.veroflow.verohealth.ui.screens.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Appointment
import kotlinx.coroutines.launch

/** Screen 14 — Appointment Confirmation. */
@Composable
fun AppointmentConfirmationScreen(
    appointment: Appointment,
    onDownloadReceipt: () -> Unit,
    onShare: () -> Unit,
    onViewAppointment: () -> Unit,
    onReturnHome: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle, contentDescription = "Confirmed",
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("Appointment Confirmed", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("Appointment ID: ${appointment.id}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))

            Icon(
                Icons.Filled.QrCode2, contentDescription = "QR Code check-in",
                modifier = Modifier.size(140.dp)
            )
            Spacer(Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(appointment.doctor.name, style = MaterialTheme.typography.titleMedium)
                    Text(appointment.doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
                    Text("${appointment.date} at ${appointment.time}", style = MaterialTheme.typography.bodyMedium)
                    Text("Payment: ${appointment.paymentStatus.name}", style = MaterialTheme.typography.bodyMedium)
                    Text("Status: ${appointment.status.name}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onDownloadReceipt()
                    scope.launch { snackbarHostState.showSnackbar("Receipt downloaded") }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Download Receipt") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onShare, modifier = Modifier.fillMaxWidth()) { Text("Share Appointment") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onViewAppointment, modifier = Modifier.fillMaxWidth()) { Text("View Appointment") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onReturnHome, modifier = Modifier.fillMaxWidth()) { Text("Return Home") }
        }
    }
}

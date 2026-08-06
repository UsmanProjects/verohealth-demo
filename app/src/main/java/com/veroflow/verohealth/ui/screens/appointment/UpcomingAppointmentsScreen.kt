package com.veroflow.verohealth.ui.screens.appointment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.model.AppointmentStatus

/** Screen 15 — Upcoming Appointments. */
@Composable
fun UpcomingAppointmentsScreen(
    appointments: List<Appointment>,
    onBack: () -> Unit,
    onOpenAppointment: (Appointment) -> Unit
) {
    var filter by remember { mutableStateOf(AppointmentStatus.UPCOMING) }
    val filtered = appointments.filter { it.status == filter }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Appointments") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppointmentStatus.entries.forEach { status ->
                    FilterChip(
                        selected = filter == status,
                        onClick = { filter = status },
                        label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No ${filter.name.lowercase()} appointments.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered, key = { it.id }) { appt ->
                        Card(onClick = { onOpenAppointment(appt) }, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(appt.doctor.name, style = MaterialTheme.typography.titleMedium)
                                Text(appt.doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
                                Text(appt.doctor.specialty.label, style = MaterialTheme.typography.bodyMedium)
                                Text("${appt.date} · ${appt.time}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    appt.status.name,
                                    color = when (appt.status) {
                                        AppointmentStatus.UPCOMING -> MaterialTheme.colorScheme.primary
                                        AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.onSurfaceVariant
                                        AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

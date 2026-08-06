package com.veroflow.verohealth.ui.screens.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.MedicationReminder
import com.veroflow.verohealth.data.model.MedicationStatus
import com.veroflow.verohealth.data.repository.HealthDataRepository
import com.veroflow.verohealth.ui.theme.HealthAmber
import com.veroflow.verohealth.ui.theme.HealthGray
import com.veroflow.verohealth.ui.theme.HealthGreen
import com.veroflow.verohealth.ui.theme.HealthRed

/** Screen 21 — Medication Reminder. */
@Composable
fun MedicationReminderScreen(onBack: () -> Unit) {
    val medications = HealthDataRepository.medications

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medication Reminders") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        if (medications.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
                Text("No active medications.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(medications, key = { it.id }) { med ->
                    MedicationCard(med)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun statusColor(status: MedicationStatus) = when (status) {
    MedicationStatus.ACTIVE -> HealthGreen
    MedicationStatus.DUE_SOON -> HealthAmber
    MedicationStatus.MISSED -> HealthRed
    MedicationStatus.COMPLETED, MedicationStatus.PAUSED -> HealthGray
}

@Composable
private fun MedicationCard(med: MedicationReminder) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = statusColor(med.status), modifier = Modifier.size(10.dp)) {}
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(med.medicineName, style = MaterialTheme.typography.titleMedium)
                    Text("${med.dosage} · ${med.frequencyPerDay}x/day", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(
                    checked = med.remindersEnabled,
                    onCheckedChange = { HealthDataRepository.toggleMedicationReminder(med.id) }
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { med.completionPercent / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${med.remainingDays} day(s) remaining · ${med.completionPercent}% complete · Next: ${med.notificationTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (med.status != MedicationStatus.COMPLETED) {
                    Button(onClick = { HealthDataRepository.markMedicationTaken(med.id) }) {
                        Text("Mark as Taken")
                    }
                }
                Text(med.status.name, style = MaterialTheme.typography.labelLarge, color = statusColor(med.status),
                    modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}

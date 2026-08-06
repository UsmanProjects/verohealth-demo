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
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.ConsultationType
import com.veroflow.verohealth.data.model.Doctor
import com.veroflow.verohealth.data.repository.AppointmentRepository
import java.time.LocalDate

/** Screen 11 — Time Slot Selection. */
@Composable
fun TimeSlotSelectionScreen(
    doctor: Doctor,
    date: LocalDate,
    initialConsultationType: ConsultationType,
    onBack: () -> Unit,
    onContinue: (time: String, consultationType: ConsultationType) -> Unit
) {
    val slots = remember(doctor, date) { AppointmentRepository.slotsFor(doctor, date) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var consultationType by remember { mutableStateOf(initialConsultationType) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Time") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            Text(doctor.name, style = MaterialTheme.typography.titleMedium)
            Text(doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
            Text(date.toString(), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            Text("Consultation Type", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                ConsultationType.entries.forEach { type ->
                    FilterChip(
                        selected = consultationType == type,
                        onClick = { consultationType = type },
                        label = { Text(type.label) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                listOf("Morning", "Afternoon", "Evening").forEach { period ->
                    val periodSlots = slots.filter { it.period == period }
                    if (periodSlots.isNotEmpty()) {
                        item {
                            Text(period, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                periodSlots.forEach { slot ->
                                    FilterChip(
                                        selected = selectedTime == slot.time,
                                        enabled = slot.isAvailable,
                                        onClick = { selectedTime = slot.time },
                                        label = { Text(slot.time) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { selectedTime?.let { onContinue(it, consultationType) } },
                enabled = selectedTime != null,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

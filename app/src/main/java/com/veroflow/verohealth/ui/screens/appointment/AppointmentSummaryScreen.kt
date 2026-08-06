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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.veroflow.verohealth.data.model.Patient
import java.time.LocalDate
import java.time.Period

/** Screen 12 — Appointment Summary. */
@Composable
fun AppointmentSummaryScreen(
    doctor: Doctor,
    date: LocalDate,
    time: String,
    consultationType: ConsultationType,
    patient: Patient?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (notes: String) -> Unit
) {
    var agreePolicy by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    val hospitalFee = 500.0
    val tax = doctor.consultationFee * 0.05
    val discount = 0.0
    val total = doctor.consultationFee + hospitalFee + tax - discount

    val age = patient?.dateOfBirth?.let {
        runCatching { Period.between(LocalDate.parse(it), LocalDate.now()).years }.getOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Summary") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                SectionCard("Doctor") {
                    Text(doctor.name, style = MaterialTheme.typography.titleMedium)
                    Text(doctor.specialty.label, style = MaterialTheme.typography.bodyMedium)
                    Text(doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(12.dp))
                SectionCard("Appointment") {
                    Text("Date: $date", style = MaterialTheme.typography.bodyMedium)
                    Text("Time: $time", style = MaterialTheme.typography.bodyMedium)
                    Text("Type: ${consultationType.label}", style = MaterialTheme.typography.bodyMedium)
                    Text("Department: ${doctor.specialty.label}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(12.dp))
                SectionCard("Patient") {
                    Text("Name: ${patient?.fullName ?: "Guest"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Age: ${age ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Gender: ${patient?.gender?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(12.dp))
                SectionCard("Charges") {
                    ChargeRow("Consultation Fee", doctor.consultationFee)
                    ChargeRow("Hospital Fee", hospitalFee)
                    ChargeRow("Tax", tax)
                    if (discount > 0) ChargeRow("Discount", -discount)
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", style = MaterialTheme.typography.titleMedium)
                        Text("Rs. ${"%.0f".format(total)}", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Describe your symptoms") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Checkbox(checked = agreePolicy, onCheckedChange = { agreePolicy = it })
                    Text(
                        "I agree to the Appointment Policy.",
                        modifier = Modifier.height(48.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onConfirm(notes) },
                    enabled = agreePolicy,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Confirm Appointment") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit Appointment") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
private fun ChargeRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text("Rs. ${"%.0f".format(amount)}", style = MaterialTheme.typography.bodyMedium)
    }
}

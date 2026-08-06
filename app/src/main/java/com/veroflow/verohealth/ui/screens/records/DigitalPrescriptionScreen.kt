package com.veroflow.verohealth.ui.screens.records

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.mock.ClinicalMockData
import com.veroflow.verohealth.data.repository.HealthDataRepository
import kotlinx.coroutines.launch

/** Screen 20 — Digital Prescription. */
@Composable
fun DigitalPrescriptionScreen(onBack: () -> Unit) {
    val prescriptions = remember { ClinicalMockData.prescriptions }
    var expandedId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescriptions") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(prescriptions, key = { it.id }) { rx ->
                val expanded = expandedId == rx.id
                Card(
                    onClick = { expandedId = if (expanded) null else rx.id },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(rx.doctorName, style = MaterialTheme.typography.titleMedium)
                                Text(rx.date, style = MaterialTheme.typography.bodyMedium)
                                Text("${rx.medicines.size} medicine(s)", style = MaterialTheme.typography.bodyMedium)
                            }
                            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                        }
                        if (expanded) {
                            Spacer(Modifier.height(8.dp))
                            rx.medicines.forEach { med ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Text(med.name, style = MaterialTheme.typography.titleMedium)
                                    Text("${med.dosage} · ${med.frequency} · ${med.duration}", style = MaterialTheme.typography.bodyMedium)
                                    Text(med.instructions, style = MaterialTheme.typography.bodyMedium)
                                    OutlinedButton(onClick = {
                                        val days = med.duration.filter { it.isDigit() }.toIntOrNull() ?: 7
                                        HealthDataRepository.addMedicationFromPrescription(med.name, med.dosage, med.frequency, days)
                                        scope.launch { snackbarHostState.showSnackbar("Added to Medication Reminder") }
                                    }) { Text("Add to Medication Reminder") }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row {
                                OutlinedButton(onClick = {
                                    scope.launch { snackbarHostState.showSnackbar("Prescription downloaded") }
                                }) { Text("Download") }
                                OutlinedButton(onClick = {
                                    scope.launch { snackbarHostState.showSnackbar("Share sheet opened") }
                                }) { Text("Share") }
                                OutlinedButton(onClick = {
                                    scope.launch { snackbarHostState.showSnackbar("Order placed (mock)") }
                                }) { Text("Order Medicines") }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

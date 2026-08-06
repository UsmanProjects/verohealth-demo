package com.veroflow.verohealth.ui.screens.emergency

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.veroflow.verohealth.data.repository.HealthDataRepository

/** Screen 24 — Emergency Services. */
@Composable
fun EmergencyServicesScreen(onBack: () -> Unit) {
    var showCallSimulation by remember { mutableStateOf<String?>(null) }
    var showAddContact by remember { mutableStateOf(false) }
    val contacts = HealthDataRepository.emergencyContacts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Services") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EmergencyActionCard(Icons.Filled.LocalHospital, "Call Ambulance", Modifier.weight(1f)) {
                        showCallSimulation = "Ambulance"
                    }
                    EmergencyActionCard(Icons.Filled.MedicalServices, "Nearby Hospitals", Modifier.weight(1f)) {}
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Emergency Contacts", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showAddContact = true }) { Text("Add Contact") }
                }
            }
            items(contacts, key = { it.id }) { contact ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(contact.name, style = MaterialTheme.typography.titleMedium)
                            Text("${contact.relationship} · ${contact.phone}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row {
                            IconButton(onClick = { showCallSimulation = contact.name }) {
                                Icon(Icons.Filled.Call, contentDescription = "Call ${contact.name}", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { HealthDataRepository.deleteEmergencyContact(contact.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete contact")
                            }
                        }
                    }
                }
            }
            if (contacts.isEmpty()) {
                item {
                    Text(
                        "Add at least one emergency contact to enable full emergency features.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }

    showCallSimulation?.let { name ->
        AlertDialog(
            onDismissRequest = { showCallSimulation = null },
            title = { Text("Calling $name…") },
            text = { Text("This is a simulated call screen for the demo app.") },
            confirmButton = { TextButton(onClick = { showCallSimulation = null }) { Text("End Call") } }
        )
    }

    if (showAddContact) {
        var name by remember { mutableStateOf("") }
        var relationship by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddContact = false },
            title = { Text("Add Emergency Contact") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Contact Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = relationship, onValueChange = { relationship = it }, label = { Text("Relationship") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        HealthDataRepository.addEmergencyContact(name, relationship.ifBlank { "Contact" }, phone)
                        showAddContact = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddContact = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EmergencyActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

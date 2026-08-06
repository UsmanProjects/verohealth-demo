package com.veroflow.verohealth.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class FaqItem(val question: String, val answer: String)

private val faqs = listOf(
    FaqItem("How do I book an appointment?", "Go to Find Doctor, choose a doctor, select a date and time, then confirm and pay."),
    FaqItem("Can I cancel an appointment?", "Yes — open the appointment from Upcoming Appointments and choose Cancel Appointment."),
    FaqItem("Is my data shared with anyone?", "This is a fully offline demo app — no data leaves your device."),
    FaqItem("How do medication reminders work?", "Reminders are added from a prescription or manually, and can be paused, resumed, or deleted at any time.")
)

/** Screen 34 — Help & Support. */
@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }
    var reportText by remember { mutableStateOf("") }
    var showSubmitted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                Text("Frequently Asked Questions", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            itemsIndexed(faqs) { index, faq ->
                val expanded = expandedIndex == index
                Card(
                    onClick = { expandedIndex = if (expanded) null else index },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(faq.question, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                        }
                        if (expanded) {
                            Spacer(Modifier.height(6.dp))
                            Text(faq.answer, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text("Report an Issue", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reportText,
                    onValueChange = { reportText = it },
                    label = { Text("Describe the issue") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { if (reportText.isNotBlank()) showSubmitted = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Submit Report") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    scope.launch { snackbarHostState.showSnackbar("Thanks for your feedback!") }
                }, modifier = Modifier.fillMaxWidth()) { Text("Send Feedback") }
            }
        }
    }

    if (showSubmitted) {
        AlertDialog(
            onDismissRequest = { showSubmitted = false },
            title = { Text("Report Submitted") },
            text = { Text("Thanks — our support team will review this shortly.") },
            confirmButton = {
                TextButton(onClick = { showSubmitted = false; reportText = "" }) { Text("OK") }
            }
        )
    }
}


/** Screen 35 — About. */
@Composable
fun AboutScreen(onBack: () -> Unit) {
    var showLicenses by remember { mutableStateOf(false) }
    var showUpdateCheck by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.HealthAndSafety, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text("VeroHealth Demo", style = MaterialTheme.typography.titleLarge)
            Text("Version 1.0.0 (Build 1)", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text("Developer: VeroFlow Demo Apps", style = MaterialTheme.typography.bodyMedium)
            Text("License: For evaluation and testing purposes only", style = MaterialTheme.typography.bodyMedium)
            Text(
                "This app runs entirely offline using local mock data. No personal information is transmitted anywhere.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = { showUpdateCheck = true }, modifier = Modifier.fillMaxWidth()) { Text("Check Updates (Mock)") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { showLicenses = true }, modifier = Modifier.fillMaxWidth()) { Text("Open Licenses") }
        }
    }

    if (showUpdateCheck) {
        AlertDialog(
            onDismissRequest = { showUpdateCheck = false },
            title = { Text("You're up to date") },
            text = { Text("VeroHealth Demo 1.0.0 is the latest version.") },
            confirmButton = { TextButton(onClick = { showUpdateCheck = false }) { Text("OK") } }
        )
    }
    if (showLicenses) {
        AlertDialog(
            onDismissRequest = { showLicenses = false },
            title = { Text("Open Source Licenses") },
            text = { Text("Jetpack Compose, Material 3, AndroidX Navigation, Coil, Kotlin Coroutines — all under their respective open source licenses.") },
            confirmButton = { TextButton(onClick = { showLicenses = false }) { Text("Close") } }
        )
    }
}

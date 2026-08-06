package com.veroflow.verohealth.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Patient
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Screen 31 — Patient Profile. */
@Composable
fun ProfileScreen(
    patient: Patient?,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onViewInsurance: () -> Unit,
    onViewDocuments: () -> Unit,
    onHelpSupport: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Profile photo", modifier = Modifier.size(72.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(patient?.fullName ?: "Guest User", style = MaterialTheme.typography.titleLarge)
                        Text("Patient ID: ${patient?.id ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(20.dp))

                SectionCard("Personal Information") {
                    InfoLine("Date of Birth", patient?.dateOfBirth ?: "—")
                    InfoLine("Gender", patient?.gender?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "—")
                    InfoLine("Blood Group", patient?.bloodGroup?.label ?: "—")
                    InfoLine("Phone Number", patient?.phone ?: "—")
                    InfoLine("Email Address", patient?.email ?: "—")
                    InfoLine("Address", patient?.address?.ifBlank { "—" } ?: "—")
                }
                Spacer(Modifier.height(12.dp))

                SectionCard("Emergency Information") {
                    InfoLine("Emergency Contact", patient?.emergencyContactName?.ifBlank { "—" } ?: "—")
                    InfoLine("Allergies", patient?.allergies?.joinToString(", ")?.ifBlank { "None recorded" } ?: "None recorded")
                    InfoLine("Chronic Conditions", patient?.chronicConditions?.joinToString(", ")?.ifBlank { "None recorded" } ?: "None recorded")
                }
                Spacer(Modifier.height(12.dp))

                val regDate = patient?.registrationDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } ?: "—"
                SectionCard("Account Information") {
                    InfoLine("Registration Date", regDate)
                    InfoLine("Membership Status", if (patient != null) "Active" else "Guest")
                    InfoLine("Insurance Status", "See Insurance section")
                }
                Spacer(Modifier.height(20.dp))

                OutlinedButton(onClick = onEditProfile, modifier = Modifier.fillMaxWidth()) { Text("Edit Profile") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) { Text("Change Password") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onViewInsurance, modifier = Modifier.fillMaxWidth()) { Text("View Insurance") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onViewDocuments, modifier = Modifier.fillMaxWidth()) { Text("Medical Documents") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onHelpSupport, modifier = Modifier.fillMaxWidth()) { Text("Help & Support") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
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
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

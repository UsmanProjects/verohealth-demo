package com.veroflow.verohealth.ui.screens.doctor

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import com.veroflow.verohealth.data.mock.MockDataProvider
import com.veroflow.verohealth.data.model.Doctor
import com.veroflow.verohealth.data.model.Specialty

/** Screen 8 — Find Doctor: search bar + specialty/gender/availability filters. */
@Composable
fun FindDoctorScreen(
    onBack: () -> Unit,
    onDoctorSelected: (Doctor) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf<Specialty?>(null) }
    var onlyAvailableToday by remember { mutableStateOf(false) }

    val allDoctors = remember { MockDataProvider.doctors }

    val filtered = remember(query, selectedSpecialty, onlyAvailableToday) {
        allDoctors.filter { doctor ->
            val matchesQuery = query.isBlank() ||
                doctor.name.contains(query, ignoreCase = true) ||
                doctor.specialty.label.contains(query, ignoreCase = true) ||
                doctor.hospital.name.contains(query, ignoreCase = true)
            val matchesSpecialty = selectedSpecialty == null || doctor.specialty == selectedSpecialty
            val matchesAvailability = !onlyAvailableToday || doctor.isAvailableToday
            matchesQuery && matchesSpecialty && matchesAvailability
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Doctor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    placeholder = { Text("Search doctor, specialty, or hospital") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = onlyAvailableToday,
                            onClick = { onlyAvailableToday = !onlyAvailableToday },
                            label = { Text("Available Today") }
                        )
                    }
                    items(Specialty.entries) { specialty ->
                        FilterChip(
                            selected = selectedSpecialty == specialty,
                            onClick = {
                                selectedSpecialty = if (selectedSpecialty == specialty) null else specialty
                            },
                            label = { Text(specialty.label) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${filtered.size} doctors found",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (selectedSpecialty != null || onlyAvailableToday || query.isNotBlank()) {
                        TextButton(onClick = {
                            query = ""; selectedSpecialty = null; onlyAvailableToday = false
                        }) { Text("Clear Filters") }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No doctors match your search.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filtered, key = { it.id }) { doctor ->
                        DoctorCard(doctor) { onDoctorSelected(doctor) }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorCard(doctor: Doctor, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocalHospital,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doctor.name, style = MaterialTheme.typography.titleMedium)
                Text(doctor.specialty.label, style = MaterialTheme.typography.bodyMedium)
                Text(doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        " ${"%.1f".format(doctor.rating)} · ${doctor.experienceYears} yrs exp",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Rs. ${doctor.consultationFee.toInt()}", style = MaterialTheme.typography.titleMedium)
                if (doctor.isAvailableToday) {
                    Text(
                        "Available today",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

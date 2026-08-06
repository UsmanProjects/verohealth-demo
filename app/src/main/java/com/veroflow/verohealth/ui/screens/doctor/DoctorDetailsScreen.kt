package com.veroflow.verohealth.ui.screens.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Doctor

/** Screen 9 — Doctor Details. */
@Composable
fun DoctorDetailsScreen(
    doctor: Doctor,
    onBack: () -> Unit,
    onBookAppointment: () -> Unit,
    onChat: () -> Unit,
    onVideoConsult: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var reviewsExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(doctor.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Save Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* share profile - mock */ }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Profile")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocalHospital, contentDescription = null,
                        modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(doctor.name, style = MaterialTheme.typography.titleLarge)
                        Text(doctor.specialty.label, style = MaterialTheme.typography.bodyLarge)
                        Text(doctor.qualification, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoChip("${doctor.experienceYears} yrs experience")
                    InfoChip("Rs. ${doctor.consultationFee.toInt()} fee")
                }
                Spacer(Modifier.height(16.dp))
                Text(doctor.hospital.name, style = MaterialTheme.typography.titleMedium)
                Text(doctor.hospital.address, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        " ${"%.1f".format(doctor.rating)} (${doctor.reviewCount} reviews)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(16.dp))

                Text("About", style = MaterialTheme.typography.titleMedium)
                Text(doctor.biography, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Languages: ${doctor.languages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                Text("Weekly Availability", style = MaterialTheme.typography.titleMedium)
                doctor.weeklyAvailability.forEach {
                    Text("${it.dayOfWeek}: ${it.startTime} - ${it.endTime}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(16.dp))

                Card(
                    onClick = { reviewsExpanded = !reviewsExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            if (reviewsExpanded) "Hide Patient Reviews" else "Show Patient Reviews",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (reviewsExpanded) {
                            Spacer(Modifier.height(8.dp))
                            Text("\"Very attentive and explained everything clearly.\" — A. Malik", style = MaterialTheme.typography.bodyMedium)
                            Text("\"Short wait time, professional staff.\" — H. Raza", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onChat, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Chat")
                    }
                    OutlinedButton(onClick = onVideoConsult, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.VideoCall, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Video")
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = onBookAppointment, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Book Appointment")
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Card {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

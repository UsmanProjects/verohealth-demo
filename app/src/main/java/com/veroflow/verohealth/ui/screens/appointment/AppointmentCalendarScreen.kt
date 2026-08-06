package com.veroflow.verohealth.ui.screens.appointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Doctor
import com.veroflow.verohealth.data.repository.AppointmentRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/** Screen 10 — Appointment Calendar: month view, past/booked dates disabled. */
@Composable
fun AppointmentCalendarScreen(
    doctor: Doctor,
    onBack: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Date") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(doctor.name, style = MaterialTheme.typography.titleMedium)
            Text(doctor.hospital.name, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { visibleMonth = visibleMonth.minusMonths(1) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                }
                Text(
                    "${visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${visibleMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { visibleMonth = visibleMonth.plusMonths(1) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                }
            }
            TextButton(
                onClick = { visibleMonth = YearMonth.now() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Return to current month")
            }
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                LegendDot(MaterialTheme.colorScheme.primaryContainer, "Available")
                LegendDot(MaterialTheme.colorScheme.errorContainer, "Fully Booked")
                LegendDot(MaterialTheme.colorScheme.surfaceVariant, "Past")
            }
            Spacer(Modifier.height(12.dp))

            val firstDay = visibleMonth.atDay(1)
            val daysInMonth = visibleMonth.lengthOfMonth()
            val leadingBlanks = firstDay.dayOfWeek.value % 7 // Sun=0

            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                items(leadingBlanks) { Spacer(Modifier.aspectRatio(1f)) }
                items(daysInMonth) { dayIndex ->
                    val date = visibleMonth.atDay(dayIndex + 1)
                    val isPast = AppointmentRepository.isDateInPast(date)
                    val isFullyBooked = !isPast && AppointmentRepository.isDateFullyBooked(doctor, date)
                    val isSelectable = !isPast && !isFullyBooked

                    val bgColor = when {
                        isPast -> MaterialTheme.colorScheme.surfaceVariant
                        isFullyBooked -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }

                    Surface(
                        shape = CircleShape,
                        color = bgColor,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .let { m -> if (isSelectable) m.clickable { onDateSelected(date) } else m }
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("${date.dayOfMonth}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.height(10.dp).aspectRatio(1f)) {}
        Text(" $label", style = MaterialTheme.typography.bodyMedium)
    }
}

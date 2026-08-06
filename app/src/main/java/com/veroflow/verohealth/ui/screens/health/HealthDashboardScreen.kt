package com.veroflow.verohealth.ui.screens.health

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.HealthMetricType
import com.veroflow.verohealth.data.repository.HealthDataRepository

/** Screen 22 — Health Dashboard. */
@Composable
fun HealthDashboardScreen(
    onBack: () -> Unit,
    onAddReading: () -> Unit,
    onViewInsights: () -> Unit
) {
    val readings = HealthDataRepository.healthReadings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Dashboard") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(HealthMetricType.entries) { type ->
                    val latest = HealthDataRepository.latestReading(type)
                    MetricCard(type.label, latest?.value ?: "—", type.unit)
                }
            }

            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                item {
                    Text("Trends", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    val heartRateSeries = HealthDataRepository.readingsFor(HealthMetricType.HEART_RATE)
                        .mapNotNull { it.value.toFloatOrNull() }
                    TrendChartCard("Heart Rate (bpm)", heartRateSeries)
                    Spacer(Modifier.height(12.dp))
                }
                item {
                    val weightSeries = HealthDataRepository.readingsFor(HealthMetricType.WEIGHT)
                        .mapNotNull { it.value.toFloatOrNull() }
                    TrendChartCard("Weight (kg)", weightSeries)
                    Spacer(Modifier.height(16.dp))
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onAddReading, modifier = Modifier.weight(1f)) { Text("Add New Reading") }
                        OutlinedButton(onClick = onViewInsights, modifier = Modifier.weight(1f)) { Text("View Insights") }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, unit: String) {
    Card {
        Column(modifier = Modifier.padding(16.dp).height(90.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("$value $unit", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TrendChartCard(title: String, values: List<Float>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (values.size < 2) {
                Text("Not enough data yet — add more readings to see a trend.", style = MaterialTheme.typography.bodyMedium)
            } else {
                val primaryColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    val max = values.max()
                    val min = values.min()
                    val range = (max - min).takeIf { it > 0f } ?: 1f
                    val stepX = size.width / (values.size - 1)
                    val points = values.mapIndexed { index, v ->
                        Offset(index * stepX, size.height - ((v - min) / range) * size.height)
                    }
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = primaryColor,
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}

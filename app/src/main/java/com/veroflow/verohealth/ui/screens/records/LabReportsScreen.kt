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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
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
import com.veroflow.verohealth.data.mock.ClinicalMockData
import com.veroflow.verohealth.data.model.LabCategory
import com.veroflow.verohealth.data.model.LabReport
import com.veroflow.verohealth.data.model.ReportStatus

/** Screen 18 — Laboratory Reports. */
@Composable
fun LabReportsScreen(
    onBack: () -> Unit,
    onOpenReport: (LabReport) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<LabCategory?>(null) }
    val allReports = remember { ClinicalMockData.labReports }
    val filtered = allReports.filter { selectedCategory == null || it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laboratory Reports") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LabCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category },
                        label = { Text(category.label) }
                    )
                }
            }

            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                items(filtered, key = { it.id }) { report ->
                    Card(onClick = { onOpenReport(report) }, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(report.testName, style = MaterialTheme.typography.titleMedium)
                                Text("${report.date} · ${report.doctorName}", style = MaterialTheme.typography.bodyMedium)
                                Text(report.laboratoryName, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                report.status.name,
                                color = when (report.status) {
                                    ReportStatus.READY -> MaterialTheme.colorScheme.primary
                                    ReportStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                                    ReportStatus.REVIEWED -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

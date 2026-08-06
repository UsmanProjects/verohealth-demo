package com.veroflow.verohealth.ui.screens.records

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.LabReport
import kotlinx.coroutines.launch

/** Screen 19 — Laboratory Report Viewer (mock PDF-style reader). */
@Composable
fun LabReportViewerScreen(
    report: LabReport,
    onBack: () -> Unit
) {
    var zoom by remember { mutableStateOf(1.0f) }
    var searchQuery by remember { mutableStateOf("") }
    var bookmarked by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val highlightedResults = report.results.filter {
        searchQuery.isBlank() || it.testName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(report.testName) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = { bookmarked = !bookmarked }) {
                        Icon(
                            if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = { zoom = (zoom - 0.1f).coerceAtLeast(0.6f) }) {
                        Icon(Icons.Filled.ZoomOut, contentDescription = "Zoom out")
                    }
                    Text("${(zoom * 100).toInt()}%", modifier = Modifier.height(48.dp))
                    IconButton(onClick = { zoom = (zoom + 0.1f).coerceAtMost(2.0f) }) {
                        Icon(Icons.Filled.ZoomIn, contentDescription = "Zoom in")
                    }
                }
                Text("Page 1 of 1", style = MaterialTheme.typography.bodyMedium)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Search within report") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding((16 * zoom).dp)) {
                    Text("Patient: (see profile)", style = MaterialTheme.typography.bodyMedium)
                    Text("Report Date: ${report.date}", style = MaterialTheme.typography.bodyMedium)
                    Text("Laboratory: ${report.laboratoryName}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Test Results", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    LazyColumn {
                        items(highlightedResults) { row ->
                            val isHighlighted = searchQuery.isNotBlank() &&
                                row.testName.contains(searchQuery, ignoreCase = true)
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    row.testName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isHighlighted) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Text("Result: ${row.result}  ·  Reference: ${row.referenceRange}  ·  ${row.flag}",
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    if (report.doctorRemarks.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Doctor Remarks", style = MaterialTheme.typography.titleMedium)
                        Text(report.doctorRemarks, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    scope.launch { snackbarHostState.showSnackbar("PDF downloaded") }
                }, modifier = Modifier.weight(1f)) { Text("Download PDF") }
                OutlinedButton(onClick = {
                    scope.launch { snackbarHostState.showSnackbar("Share sheet opened") }
                }, modifier = Modifier.weight(1f)) { Text("Share") }
                OutlinedButton(onClick = {
                    scope.launch { snackbarHostState.showSnackbar("Sent to printer (mock)") }
                }, modifier = Modifier.weight(1f)) { Text("Print") }
            }
        }
    }
}

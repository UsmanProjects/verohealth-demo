package com.veroflow.verohealth.ui.screens.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.DocumentCategory
import com.veroflow.verohealth.data.repository.HealthDataRepository
import kotlinx.coroutines.launch

/** Screen 27 — Medical Document Center. */
@Composable
fun DocumentCenterScreen(onBack: () -> Unit) {
    val documents = HealthDataRepository.documents
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val totalSizeKb = documents.sumOf { it.sizeKb }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Documents") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    HealthDataRepository.addDocument("Scan_${(1000..9999).random()}.pdf", DocumentCategory.OTHER)
                    scope.launch { snackbarHostState.showSnackbar("Document uploaded") }
                },
                icon = { Icon(Icons.Filled.UploadFile, contentDescription = null) },
                text = { Text("Upload") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Storage used: $totalSizeKb KB",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(documents, key = { it.id }) { doc ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Description, contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(doc.fileName, style = MaterialTheme.typography.titleMedium)
                                    Text("${doc.category.label} · ${doc.uploadDate} · ${doc.sizeKb} KB", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Row {
                                IconButton(onClick = {
                                    scope.launch { snackbarHostState.showSnackbar("Share sheet opened") }
                                }) { Icon(Icons.Filled.Share, contentDescription = "Share") }
                                IconButton(onClick = { HealthDataRepository.deleteDocument(doc.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.padding(40.dp)) }
            }
        }
    }
}

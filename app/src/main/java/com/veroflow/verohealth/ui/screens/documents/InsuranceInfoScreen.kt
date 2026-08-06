package com.veroflow.verohealth.ui.screens.documents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.veroflow.verohealth.data.model.InsuranceInfo
import com.veroflow.verohealth.data.repository.HealthDataRepository
import com.veroflow.verohealth.data.repository.Validators
import java.time.LocalDate

/** Screen 28 — Insurance Information. */
@Composable
fun InsuranceInfoScreen(onBack: () -> Unit) {
    val current = HealthDataRepository.insurance.firstOrNull()
    var editing by remember { mutableStateOf(current == null) }

    var provider by remember { mutableStateOf(current?.provider ?: "") }
    var policyNumber by remember { mutableStateOf(current?.policyNumber ?: "") }
    var coverage by remember { mutableStateOf(current?.coverage ?: "") }
    var expiration by remember { mutableStateOf(current?.expirationDate ?: "") }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    val isExpired = current?.expirationDate?.let {
        runCatching { LocalDate.parse(it).isBefore(LocalDate.now()) }.getOrDefault(false)
    } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insurance Information") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            if (!editing && current != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(current.provider, style = MaterialTheme.typography.titleLarge)
                        Text("Policy Number: ${current.policyNumber}", style = MaterialTheme.typography.bodyMedium)
                        Text("Coverage: ${current.coverage}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Expires: ${current.expirationDate}${if (isExpired) " (Expired)" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { editing = true }, modifier = Modifier.fillMaxWidth()) { Text("Edit") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    HealthDataRepository.insurance.clear()
                }, modifier = Modifier.fillMaxWidth()) { Text("Delete") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { /* upload card - mock */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Upload Insurance Card")
                }
            } else {
                OutlinedTextField(value = provider, onValueChange = { provider = it }, label = { Text("Provider") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = policyNumber, onValueChange = { policyNumber = it }, label = { Text("Policy Number") },
                    isError = errors["policy"] != null, supportingText = { errors["policy"]?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = coverage, onValueChange = { coverage = it }, label = { Text("Coverage") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = expiration, onValueChange = { expiration = it }, label = { Text("Expiration Date (YYYY-MM-DD)") },
                    isError = errors["expiry"] != null, supportingText = { errors["expiry"]?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val map = mutableMapOf<String, String>()
                        Validators.requiredError(policyNumber, "Policy number")?.let { map["policy"] = it }
                        Validators.requiredError(expiration, "Expiration date")?.let { map["expiry"] = it }
                        errors = map
                        if (map.isEmpty()) {
                            HealthDataRepository.insurance.clear()
                            HealthDataRepository.insurance.add(InsuranceInfo(provider, policyNumber, coverage, expiration))
                            editing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Save") }
            }
        }
    }
}

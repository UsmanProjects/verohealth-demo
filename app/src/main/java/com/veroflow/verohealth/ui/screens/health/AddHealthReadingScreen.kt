package com.veroflow.verohealth.ui.screens.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.HealthMetricType
import com.veroflow.verohealth.data.model.HealthReading
import com.veroflow.verohealth.data.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Screen 23 — Add Health Reading. */
@Composable
fun AddHealthReadingScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    var heartRate by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var bloodSugar by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var oxygen by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun validateAndSave() {
        val fields = listOf(heartRate, bloodSugar, weight, temperature, oxygen)
        if (fields.all { it.isBlank() } && bloodPressure.isBlank()) {
            error = "Enter at least one reading"
            return
        }
        if (heartRate.isNotBlank() && heartRate.toIntOrNull() == null) { error = "Heart rate must be numeric"; return }
        if (bloodSugar.isNotBlank() && bloodSugar.toIntOrNull() == null) { error = "Blood sugar must be numeric"; return }
        if (weight.isNotBlank() && weight.toFloatOrNull() == null) { error = "Weight must be numeric"; return }
        if (temperature.isNotBlank() && temperature.toFloatOrNull() == null) { error = "Temperature must be numeric"; return }
        if (oxygen.isNotBlank() && oxygen.toIntOrNull() == null) { error = "Oxygen level must be numeric"; return }
        if (bloodPressure.isNotBlank() && !bloodPressure.matches(Regex("^[0-9]{2,3}/[0-9]{2,3}$"))) {
            error = "Blood pressure must be in the format 120/80"; return
        }
        error = null

        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        fun add(type: HealthMetricType, value: String) {
            if (value.isNotBlank()) {
                HealthDataRepository.addHealthReading(
                    HealthReading(id = "HR-${(10000..99999).random()}", type = type, value = value, dateTime = now, notes = notes)
                )
            }
        }
        add(HealthMetricType.HEART_RATE, heartRate)
        add(HealthMetricType.BLOOD_PRESSURE, bloodPressure)
        add(HealthMetricType.BLOOD_SUGAR, bloodSugar)
        add(HealthMetricType.WEIGHT, weight)
        add(HealthMetricType.TEMPERATURE, temperature)
        add(HealthMetricType.OXYGEN, oxygen)

        scope.launch { snackbarHostState.showSnackbar("Reading saved") }
        onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Health Reading") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                OutlinedTextField(value = bloodPressure, onValueChange = { bloodPressure = it },
                    label = { Text("Blood Pressure (e.g. 120/80)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = heartRate, onValueChange = { heartRate = it },
                    label = { Text("Heart Rate (bpm)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = bloodSugar, onValueChange = { bloodSugar = it },
                    label = { Text("Blood Sugar (mg/dL)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = weight, onValueChange = { weight = it },
                    label = { Text("Weight (kg)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = temperature, onValueChange = { temperature = it },
                    label = { Text("Temperature (°F)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = oxygen, onValueChange = { oxygen = it },
                    label = { Text("Oxygen Saturation (%)") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it },
                    label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(20.dp))
                Button(onClick = { validateAndSave() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text("Save Reading")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    heartRate = ""; bloodPressure = ""; bloodSugar = ""; weight = ""; temperature = ""; oxygen = ""; notes = ""
                }, modifier = Modifier.fillMaxWidth()) { Text("Clear Form") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
            }
        }
    }
}

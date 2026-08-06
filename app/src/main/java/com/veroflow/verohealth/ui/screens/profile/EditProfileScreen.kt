package com.veroflow.verohealth.ui.screens.profile

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
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Patient
import com.veroflow.verohealth.data.repository.Validators
import kotlinx.coroutines.launch

/** Screen 32 — Edit Profile. */
@Composable
fun EditProfileScreen(
    patient: Patient?,
    onBack: () -> Unit,
    onSaved: (updated: Patient) -> Unit
) {
    var phone by remember { mutableStateOf(patient?.phone ?: "") }
    var email by remember { mutableStateOf(patient?.email ?: "") }
    var address by remember { mutableStateOf(patient?.address ?: "") }
    var emergencyContact by remember { mutableStateOf(patient?.emergencyContactName ?: "") }
    var allergies by remember { mutableStateOf(patient?.allergies?.joinToString(", ") ?: "") }
    var conditions by remember { mutableStateOf(patient?.chronicConditions?.joinToString(", ") ?: "") }

    var errors by remember { mutableStateOf(mapOf<String, String>()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") },
                    isError = errors["phone"] != null, supportingText = { errors["phone"]?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = email, onValueChange = { email = it }, label = { Text("Email") },
                    isError = errors["email"] != null, supportingText = { errors["email"]?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = emergencyContact, onValueChange = { emergencyContact = it }, label = { Text("Emergency Contact") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("Allergies (comma separated)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = conditions, onValueChange = { conditions = it }, label = { Text("Medical Conditions (comma separated)") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        val map = mutableMapOf<String, String>()
                        Validators.phoneError(phone)?.let { map["phone"] = it }
                        Validators.emailError(email)?.let { map["email"] = it }
                        errors = map
                        if (map.isEmpty() && patient != null) {
                            val updated = patient.copy(
                                phone = phone,
                                email = email,
                                address = address,
                                emergencyContactName = emergencyContact,
                                allergies = allergies.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                chronicConditions = conditions.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            )
                            scope.launch { snackbarHostState.showSnackbar("Profile updated") }
                            onSaved(updated)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Save") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        phone = patient?.phone ?: ""; email = patient?.email ?: ""; address = patient?.address ?: ""
                        emergencyContact = patient?.emergencyContactName ?: ""
                        allergies = patient?.allergies?.joinToString(", ") ?: ""
                        conditions = patient?.chronicConditions?.joinToString(", ") ?: ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Reset Changes") }
            }
        }
    }
}

package com.veroflow.verohealth.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.BloodGroup
import com.veroflow.verohealth.data.model.Gender
import com.veroflow.verohealth.data.repository.AuthRepository
import com.veroflow.verohealth.data.repository.AuthResult
import com.veroflow.verohealth.data.repository.Validators
import kotlinx.coroutines.launch

/**
 * Screen 4 — Patient Registration.
 * Implements every validation rule from the spec: empty fields, invalid email,
 * invalid phone, weak password, password mismatch, missing consent, invalid DOB.
 */
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.FEMALE) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Pakistan") }
    var bloodGroup by remember { mutableStateOf(BloodGroup.O_POS) }
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var receiveTips by remember { mutableStateOf(false) }

    var errors by remember { mutableStateOf(mapOf<String, String>()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun clearForm() {
        firstName = ""; lastName = ""; dob = ""; email = ""; phone = ""
        nationalId = ""; password = ""; confirmPassword = ""
        agreeTerms = false; agreePrivacy = false; receiveTips = false
        errors = emptyMap()
        serverError = null
    }

    fun validate(): Boolean {
        val map = mutableMapOf<String, String>()
        Validators.requiredError(firstName, "First name")?.let { map["firstName"] = it }
        Validators.requiredError(lastName, "Last name")?.let { map["lastName"] = it }
        Validators.dateOfBirthError(dob)?.let { map["dob"] = it }
        Validators.emailError(email)?.let { map["email"] = it }
        Validators.phoneError(phone)?.let { map["phone"] = it }
        Validators.requiredError(nationalId, "National ID")?.let { map["nationalId"] = it }
        Validators.passwordError(password)?.let { map["password"] = it }
        Validators.confirmPasswordError(password, confirmPassword)?.let { map["confirm"] = it }
        if (!agreeTerms || !agreePrivacy) {
            map["consent"] = "You must accept the Terms and Privacy Policy to continue"
        }
        errors = map
        return map.isEmpty()
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            Text("Create your account", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Register to book appointments and manage your health records",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
        }

        item {
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                label = { Text("First Name") },
                isError = errors["firstName"] != null,
                supportingText = { errors["firstName"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                label = { Text("Last Name") },
                isError = errors["lastName"] != null,
                supportingText = { errors["lastName"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = dob, onValueChange = { dob = it },
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                isError = errors["dob"] != null,
                supportingText = { errors["dob"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            GenderSelector(gender) { gender = it }
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email Address") },
                isError = errors["email"] != null,
                supportingText = { errors["email"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Phone Number") },
                isError = errors["phone"] != null,
                supportingText = { errors["phone"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = nationalId, onValueChange = { nationalId = it },
                label = { Text("National ID (mock)") },
                isError = errors["nationalId"] != null,
                supportingText = { errors["nationalId"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            CountrySelector(country) { country = it }
            Spacer(Modifier.height(12.dp))
        }
        item {
            BloodGroupSelector(bloodGroup) { bloodGroup = it }
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = errors["password"] != null,
                supportingText = { errors["password"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
        }
        item {
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = errors["confirm"] != null,
                supportingText = { errors["confirm"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            Row {
                Checkbox(checked = agreeTerms, onCheckedChange = { agreeTerms = it })
                Text(
                    "I agree to the Terms & Conditions",
                    modifier = Modifier.height(48.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        item {
            Row {
                Checkbox(checked = agreePrivacy, onCheckedChange = { agreePrivacy = it })
                Text(
                    "I agree to the Privacy Policy",
                    modifier = Modifier.height(48.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        item {
            Row {
                Checkbox(checked = receiveTips, onCheckedChange = { receiveTips = it })
                Text(
                    "Receive health tips",
                    modifier = Modifier.height(48.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            errors["consent"]?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            serverError?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Button(
                onClick = {
                    serverError = null
                    if (validate()) {
                        isSubmitting = true
                        scope.launch {
                            val result = AuthRepository.register(
                                firstName, lastName, dob, gender, email, phone,
                                nationalId, password, country, bloodGroup, receiveTips
                            )
                            isSubmitting = false
                            when (result) {
                                is AuthResult.Success -> showSuccessDialog = true
                                is AuthResult.Failure -> serverError = result.message
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Register")
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { clearForm() }, modifier = Modifier.fillMaxWidth()) {
                Text("Clear Form")
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Already have an account? Login")
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Registration Successful") },
            text = { Text("Your VeroHealth account has been created. Please log in to continue.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    clearForm()
                    onRegistered()
                }) { Text("Continue to Login") }
            }
        )
    }
}

@Composable
private fun GenderSelector(selected: Gender, onSelect: (Gender) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Gender.entries.forEach { g ->
            Row {
                androidx.compose.material3.RadioButton(
                    selected = selected == g,
                    onClick = { onSelect(g) }
                )
                Text(
                    g.name.lowercase().replaceFirstChar { it.uppercase() },
                    modifier = Modifier.height(48.dp)
                )
            }
        }
    }
}

@Composable
private fun CountrySelector(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Pakistan", "United States", "United Kingdom", "United Arab Emirates", "Canada")
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Country") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}

@Composable
private fun BloodGroupSelector(selected: BloodGroup, onSelect: (BloodGroup) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Blood Group") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BloodGroup.entries.forEach { bg ->
                DropdownMenuItem(text = { Text(bg.label) }, onClick = { onSelect(bg); expanded = false })
            }
        }
    }
}

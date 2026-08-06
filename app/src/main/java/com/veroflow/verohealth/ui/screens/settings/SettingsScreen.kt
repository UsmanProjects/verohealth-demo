package com.veroflow.verohealth.ui.screens.settings

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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.ui.theme.ThemeMode

/** Screen 33 — Settings. */
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit
) {
    var language by remember { mutableStateOf("English") }
    var largeText by remember { mutableStateOf(false) }
    var highContrast by remember { mutableStateOf(false) }
    var reduceAnimations by remember { mutableStateOf(false) }

    var appointmentNotifs by remember { mutableStateOf(true) }
    var medicationNotifs by remember { mutableStateOf(true) }
    var healthTipsNotifs by remember { mutableStateOf(true) }
    var labReportNotifs by remember { mutableStateOf(true) }
    var promoNotifs by remember { mutableStateOf(false) }

    var biometricLogin by remember { mutableStateOf(false) }
    var hideSensitiveInfo by remember { mutableStateOf(false) }
    var analyticsSharing by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            item {
                Spacer(Modifier.height(12.dp))
                Text("Theme", style = MaterialTheme.typography.titleMedium)
                ThemeMode.entries.forEach { mode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = themeMode == mode, onClick = { onThemeModeChange(mode) })
                        Text(
                            when (mode) {
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.SYSTEM -> "System Default"
                            },
                            modifier = Modifier.height(48.dp)
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Language", style = MaterialTheme.typography.titleMedium)
                LanguageDropdown(language) { language = it }
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Accessibility", style = MaterialTheme.typography.titleMedium)
                SettingSwitchRow("Large Text", largeText) { largeText = it }
                SettingSwitchRow("High Contrast", highContrast) { highContrast = it }
                SettingSwitchRow("Reduce Animations", reduceAnimations) { reduceAnimations = it }
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Notifications", style = MaterialTheme.typography.titleMedium)
                SettingSwitchRow("Appointment Notifications", appointmentNotifs) { appointmentNotifs = it }
                SettingSwitchRow("Medication Reminders", medicationNotifs) { medicationNotifs = it }
                SettingSwitchRow("Health Tips", healthTipsNotifs) { healthTipsNotifs = it }
                SettingSwitchRow("Laboratory Reports", labReportNotifs) { labReportNotifs = it }
                SettingSwitchRow("Promotional Messages", promoNotifs) { promoNotifs = it }
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Privacy", style = MaterialTheme.typography.titleMedium)
                SettingSwitchRow("Biometric Login (Mock)", biometricLogin) { biometricLogin = it }
                SettingSwitchRow("Hide Sensitive Information", hideSensitiveInfo) { hideSensitiveInfo = it }
                SettingSwitchRow("Analytics Sharing", analyticsSharing) { analyticsSharing = it }
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Permissions", style = MaterialTheme.typography.titleMedium)
                listOf("Camera", "Storage", "Notifications", "Location").forEach {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                        Text("Granted", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                TextButton(onClick = onPrivacyPolicy, modifier = Modifier.fillMaxWidth()) { Text("Privacy Policy") }
                TextButton(onClick = onTerms, modifier = Modifier.fillMaxWidth()) { Text("Terms & Conditions") }
                TextButton(onClick = onAbout, modifier = Modifier.fillMaxWidth()) { Text("About") }
                TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun LanguageDropdown(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("English", "Urdu", "Arabic")
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
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

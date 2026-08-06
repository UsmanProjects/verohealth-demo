package com.veroflow.verohealth.ui.screens.appointment

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.veroflow.verohealth.data.model.Appointment
import com.veroflow.verohealth.data.repository.AppointmentRepository
import com.veroflow.verohealth.data.repository.PaymentResult
import kotlinx.coroutines.launch

enum class PaymentMethod(val label: String) {
    CREDIT_CARD("Credit Card"), DEBIT_CARD("Debit Card"), DIGITAL_WALLET("Digital Wallet"),
    CASH_AT_HOSPITAL("Cash at Hospital"), INSURANCE("Insurance Coverage")
}

/** Screen 13 — Payment. */
@Composable
fun PaymentScreen(
    draftAppointment: Appointment,
    onBack: () -> Unit,
    onCancelPayment: () -> Unit,
    onPaymentSuccess: (Appointment) -> Unit
) {
    var method by remember { mutableStateOf(PaymentMethod.CREDIT_CARD) }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var insuranceProvider by remember { mutableStateOf("") }
    var membershipNumber by remember { mutableStateOf("") }
    var saveInfo by remember { mutableStateOf(false) }

    var errors by remember { mutableStateOf(mapOf<String, String>()) }
    var isProcessing by remember { mutableStateOf(false) }
    var showFailureDialog by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf<Appointment?>(null) }
    val scope = rememberCoroutineScope()

    fun validate(): Boolean {
        val map = mutableMapOf<String, String>()
        when (method) {
            PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> {
                if (cardNumber.replace(" ", "").length < 12) map["card"] = "Enter a valid card number"
                if (cardHolder.isBlank()) map["holder"] = "Cardholder name is required"
                if (!expiry.matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}$"))) map["expiry"] = "Use MM/YY format"
                if (!cvv.matches(Regex("^[0-9]{3,4}$"))) map["cvv"] = "Enter a valid CVV"
            }
            PaymentMethod.INSURANCE -> {
                if (insuranceProvider.isBlank()) map["provider"] = "Insurance provider is required"
                if (membershipNumber.isBlank()) map["membership"] = "Membership number is required"
            }
            else -> {}
        }
        errors = map
        return map.isEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            item {
                Text("Total: Rs. ${"%.0f".format(draftAppointment.totalAmount)}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("Payment Method", style = MaterialTheme.typography.titleMedium)
                PaymentMethod.entries.forEach { m ->
                    Row {
                        RadioButton(selected = method == m, onClick = { method = m })
                        Text(m.label, modifier = Modifier.height(48.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(12.dp))

                when (method) {
                    PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> {
                        OutlinedTextField(
                            value = cardNumber, onValueChange = { cardNumber = it },
                            label = { Text("Card Number") },
                            isError = errors["card"] != null,
                            supportingText = { errors["card"]?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cardHolder, onValueChange = { cardHolder = it },
                            label = { Text("Card Holder Name") },
                            isError = errors["holder"] != null,
                            supportingText = { errors["holder"]?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = expiry, onValueChange = { expiry = it },
                                label = { Text("MM/YY") },
                                isError = errors["expiry"] != null,
                                supportingText = { errors["expiry"]?.let { Text(it) } },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cvv, onValueChange = { cvv = it },
                                label = { Text("CVV") },
                                isError = errors["cvv"] != null,
                                supportingText = { errors["cvv"]?.let { Text(it) } },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    PaymentMethod.INSURANCE -> {
                        OutlinedTextField(
                            value = insuranceProvider, onValueChange = { insuranceProvider = it },
                            label = { Text("Insurance Provider") },
                            isError = errors["provider"] != null,
                            supportingText = { errors["provider"]?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = membershipNumber, onValueChange = { membershipNumber = it },
                            label = { Text("Membership Number") },
                            isError = errors["membership"] != null,
                            supportingText = { errors["membership"]?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {
                        Text(
                            "No additional details needed — settle this at the counter or via your wallet app.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row {
                    Checkbox(checked = saveInfo, onCheckedChange = { saveInfo = it })
                    Text("Save payment information", modifier = Modifier.height(48.dp), style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (validate()) {
                            isProcessing = true
                            scope.launch {
                                val result = AppointmentRepository.processPayment(draftAppointment, cardNumber.ifBlank { "0000000000004242" })
                                isProcessing = false
                                when (result) {
                                    is PaymentResult.Success -> showSuccessDialog = result.appointment
                                    is PaymentResult.Failure -> showFailureDialog = result.message
                                }
                            }
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (isProcessing) CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                    else Text("Pay Now")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onCancelPayment, modifier = Modifier.fillMaxWidth()) { Text("Cancel Payment") }
            }
        }
    }

    showFailureDialog?.let { message ->
        AlertDialog(
            onDismissRequest = { showFailureDialog = null },
            title = { Text("Payment Failed") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { showFailureDialog = null }) { Text("Retry Payment") } },
            dismissButton = { TextButton(onClick = onCancelPayment) { Text("Cancel") } }
        )
    }

    showSuccessDialog?.let { appt ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Payment Successful") },
            text = { Text("Your appointment has been confirmed and paid.") },
            confirmButton = { TextButton(onClick = { onPaymentSuccess(appt) }) { Text("Continue") } }
        )
    }
}
